package org.pentagonprofilestats.pageObjects;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import org.openqa.selenium.support.ui.Select;
import org.pentagonprofilestats.basePageObjects.BasePageObjectsUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PPSHomePageObjects extends BasePageObjectsUtil {

    public PPSHomePageObjects(WebDriver driver,long timeouts){
        super(driver, timeouts);
    }

    @FindBy(xpath = "//div[@class='header']/h1")
    private WebElement headerTitle;

    @FindBy(xpath = "//div[@class='details']/label[text()='Name:']")
    private WebElement nameLabel;

    @FindBy(xpath = "//div[@class='details']/label[text()='Years of Experience:']")
    private WebElement yearsOfExperienceLabel;

    @FindBy(xpath = "//div[@class='autocomplete-box']/label[text()='Skills:']")
    private WebElement skillsLabel;

    @FindBy(xpath = "//div[@class='details']/label[text()='Websites Developed:']")
    private WebElement websitesDevelopedLabel;

    @FindBy(xpath = "//div[@class='details']/label[text()='Apps Made:']")
    private WebElement appsMadeLabel;

//    --------------------

    @FindBy(id="name")
    WebElement userName;

    @FindBy(id="experience")
    WebElement yearsOfExperience;

    @FindBy(id="skills")
    WebElement skills;

    @FindBy(id="websites")
    WebElement websitesDeveloped;

    @FindBy(id="apps")
    WebElement appsMade;

    @FindBy(id="measureBtn")
    WebElement trackButton;

    @FindBy(xpath="//div[@id='skillsContainer']/div[@class='skills']")
    List<WebElement> skillsSelectedElements;

    @FindBy(xpath = "//select[@id='experience']/option[text()!='Select']")
    List<WebElement> yearsOfExperienceDropDownList;

    @FindBy(xpath="//div[@id='suggestionsList']/div[@class='suggestions']")
    List<WebElement> skillsPopulatedElements;


    private Select select;

    private String[] skillsArray;

    private List<String> skillsSelected;

    private List<String> experienceDropDownList;

    private List<String> skillsPopulated;

    private List<String> actualExperienceDropDownList = Arrays.asList("0 Years","1 Year","2 Years","3 Years","4 Years","5+ Years");


    public String[] getSkillsArray(){
        return skillsArray;
    }

    //Getting data from application
    public List<String> getExperienceDropDownList(){
        experienceDropDownList = new ArrayList<>();
        for(WebElement experienceList : yearsOfExperienceDropDownList){
            experienceDropDownList.add(experienceList.getText());
        }
        return experienceDropDownList;
    }

    public String getUserName(){
        WebElement nameElement = waitVisible(this.userName);
        String name = nameElement.getAttribute("value");
        return name;
    }

    public String getYearsOfExperience(){
        waitVisible(this.yearsOfExperience);
        String experience = select.getFirstSelectedOption().getText();
        return experience;
    }

    public List<String> getSkillsSelected(){
        skillsSelected = new ArrayList<>();
        for(WebElement skillElement : skillsSelectedElements){
            skillsSelected.add(skillElement.getText());
        }
        return skillsSelected;
    }

    public String getWebSitesDeveloped(){
        WebElement websitesDevelopedElement = waitVisible(websitesDeveloped);
        String noOfWebsitesDeveloped = websitesDevelopedElement.getAttribute("value");
        return noOfWebsitesDeveloped;
    }

    public String getAppsMade(){
        WebElement appsMadeElement = waitVisible(appsMade);
        String noOfAppsMade = appsMadeElement.getAttribute("value");
        return noOfAppsMade;
    }

    public List<String> getSkillsPopulated(){
        skillsPopulated = new ArrayList<>();
        for(WebElement skill : skillsPopulatedElements){
            skillsPopulated.add(skill.getText());
        }
        return skillsPopulated;
    }

    public void enterStartingCharacterofSkillInSkillsField(String startingCharacter){
        WebElement skillsPopulationTest = waitVisible(this.skills);
        skillsPopulationTest.clear();
        skillsPopulationTest.sendKeys(startingCharacter);
    }


    public PPSHomePageObjects setUserName(String userName){
        WebElement name = waitVisible(this.userName);
        name.clear();
        name.sendKeys(userName);
        return this;
    }

    public PPSHomePageObjects setYearsOfExperience(String yearsOfExperience){
        WebElement experienceInYears = waitVisible(this.yearsOfExperience);
        select = new Select(experienceInYears);
        select.selectByVisibleText(yearsOfExperience);
        return this;
    }

    public PPSHomePageObjects setSkills(String commaSeparatedSkills){
        skillsArray = commaSeparatedSkills.split(",");
        for (String skill : skillsArray) {
            String trimmedSkill = skill.trim();
            waitClickable(skills);
            skills.clear();
            skills.sendKeys(trimmedSkill);
            By suggestionLocator = By.xpath("//div[@id='suggestionsList']//div[text()='" + trimmedSkill + "']");
            WebElement suggestion = waitClickable(suggestionLocator);
            suggestion.click();
        }
        return this;
    }

    public PPSHomePageObjects setWebsitesDeveloped(String websitesDeveloped){
        WebElement noOfWebsitesDeveloped = waitVisible(this.websitesDeveloped);
        noOfWebsitesDeveloped.clear();
        noOfWebsitesDeveloped.sendKeys(String.valueOf(websitesDeveloped));
        return this;
    }

    public PPSHomePageObjects setAppsMade(String appsMade){
        WebElement noOfAppsMade = waitVisible(this.appsMade);
        noOfAppsMade.clear();
        noOfAppsMade.sendKeys(String.valueOf(appsMade));
        return this;
    }

    public PPSHomePageObjects clickTrack(){
        waitClickable(trackButton).click();
        return this;
    }

    public void clickYearsOfExperience(){
        yearsOfExperience.click();
    }

    public List<String> getActualExperienceDropDownList() {
        return actualExperienceDropDownList;
    }

    public void setActualExperienceDropDownList(List<String> actualExperienceDropDownList) {
        this.actualExperienceDropDownList = actualExperienceDropDownList;
    }

    public Alert switchToAlert(){
        Alert nameRequiredAlert = driver.switchTo().alert();
        return nameRequiredAlert;
    }

    // ---------- Header getters ----------
    public boolean isHeaderDisplayed() { return headerTitle.isDisplayed(); }
    public String getHeaderText() { return headerTitle.getText(); }

    // ---------- Labels: visibility + text ----------
    public boolean isNameLabelDisplayed() { return nameLabel.isDisplayed(); }
    public String getNameLabelText() { return nameLabel.getText(); }

    public boolean isYearsOfExperienceLabelDisplayed() { return yearsOfExperienceLabel.isDisplayed(); }
    public String getYearsOfExperienceLabelText() { return yearsOfExperienceLabel.getText(); }

    public boolean isSkillsLabelDisplayed() { return skillsLabel.isDisplayed(); }
    public String getSkillsLabelText() { return skillsLabel.getText(); }

    public boolean isWebsitesDevelopedLabelDisplayed() { return websitesDevelopedLabel.isDisplayed(); }
    public String getWebsitesDevelopedLabelText() { return websitesDevelopedLabel.getText(); }

    public boolean isAppsMadeLabelDisplayed() { return appsMadeLabel.isDisplayed(); }
    public String getAppsMadeLabelText() { return appsMadeLabel.getText(); }

    // ---------- Inputs visibility ----------
    public boolean isUserNameInputDisplayed() { return userName.isDisplayed(); }
    public boolean isYearsOfExperienceSelectDisplayed() { return yearsOfExperience.isDisplayed(); }
    public boolean isSkillsInputDisplayed() { return skills.isDisplayed(); }
    public boolean isWebsitesDevelopedInputDisplayed() { return websitesDeveloped.isDisplayed(); }
    public boolean isAppsMadeInputDisplayed() { return appsMade.isDisplayed(); }
    public boolean isTrackButtonDisplayed() { return trackButton.isDisplayed(); }

}
