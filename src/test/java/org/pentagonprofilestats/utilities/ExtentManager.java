package org.pentagonprofilestats.utilities;
import org.pentagonprofilestats.utilities.AIService;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
//import java.net.URL;
import java.net.URL;

//Extent report 5.x...//version

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.qameta.allure.Attachment;
import org.pentagonprofilestats.testBase.BaseTestCase;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;


public class ExtentManager extends BaseTestCase implements ITestListener {
    public ExtentSparkReporter sparkReporter;
    public ExtentReports extent;
    public ExtentTest test;

    String repName;

    public void onStart(ITestContext testContext) {

		/*SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		Date dt=new Date();
		String currentdatetimestamp=df.format(dt);
		*/

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());// time stamp
        repName = "PentagonProfileStats-Test-Report-" + timeStamp + ".html";
        sparkReporter = new ExtentSparkReporter(".\\reports\\" + repName);// specify location of the report

        sparkReporter.config().setDocumentTitle("Pentagon Profile Stats Automation Report"); // Title of report
        sparkReporter.config().setReportName("Pentagon Profile Stats Functional Testing"); // name of the report
        sparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Application", "Pentagon Profile Stats");
        extent.setSystemInfo("Module", "Admin");
        extent.setSystemInfo("Sub Module", "Profile Analysis");
        extent.setSystemInfo("User Name", System.getProperty("user.name"));
        extent.setSystemInfo("Environemnt", "QA");

         String os = testContext.getCurrentXmlTest().getParameter("os");
         extent.setSystemInfo("Operating System", os);

         String browser = testContext.getCurrentXmlTest().getParameter("browser");
         extent.setSystemInfo("Browser", browser);

         List<String> includedGroups = testContext.getCurrentXmlTest().getIncludedGroups();
         if(!includedGroups.isEmpty()) {
             extent.setSystemInfo("Groups", includedGroups.toString());
         }
    }

    public void onTestSuccess(ITestResult result) {

        test = extent.createTest(result.getTestClass().getName());
        test.assignCategory(result.getMethod().getGroups()); // to display groups in report
        test.log(Status.PASS,result.getName()+" got successfully executed");
//        logger.info("*** Test Passed *** | Test Class : '{}' | Test Method : '{}'", result.getTestClass().getTestName(), result.getName());
    }

    public void onTestFailure(ITestResult result) {
        test = extent.createTest(result.getTestClass().getName());
        test.assignCategory(result.getMethod().getGroups());

        test.log(Status.FAIL, result.getName() + " got failed");

        // Log the error message
        Throwable t = result.getThrowable();
        String errorMessage = (t != null) ? t.getMessage() : "No error message";
        test.log(Status.INFO, errorMessage);

        BaseTestCase base = (BaseTestCase) result.getInstance();

        // ================== 🤖 START AI INTEGRATION ==================
        try {
            // 1. Get Page Source safely
            String pageSource = "";
            if (base.driver != null) {
                try {
                    pageSource = base.driver.getPageSource();
                } catch (Exception e) {
                    test.warning("Could not capture page source: " + e.getMessage());
                }
            }

            // 2. Call AI Service
            // We pass the Test Name, the Error, and the Page Source
            String aiAnalysis = AIService.analyzeFailure(result.getName(), t, pageSource);

            // 3. Display in Report as a Code Block for better visibility
            if (aiAnalysis != null && !aiAnalysis.isEmpty()) {
                test.info("<b>🤖 AI Root Cause Analysis:</b>");
                test.info(MarkupHelper.createCodeBlock(aiAnalysis, CodeLanguage.XML));
            } else {
                test.warning("AI Analysis returned no result.");
            }

        } catch (Exception e) {
            // If AI fails (e.g., bad Key), don't break the report, just log a warning
            test.warning("⚠️ AI Analysis failed: " + e.getMessage());
        }
        // ================== 🤖 END AI INTEGRATION ==================

        // Existing Screenshot Logic
        try {
            String imgPath = base.takeScreenshot(result.getName());

            // Allure Screenshot
            byte[] bytes = base.takeScreenshotForAllure();
            io.qameta.allure.Allure.getLifecycle()
                    .addAttachment("Failure Screenshot", "image/png", "png", bytes);

            // Extent Screenshot
            test.addScreenCaptureFromPath(imgPath);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void onTestSkipped(ITestResult result) {
        test = extent.createTest(result.getTestClass().getName());
        test.assignCategory(result.getMethod().getGroups());
        test.log(Status.SKIP, result.getName()+" got skipped");
        test.log(Status.INFO, result.getThrowable().getMessage());
//        logger.info("*** Test Skipped *** | Test Class : '{}' | Test Method : '{}'", result.getTestClass().getTestName(), result.getName());

    }

    public void onFinish(ITestContext testContext) {

        extent.flush();

        String pathOfExtentReport = System.getProperty("user.dir")+"\\reports\\"+repName;
        File extentReport = new File(pathOfExtentReport);

        try {
            Desktop.getDesktop().browse(extentReport.toURI());
        } catch (IOException e) {
            e.printStackTrace();
        }

//        logger.info("*** Report Generated *** | In onFinish");



		/*  try {
			  URL url = new  URL("file:///"+System.getProperty("user.dir")+"\\reports\\"+repName);

		  // Create the email message
		  ImageHtmlEmail email = new ImageHtmlEmail();
		  email.setDataSourceResolver(new DataSourceUrlResolver(url));
		  email.setHostName("smtp.googlemail.com");
		  email.setSmtpPort(465);
		  email.setAuthenticator(new DefaultAuthenticator("pavanoltraining@gmail.com","password"));
		  email.setSSLOnConnect(true);
		  email.setFrom("pavanoltraining@gmail.com"); //Sender
		  email.setSubject("Test Results");
		  email.setMsg("Please find Attached Report....");
		  email.addTo("pavankumar.busyqa@gmail.com"); //Receiver
		  email.attach(url, "extent report", "please check report...");
		  email.send(); // send the email
		  }
		  catch(Exception e)
		  {
			  e.printStackTrace();
			  }
		 */

    }



}



