package fproject;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.apache.logging.log4j.LogManager; // Added for Log4j2
import org.apache.logging.log4j.Logger;     // Added for Log4j2

public class EMICalculatorPage {
    private static final Logger logger = LogManager.getLogger(EMICalculatorPage.class); // Log4j2 Logger

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    private final By menuDropdown = By.xpath("//*[@id=\"menu-item-dropdown-2696\"]");
    private final By homeLoanEMICalculatorLink = By.xpath("//a[text()='Home Loan EMI Calculator']");
    private final By loanEMICalculatorLink=By.xpath("//*[@id=\"menu-item-2423\"]/a");
    private final By emiCalculatorTab = By.id("emi-calc");
    private final By loanAmountInput = By.id("loanamount");
    private final By interestRateInput = By.id("loaninterest");
    private final By loanTenureInput = By.id("loanterm");
    private final By emiValueDisplay = By.xpath("//div[@id='loansummary-emi']//span");
    private final By yearLabel = By.xpath("//*[@id=\"loantermsteps\"]/span[7]/span");
    private final By monthToggle = By.xpath("//*[@id=\"ltermwrapper\"]/div[1]/div/div/div/div/div/label[2]");
    private final By yearToggle =By.xpath("//*[@id=\"ltermwrapper\"]/div[1]/div/div/div/div/div/label[1]");
    private final By monthLabel = By.xpath("//*[@id=\"loantermsteps\"]/span[7]/span");
    private final By year2025 = By.id("year2025");
    private final By firstMonthPrincipal = By.xpath("//*[@id=\"monthyear2025\"]/td/div/table/tbody/tr[1]/td[2]");
    private final By firstMonthInterest = By.xpath("//*[@id=\"monthyear2025\"]/td/div/table/tbody/tr[1]/td[3]");

    // Locators for Loan Amount Calculator Tab
    private final By loanAmountCalculatorTab = By.id("loan-amount-calc");
    private final By emiInputLoanAmountCalc = By.id("loanemi");
    private final By feesInputLoanAmountCalc = By.id("loanfees");
    // Locators for Loan Tenure Calculator Tab
    private final By loanTenureCalculatorTab = By.id("loan-tenure-calc");
    private final By amountInputLoanTenureCalc = By.id("loanamount"); // Re-using loanamount
    private final By emiInputLoanTenureCalc = By.id("loanemi"); // Re-using loanemi
    private final By feesInputLoanTenureCalc = By.id("loanfees"); // Re-using loanfees

    // Locators for Table Extraction
    private final By yearHeader = By.id("yearheader");


