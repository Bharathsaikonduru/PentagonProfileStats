package org.pentagonprofilestats.utilities;


import lombok.Data;
import org.testng.annotations.DataProvider;

import java.io.IOException;

public class DataProviders {

    ExcelDataProvider excelDataProvider = new ExcelDataProvider();

    @DataProvider(name = "validNamesDataProvider")
    public Object[][] validNames() throws IOException {
        Object[][] validNamesData = excelDataProvider
                .getData("testData/DataToValidateNameField.xlsx", "Sheet1");
        return validNamesData;
    }

    @DataProvider(name = "invalidNamesDataProvider")
    public Object[][] invalidName() throws Exception {
        Object[][] invalidNameData = excelDataProvider
                .getData("testData/DataToValidateNameFieldNegativeTestCase.xlsx", "Sheet1");
        return invalidNameData;
    }

    @DataProvider(name = "EmptyNameDataProvider")
    public Object[][] emptyName() throws IOException {
        Object[][] emptyName = excelDataProvider
                .getData("testData/NegativeTCDataToValidateNameField.xlsx", "Sheet1");
        return emptyName;
    }

    @DataProvider(name = "yearsOfExperienceDataProvider")
    public Object[][] dataForYearsOfExperienceValidation() throws IOException {
        Object[][] yearsOfExperienceData = excelDataProvider
                .getData("testData/YearsOfExperienceOptionsValidation.xlsx", "Sheet1");
        return yearsOfExperienceData;
    }

    @DataProvider(name = "skillsDataProvider")
    public Object[][] dataForSkillsField() throws IOException {
        Object[][] skillsDataWithStartingCharacter = excelDataProvider
                .getData("testData/SkillsPopulate.xlsx", "Sheet1");
        return skillsDataWithStartingCharacter;
    }

    @DataProvider(name = "skillsNegativeDataProvider")
    public Object[][] dataForSkillsFieldNegative() throws IOException {
        Object[][] skillsDataWithStartingCharacterNegative = excelDataProvider
                .getData("testData/SkillsPopulateNegative.xlsx", "Sheet1");
        return skillsDataWithStartingCharacterNegative;
    }

    @DataProvider(name = "websitesDevelopedDataProvider")
    public Object[][] dataForWebsitesDeveloped(){
        Object[][] websitesDevelopedData = {
                {"12"},
                {"81279817283712897398"},
                {"98273487238947982349826464267283981273981273817283712371283719830"},
                {"1000147798179217179371987846716478164124981720941094710948091740917498170941704710710948710989084984097047984718481748174071047"},
                {"8718471847042878748923748273872387893748374827410941809481094109409409348908409849023849234273847310430948930489074978748747098190481098128101111111184912912749174784718471982"}
        };
        return websitesDevelopedData;
    }

    @DataProvider(name = "websitesDevelopedNegativeTestCaseDataProvider")
    public Object[][] dataForNegativeTestCaseWebsitesDeveloped() throws IOException {
        Object[][] websitesDevelopedData = excelDataProvider
                .getData("testData/WebsitesDevelopedNegative.xlsx", "Sheet1");
        return websitesDevelopedData;
    }

    @DataProvider(name = "appsMadeDataProvider")
    public Object[][] dataForAppsMade(){
        Object[][] AppsMadeData = {
                {"12"},
                {"81279817283712897398"},
                {"98273487238947982349826464267283981273981273817283712371283719830"},
                {"1000147798179217179371987846716478164124981720941094710948091740917498170941704710710948710989084984097047984718481748174071047"},
                {"8718471847042878748923748273872387893748374827410941809481094109409409348908409849023849234273847310430948930489074978748747098190481098128101111111184912912749174784718471982"}
        };
        return AppsMadeData;
    }

    @DataProvider(name = "appsMadeNegativeTestCaseDataProvider")
    public Object[][] dataForNegativeTestCaseAppsMade() throws IOException {
        Object[][] AppsMadeData = excelDataProvider
                .getData("testData/AppsMadeNegative.xlsx", "Sheet1");
        return AppsMadeData;
    }

    @DataProvider(name = "profileAnalysisDataProvider")
    public Object[][] dataForProfileAnalysis() throws Exception {
        Object[][] ProfileAnalysisData = excelDataProvider
                .getData("testData/ProfileAnalysisData.xlsx", "Sheet1");
        return ProfileAnalysisData;
    }

}


