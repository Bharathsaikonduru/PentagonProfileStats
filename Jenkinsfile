pipeline {
    agent any

    tools {
        // Ensures Maven and JDK are available in Jenkins
        maven 'Maven-3.9.6'   // Name of Maven installation in Jenkins
        jdk 'MyJDK'          // Name of JDK installation in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                // Pull code from GitHub
                git branch: 'dhruv',
                    url: 'https://github.com/Bharathsaikonduru/PentagonProfileStats.git',
                    credentialsId: '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'
            }
        }

        stage('Build') {
            steps {
                // Clean and compile project
                sh 'mvn clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                // Run TestNG tests
                sh 'mvn clean test'
            }
            post {
                always {
                    // Archive test results (JUnit XMLs)
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Generate Reports') {
            steps {
                // Generate Allure report
                sh 'allure generate allure-report'
                sh 'allure open allure-report'

                // Archive Extent report (HTML)
                archiveArtifacts artifacts: 'target/extent-reports/*.html', fingerprint: true

                // Archive Allure report (HTML)
                archiveArtifacts artifacts: 'target/site/allure-maven-plugin/**', fingerprint: true
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished. Reports archived.'
        }
    }
}