    public EMICalculatorPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.js = (JavascriptExecutor) driver;
        logger.info("EMICalculatorPage initialized.");
    }

    public void navigateToEMICalculator(String url) {
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(loanAmountInput));
        logger.info("Navigated to URL: " + url);
    }

    public void clickHomeLoanEMICalculator() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(menuDropdown)).click();
        logger.info("Clicked menu dropdown.");
        wait.until(ExpectedConditions.visibilityOfElementLocated(homeLoanEMICalculatorLink)).click();
        logger.info("Clicked 'Home Loan EMI Calculator' link.");
    }
    public void clickLoanEMICalculator() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(menuDropdown)).click();
        logger.info("Clicked menu dropdown.");
        wait.until(ExpectedConditions.visibilityOfElementLocated(loanEMICalculatorLink)).click();
        logger.info("Clicked 'Loan EMI Calculator' link.");
    }

    public void clickEMICalculatorTab() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(emiCalculatorTab)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(loanAmountInput));
        logger.info("Clicked 'EMI Calculator' tab.");
    }

    public void enterLoanAmount(String amount) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(loanAmountInput));
        element.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        element.sendKeys(amount);
        element.sendKeys(Keys.RETURN);
        logger.info("Entered loan amount: " + amount);
    }

    public String getLoanAmountValue() {
        String value = wait.until(ExpectedConditions.visibilityOfElementLocated(loanAmountInput)).getAttribute("value");
        logger.info("Retrieved loan amount value: " + value);
        return value;
    }

    public void enterInterestRate(String rate) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(interestRateInput));
        element.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        element.sendKeys(rate);
        element.sendKeys(Keys.RETURN);
        logger.info("Entered interest rate: " + rate);
    }

    public String getInterestRateValue() {
        String value = wait.until(ExpectedConditions.visibilityOfElementLocated(interestRateInput)).getAttribute("value");
        logger.info("Retrieved interest rate value: " + value);
        return value;
    }

    public void enterLoanTenure(String tenure) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(loanTenureInput));
        element.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        element.sendKeys(tenure);
        element.sendKeys(Keys.RETURN);
        logger.info("Entered loan tenure: " + tenure);
    }

    public String getLoanTenureValue() {
        String value = wait.until(ExpectedConditions.visibilityOfElementLocated(loanTenureInput)).getAttribute("value");
        logger.info("Retrieved loan tenure value: " + value);
        return value;
    }

    public String getEMICalculatedValue() {
        WebElement emi = wait.until(ExpectedConditions.visibilityOfElementLocated(emiValueDisplay));
        js.executeScript("arguments[0].scrollIntoView(true);", emi);
        String value = emi.getText();
        logger.info("Retrieved calculated EMI value: " + value);
        return value;
    }

    public void moveSlider(By sliderLocator) {
        WebElement slider = wait.until(ExpectedConditions.visibilityOfElementLocated(sliderLocator));
        new Actions(driver).clickAndHold(slider).moveByOffset(30, 0).release().perform();
        logger.info("Moved slider for locator: " + sliderLocator);
    }

    public String getTenureMaxYear() {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(yearLabel)).getText();
        logger.info("Retrieved max tenure in years: " + text);
        return text;
    }
    public String getTenureMaxMonth() {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(monthLabel)).getText();
        logger.info("Retrieved max tenure in months: " + text);
        return text;
    }

    public void toggleTenureToMonth() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(monthToggle)).click();
        logger.info("Toggled tenure to months.");
    }
    public void toggleTenureToYear() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(yearToggle)).click();
        logger.info("Toggled tenure to years.");
    }

    public void clickLoanAmountCalculatorTab() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(loanAmountCalculatorTab)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(emiInputLoanAmountCalc));
        logger.info("Clicked 'Loan Amount Calculator' tab.");
    }

    public void enterEMIForLoanAmountCalc(String emi) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(emiInputLoanAmountCalc));
        element.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        element.sendKeys(emi);
        element.sendKeys(Keys.RETURN);
        logger.info("Entered EMI for loan amount calc: " + emi);
    }

    public String getEMIValueForLoanAmountCalc() {
        String value = wait.until(ExpectedConditions.visibilityOfElementLocated(emiInputLoanAmountCalc)).getAttribute("value");
        logger.info("Retrieved EMI value for loan amount calc: " + value);
        return value;
    }

    public void enterFeesForLoanAmountCalc(String fees) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(feesInputLoanAmountCalc));
        element.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        element.sendKeys(fees);
        element.sendKeys(Keys.RETURN);
        logger.info("Entered fees for loan amount calc: " + fees);
    }

    public String getFeesValueForLoanAmountCalc() {
        String value = wait.until(ExpectedConditions.visibilityOfElementLocated(feesInputLoanAmountCalc)).getAttribute("value");
        logger.info("Retrieved fees value for loan amount calc: " + value);
        return value;
    }

    public void clickLoanTenureCalculatorTab() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(loanTenureCalculatorTab)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(amountInputLoanTenureCalc));
        logger.info("Clicked 'Loan Tenure Calculator' tab.");
    }

    public void enterAmountForLoanTenureCalc(String amount) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(amountInputLoanTenureCalc));
        element.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        element.sendKeys(amount);
        element.sendKeys(Keys.RETURN);
        logger.info("Entered amount for loan tenure calc: " + amount);
    }

    public String getAmountValueForLoanTenureCalc() {
        String value = wait.until(ExpectedConditions.visibilityOfElementLocated(amountInputLoanTenureCalc)).getAttribute("value");
        logger.info("Retrieved amount value for loan tenure calc: " + value);
        return value;
    }

    public void enterEMIForLoanTenureCalc(String emi) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(emiInputLoanTenureCalc));
        element.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        element.sendKeys(emi);
        element.sendKeys(Keys.RETURN);
        logger.info("Entered EMI for loan tenure calc: " + emi);
    }

    public String getEMIValueForLoanTenureCalc() {
        String value = wait.until(ExpectedConditions.visibilityOfElementLocated(emiInputLoanTenureCalc)).getAttribute("value");
        logger.info("Retrieved EMI value for loan tenure calc: " + value);
        return value;
    }

    public void enterFeesForLoanTenureCalc(String fees) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(feesInputLoanTenureCalc));
        element.sendKeys(Keys.CONTROL + "a", Keys.DELETE);
        element.sendKeys(fees);
        element.sendKeys(Keys.RETURN);
        logger.info("Entered fees for loan tenure calc: " + fees);
    }

    public String getFeesValueForLoanTenureCalc() {
        String value = wait.until(ExpectedConditions.visibilityOfElementLocated(feesInputLoanTenureCalc)).getAttribute("value");
        logger.info("Retrieved fees value for loan tenure calc: " + value);
        return value;
    }

    public void clickCarLoanLink() {
        driver.findElement(By.xpath("//*[@id=\"car-loan\"]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(loanAmountInput));
        logger.info("Clicked 'Car Loan' link.");
    }

    public void scrollIntoViewYear2025() {
        WebElement year = wait.until(ExpectedConditions.visibilityOfElementLocated(year2025));
        js.executeScript("arguments[0].scrollIntoView();", year);
        year.click();
        logger.info("Scrolled into view and clicked year 2025.");
    }

    public String getFirstMonthPrincipal() {
        String principal = driver.findElement(firstMonthPrincipal).getText();
        logger.info("Retrieved first month principal: " + principal);
        return principal;
    }

    public String getFirstMonthInterest() {
        String interest = driver.findElement(firstMonthInterest).getText();
        logger.info("Retrieved first month interest: " + interest);
        return interest;
    }

    public void scrollIntoViewYearHeader() {
        WebElement year = wait.until(ExpectedConditions.visibilityOfElementLocated(yearHeader));
        js.executeScript("arguments[0].scrollIntoView();", year);
        logger.info("Scrolled into view year header.");
    }

    public int getPaymentScheduleColumnCount() {
        int count = driver.findElements(By.xpath("//*[@id=\"paymentschedule\"]/table/tbody/tr[2]/td")).size();
        logger.info("Payment schedule column count: " + count);
        return count;
    }

    public int getPaymentScheduleRowCount() {
        int count = driver.findElements(By.xpath("//*[@id=\"paymentschedule\"]/table/tbody/tr/td[2]")).size();
        logger.info("Payment schedule row count: " + count);
        return count;
    }

    public String getPaymentScheduleHeaderValue(int column) {
        String value = driver.findElement(By.xpath("//*[@id=\"paymentschedule\"]//tbody/tr/th[" + column + "]")).getText();
        logger.info("Payment schedule header value for column " + column + ": " + value);
        return value;
    }

    public String getPaymentScheduleCellValue(int row, int column) {
        String value = driver.findElement(By.xpath("//*[@id=\"paymentschedule\"]/table/tbody/tr[" + row + "]/td[" + column + "]")).getText();
        logger.info("Payment schedule cell value at row " + row /2+ ", column " + column + ": " + value);
        return value;
    }
}