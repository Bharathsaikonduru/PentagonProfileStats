package org.pentagonprofilestats.testBase;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.pentagonprofilestats.pageObjects.PPSHomePageObjects;
import org.pentagonprofilestats.pageObjects.PPSResultDisplayPageObjects;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


public class BaseTestCase {
    public WebDriver driver;
    public Logger logger;
    public ExtentReports extent;
    public ExtentTest test;
    Properties properties;


    @BeforeClass(groups = {"smoke", "functional", "positive", "negative", "name"})
    @Parameters({"os","browser"})
    public void driverSetup(String os, String browser) throws IOException {
        FileReader file = new FileReader("src/test/resources/config.properties");
        properties = new Properties();
        properties.load(file);

        logger = LogManager.getLogger(this.getClass());

        if(properties.getProperty("execution_env").equalsIgnoreCase("remote")){
            DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
            if(os.equalsIgnoreCase("windows")){
                desiredCapabilities.setPlatform(Platform.WIN11);
                logger.info("Class : BaseTestCase | Method : driverSetup | In Remote Execution (Grid) | Selected OS Platform : '{}'", os);
            }else if(os.equalsIgnoreCase("mac")){
                desiredCapabilities.setPlatform(Platform.MAC);
                logger.info("Class : BaseTestCase | Method : driverSetup | In Remote Execution (Grid) | Selected OS Platform : '{}'", os);
            }else{
                logger.error("Class : BaseTestCase | Method : driverSetup | In Remote Execution (Grid) | ******** No Matching Operating System Found ********");
                return;
            }

            switch (browser.toLowerCase()){
                case "chrome" :
                    desiredCapabilities.setBrowserName("chrome");
                    logger.info("Class : BaseTestCase | Method : driverSetup | In Remote Execution (Grid) | Selected Browser : '{}'", browser);
                    break;
                case "edge" :
                    desiredCapabilities.setBrowserName("MicrosoftEdge");
                    logger.info("Class : BaseTestCase | Method : driverSetup | In Remote Execution (Grid) | Selected Browser : '{}'", browser);
                    break;
                default:
                    logger.info("Class : BaseTestCase | Method : driverSetup | In Remote Execution (Grid) | ******** No Matching Browser Found ********");
                    return;

            }

            driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), desiredCapabilities);

        }
        else if(properties.getProperty("execution_env").equalsIgnoreCase("local")){
            switch (browser.toLowerCase()){
                case "chrome" :
                    driver = new ChromeDriver();
                    logger.info("Class : BaseTestCase | Method : driverSetup | In Local Execution | Selected Browser : '{}'", browser);
                    break;
                case "edge" :
                    driver = new EdgeDriver();
                    logger.info("Class : BaseTestCase | Method : driverSetup | In Local Execution | Selected Browser : '{}'", browser);
                    break;
                case "firefox" :
                    driver = new FirefoxDriver();
                    logger.info("Class : BaseTestCase | Method : driverSetup | In Local Execution | Selected Browser : '{}'", browser);
                    break;
                default:
                    logger.info("Class : BaseTestCase | Method : driverSetup | In Local Execution | ******** No Matching Browser Found ********");
                    return;

            }
        }
        driver.get("https://kashishbarnwal2611.github.io/PentagonProfileStats/");
        driver.manage().window().maximize();
    }

    @BeforeMethod(groups = {"smoke", "functional", "positive", "negative", "name"})
    public void refresh(){
        driver.navigate().refresh();
    }


    public PPSHomePageObjects getPPPHomePageObjects(){
        return new PPSHomePageObjects(driver, 10);
    }

    public PPSResultDisplayPageObjects getPPSResultDisplayPageObjects(){
        return new PPSResultDisplayPageObjects(driver, 10);
    }


    @AfterClass(groups = {"smoke", "functional", "positive", "negative", "name"})
    public void close(){
        if(driver != null){
            driver.quit();
        }
        if(extent!=null){
            extent.flush();;
        }
    }



    @Attachment(value = "Screenshot", type = "image/png", fileExtension = "png")
    public byte[] takeScreenshotForAllure(){
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }



    public String takeScreenshot(String screenshotName){
        String dateName = new java.text.SimpleDateFormat("yyyyMMddhhmmss").format(new java.util.Date());
        TakesScreenshot ss = (TakesScreenshot) driver;
        File source = ss.getScreenshotAs(OutputType.FILE);

        String destination = System.getProperty("user.dir") + "/screenshots/" + screenshotName + "_" + dateName + ".png";
        File finalDestination = new File(destination);

        try{
            FileUtils.copyFile(source, finalDestination);
//            System.out.println("ScreenShot saved: "+destination);
            if(test!=null){
                test.addScreenCaptureFromPath(destination);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return destination;
    }



//    @BeforeClass(groups = {"smoke", "functional", "positive", "negative", "name"})
//    @Parameters({"os", "browser", "headless"})
//    public void driverSetup(String os, String browser, @Optional String headlessParam) throws IOException {
//
//        FileReader file = new FileReader("src/test/resources/config.properties");
//        properties = new Properties();
//        properties.load(file);
//
//        logger = LogManager.getLogger(this.getClass());
//
//        // Determine headless flag: TestNG parameter overrides properties
//        boolean isHeadless = false;
//        if (headlessParam != null && !headlessParam.isBlank()) {
//            isHeadless = Boolean.parseBoolean(headlessParam.trim());
//        } else if (properties.getProperty("headless") != null) {
//            isHeadless = Boolean.parseBoolean(properties.getProperty("headless").trim());
//        }
//
//        // Helper: common args for Chromium in headless
//        List<String> commonHeadlessArgs = Arrays.asList(
//                "--headless=new",           // use new headless implementation for Chromium-based browsers
//                "--disable-gpu",            // harmless on Linux; keeps parity with older setups
//                "--window-size=1920,1080",  // ensures consistent layout in headless mode
//                "--no-sandbox",             // often needed in CI containers
//                "--disable-dev-shm-usage"   // avoid /dev/shm issues in containers
//        );
//
//        if (properties.getProperty("execution_env").equalsIgnoreCase("remote")) {
//            // --- Remote (Selenium Grid) ---
//            logger.info("Class : BaseTestCase | Method : driverSetup | In Remote Execution (Grid)");
//
//            // Normalize OS logs and (optionally) set platform if you need it
//            if (os.equalsIgnoreCase("windows")) {
//                logger.info("Selected OS Platform : '{}'", os);
//                // desired: use options.setPlatformName("windows")
//            } else if (os.equalsIgnoreCase("mac")) {
//                logger.info("Selected OS Platform : '{}'", os);
//                // desired: use options.setPlatformName("mac")
//            } else {
//                logger.error("******** No Matching Operating System Found ********");
//                return;
//            }
//
//            switch (browser.toLowerCase()) {
//                case "chrome": {
//                    ChromeOptions options = new ChromeOptions();
//                    if (isHeadless) options.addArguments(commonHeadlessArgs);
//                    // optionally pin version/caps for Grid:
//                    // options.setBrowserVersion("stable");
//                    logger.info("Selected Browser : '{}' | Headless: {}", browser, isHeadless);
//                    driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);
//                    break;
//                }
//                case "edge": {
//                    EdgeOptions options = new EdgeOptions();
//                    if (isHeadless) options.addArguments(commonHeadlessArgs);
//                    logger.info("Selected Browser : '{}' | Headless: {}", browser, isHeadless);
//                    driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);
//                    break;
//                }
//                case "firefox": {
//                    FirefoxOptions options = new FirefoxOptions();
//                    if (isHeadless) {
//                        options.addArguments("-headless");
//                        // Set a window size explicitly for Gecko headless
//                        options.addArguments("--width=1920");
//                        options.addArguments("--height=1080");
//                    }
//                    logger.info("Selected Browser : '{}' | Headless: {}", browser, isHeadless);
//                    driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), options);
//                    break;
//                }
//                default:
//                    logger.error("******** No Matching Browser Found ********");
//                    return;
//            }
//
//        } else if (properties.getProperty("execution_env").equalsIgnoreCase("local")) {
//            // --- Local ---
//            switch (browser.toLowerCase()) {
//                case "chrome": {
//                    ChromeOptions options = new ChromeOptions();
//                    if (isHeadless) options.addArguments(commonHeadlessArgs);
//                    driver = new ChromeDriver(options);
//                    logger.info("In Local Execution | Selected Browser : '{}' | Headless: {}", browser, isHeadless);
//                    break;
//                }
//                case "edge": {
//                    EdgeOptions options = new EdgeOptions();
//                    if (isHeadless) options.addArguments(commonHeadlessArgs);
//                    driver = new EdgeDriver(options);
//                    logger.info("In Local Execution | Selected Browser : '{}' | Headless: {}", browser, isHeadless);
//                    break;
//                }
//                case "firefox": {
//                    FirefoxOptions options = new FirefoxOptions();
//                    if (isHeadless) {
//                        options.addArguments("-headless");
//                        options.addArguments("--width=1920");
//                        options.addArguments("--height=1080");
//                    }
//                    driver = new FirefoxDriver(options);
//                    logger.info("In Local Execution | Selected Browser : '{}' | Headless: {}", browser, isHeadless);
//                    break;
//                }
//                default:
//                    logger.error("In Local Execution | ******** No Matching Browser Found ********");
//                    return;
//            }
//        } else {
//            logger.error("******** Unknown execution_env in properties. Use 'local' or 'remote'. ********");
//            return;
//        }
//
//        driver.get("https://kashishbarnwal2611.github.io/PentagonProfileStats/");
//        driver.manage().window().maximize(); // still fine; in headless, acts on virtual window size
//    }


}
