package org.pentagonprofilestats.testCases;

import org.openqa.selenium.Alert;

import org.pentagonprofilestats.pageObjects.PPSHomePageObjects;
import org.pentagonprofilestats.testBase.BaseTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.qameta.allure.*;
public class PPS01TestCaseNameField extends BaseTestCase {

    PPSHomePageObjects homeObj;
    @BeforeClass(groups = {"smoke", "functional", "positive", "negative", "name"})
    public void initializeObject(){
        homeObj = getPPPHomePageObjects();
    }

    @Test(priority = 1 ,groups = {"smoke", "functional", "positive", "name"} ,dataProvider = "validNamesDataProvider",
            dataProviderClass = org.pentagonprofilestats.utilities.DataProviders.class)
    @Description("Validating names in the Name field")
    @Severity(SeverityLevel.MINOR)
    public void validName(String userName){
        logger.info("Test Started : validName | userName Entered ='{}'", userName);
        homeObj.setUserName(userName);
        try {
            Assert.assertEquals(homeObj.getUserName(), userName);

        }catch (AssertionError e){
            Assert.fail("Name not valid");

        }
    }

    @Test(priority = 2 ,groups = {"smoke", "functional", "negative", "name"} ,dataProvider = "invalidNamesDataProvider",
            dataProviderClass = org.pentagonprofilestats.utilities.DataProviders.class)
    public void invalidName(String userName){
        homeObj.setUserName(userName);
        try{
            Assert.assertEquals(homeObj.getUserName(),"");
        }catch (AssertionError e){
            Assert.fail("userName should only contains Alphabets");
        }
    }



    @Test(priority = 3,groups = {"functional", "negative", "name", "alert"} ,dataProvider = "EmptyNameDataProvider",
            dataProviderClass = org.pentagonprofilestats.utilities.DataProviders.class)
    @Description("Validating the Alert for Empty Name")

    public void emptyName(String name, String experience, String skills,
                          String websitesDeveloped, String appsMade) throws InterruptedException {
        homeObj.setUserName(name)
                .setYearsOfExperience(experience)
                .setSkills(skills)
                .setWebsitesDeveloped(websitesDeveloped)
                .setAppsMade(appsMade)
                .clickTrack();
        Alert nameRequiredAlert = homeObj.switchToAlert();
        String alertMessage = nameRequiredAlert.getText();
        nameRequiredAlert.accept();
        Assert.assertEquals(alertMessage,"Name required!");
    }






}
