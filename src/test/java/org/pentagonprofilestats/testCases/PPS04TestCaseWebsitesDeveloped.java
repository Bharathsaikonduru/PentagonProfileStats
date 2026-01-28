package org.pentagonprofilestats.testCases;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.pentagonprofilestats.pageObjects.PPSHomePageObjects;
import org.pentagonprofilestats.testBase.BaseTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PPS04TestCaseWebsitesDeveloped extends BaseTestCase {

    PPSHomePageObjects homeObj;

    @BeforeClass(groups = {"smoke", "functional", "positive", "negative", "name"})
    public void initialize(){
        homeObj = getPPPHomePageObjects();
    }

    @Test(priority = 1 ,groups = {"functional", "positive", "websites"} ,dataProvider = "websitesDevelopedDataProvider", dataProviderClass = org.pentagonprofilestats.utilities.DataProviders.class)
    public void validateWebsitesDeveloped(String noOfWebsitesDeveloped){
        homeObj.setWebsitesDeveloped(noOfWebsitesDeveloped);
        Assert.assertEquals(homeObj.getWebSitesDeveloped(), noOfWebsitesDeveloped);
    }

    @Test(priority = 2 ,groups = {"functional", "negative", "websites"} ,dataProvider = "websitesDevelopedNegativeTestCaseDataProvider", dataProviderClass = org.pentagonprofilestats.utilities.DataProviders.class)
    @Description("Negative Numerics are not allowed but it is accepting")
    @Severity(SeverityLevel.CRITICAL)
    public void validateNegativeTestCasesOfWebsitesDevelopedField(String noOfWebsitesDeveloped){
        try{
            homeObj.setWebsitesDeveloped(noOfWebsitesDeveloped);
            takeScreenshotForAllure();
            Assert.assertEquals(homeObj.getWebSitesDeveloped(), "", "Should only accept positive numeric value");
        }catch(AssertionError e){
            logger.info("Test Failed | TestCase : PPS04TestCaseWebsitesDeveloped | Test Method : validateNegativeTestCasesOfWebsitesDevelopedField | Negative numeric value is not accepted as expected");
            Assert.fail("Should only accept positive numeric value");
        }

    }


}
