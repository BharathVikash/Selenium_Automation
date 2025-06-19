package fproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration; // Import Duration
import java.util.Properties;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import org.apache.logging.log4j.LogManager; // Added for Log4j2
import org.apache.logging.log4j.Logger;     // Added for Log4j2


public class EMICalculatorTests {

    private static final Logger fileLogger = LogManager.getLogger(EMICalculatorTests.class); // Log4j2 Logger
    private WebDriver driver;
    private ExtentReports extent;
    private ExtentTest extentLogger; // Renamed from 'logger' to avoid confusion with Log4j2 logger
    private EMICalculatorPage emiCalculatorPage;
    private String url;
    private String loanAmount;
    private String interestRate;
    private String loanTenure;
    private String emi;
    private String fees;

    @BeforeSuite
    public void setupReport() {
        extent = ExtentReporterSetup.setupExtentReport();
        fileLogger.info("Extent Report setup complete.");
    }

    @BeforeClass
    @Parameters({"browser"})
    public void setupDriver(String browser) throws IOException {

        Properties properties=new Properties();

        FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir")+"/Resources/Config.properties");
        properties.load(fileInputStream);
        fileLogger.info("Loaded configuration from Config.properties.");

        url = properties.getProperty("url");
        loanAmount = properties.getProperty("loanAmount");
        interestRate = properties.getProperty("interestRate");
        loanTenure = properties.getProperty("loanTenure");
        emi = properties.getProperty("emi");
        fees = properties.getProperty("fees");
        fileLogger.info("Config properties loaded: url={}, loanAmount={}, interestRate={}, loanTenure={}, emi={}, fees={}", url, loanAmount, interestRate, loanTenure, emi, fees);


        if(browser.equalsIgnoreCase("chrome")) {
            driver = new ChromeDriver();
            fileLogger.info("Chrome browser initialized.");
        }
        else {
            driver=new EdgeDriver();
            fileLogger.info("Edge browser initialized.");
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); // Added implicit wait
        fileLogger.info("Browser maximized and implicit wait set.");

        emiCalculatorPage = new EMICalculatorPage(driver);
        emiCalculatorPage.navigateToEMICalculator(url);
        fileLogger.info("Navigated to EMI Calculator page.");
    }


    @Test(priority = 1)
    public void testEMICalculation() throws InterruptedException {
        extentLogger = extent.createTest("EMI Calculation Test");
        fileLogger.info("Starting test: testEMICalculation");

        try {
            emiCalculatorPage.clickCarLoanLink();
            extentLogger.info("Navigated to EMI Calculator");
            fileLogger.info("Navigated to EMI Calculator for car loan.");

            int principal = Integer.parseInt(loanAmount);
            double annualRate = Double.parseDouble(interestRate);
            int months = Integer.parseInt(loanTenure) * 12;
            double monthlyRate = annualRate / (12 * 100);
            int expectedEMI = (int) Math.round((principal * monthlyRate * Math.pow(1 + monthlyRate, months)) /
                    (Math.pow(1 + monthlyRate, months) - 1));
            fileLogger.info("Calculated expected EMI: {}", expectedEMI);

            emiCalculatorPage.enterLoanAmount(loanAmount);
            emiCalculatorPage.enterInterestRate(interestRate);
            emiCalculatorPage.enterLoanTenure(loanTenure);
            extentLogger.info("Entered loan Details and calculated");
            fileLogger.info("Entered loan amount: {}, interest rate: {}, loan tenure: {}", loanAmount, interestRate, loanTenure);

            emiCalculatorPage.scrollIntoViewYear2025();
            fileLogger.info("Scrolled to year 2025 in the amortization table.");

            String principalStr = emiCalculatorPage.getFirstMonthPrincipal();
            String interestStr = emiCalculatorPage.getFirstMonthInterest();
            fileLogger.info("Retrieved first month principal: {} and interest: {}", principalStr, interestStr);

            int firstMonthPrincipal = Integer.parseInt(principalStr.replaceAll("[^\\d]", ""));
            int firstMonthInterest = Integer.parseInt(interestStr.replaceAll("[^\\d]", ""));
            int actualEMI = firstMonthPrincipal + firstMonthInterest;
            fileLogger.info("Actual EMI from UI: {}", actualEMI);

            Assert.assertEquals(actualEMI, expectedEMI);
            extentLogger.pass("EMI matched: " + actualEMI);
            fileLogger.info("EMI matched: {}", actualEMI);

            // Thread.sleep(3000); // Replaced with explicit wait or removed if not necessary
            Thread.sleep(2000);
            String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "EMI_Calculation_Success");
            extentLogger.addScreenCaptureFromPath(screenshotPath);
            fileLogger.info("Screenshot taken: {}", screenshotPath);

        } catch (Throwable e) {
        	Thread.sleep(2000);
            String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "EMI_Calculation_Error");
            extentLogger.log(Status.FAIL,"Test failed: " + e.getMessage());
            extentLogger.addScreenCaptureFromPath(screenshotPath);
            fileLogger.error("Test failed: {}", e.getMessage(), e); // Log stack trace
            Assert.fail(e.getMessage());
        }
    }

    @Test(priority = 2)
    public void testEMICalculatorTab() {
        extentLogger = extent.createTest("EMI Calculator Tab Test");
        fileLogger.info("Starting test: testEMICalculatorTab");

        try {
            emiCalculatorPage.clickLoanEMICalculator();
            emiCalculatorPage.clickEMICalculatorTab();
            fileLogger.info("Clicked Loan EMI Calculator and then EMI Calculator tab.");

            emiCalculatorPage.enterLoanAmount(loanAmount);
            Assert.assertEquals(emiCalculatorPage.getLoanAmountValue().replaceAll(",", ""), loanAmount);
            extentLogger.pass("LoanAmount textbox validated");
            fileLogger.info("Loan Amount textbox validated: {}", loanAmount);

            emiCalculatorPage.enterInterestRate(interestRate);
            Assert.assertEquals(emiCalculatorPage.getInterestRateValue(), interestRate);
            extentLogger.pass("Interest textbox validated");
            fileLogger.info("Interest textbox validated: {}", interestRate);

            emiCalculatorPage.enterLoanTenure(loanTenure);
            Assert.assertEquals(emiCalculatorPage.getLoanTenureValue(), loanTenure);
            extentLogger.pass("Tenure textbox validated");
            fileLogger.info("Tenure textbox validated: {}", loanTenure);

            String emiValue = emiCalculatorPage.getEMICalculatedValue();
            extentLogger.pass("EMI Value displayed: " + emiValue);
            fileLogger.info("EMI Value displayed: {}", emiValue);
            Assert.assertFalse(emiValue.isEmpty());

            toggleTenureAndValidate();
            fileLogger.info("Validated tenure toggle.");

            emiCalculatorPage.moveSlider(By.xpath("//*[@id='loanamountslider']/span"));
            extentLogger.pass("LoanAmount Slider moved successfully.");
            fileLogger.info("LoanAmount Slider moved successfully.");
            emiCalculatorPage.moveSlider(By.xpath("//*[@id='loaninterestslider']/span"));
            extentLogger.pass("Interest Slider moved successfully.");
            fileLogger.info("Interest Slider moved successfully.");
            emiCalculatorPage.moveSlider(By.xpath("//*[@id='loantermslider']/span"));
            extentLogger.pass("Tenure Slider moved successfully.");
            fileLogger.info("Tenure Slider moved successfully.");

        } catch (Throwable e) {
            extentLogger.log(Status.FAIL,"Test failed: " + e.getMessage());
            fileLogger.error("Test failed: {}", e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    @Test(priority = 3)
    public void testLoanAmountCalculatorTab() {
        extentLogger = extent.createTest("Loan Amount Calculator Tab Test");
        fileLogger.info("Starting test: testLoanAmountCalculatorTab");

        try {
            emiCalculatorPage.clickLoanAmountCalculatorTab();
            fileLogger.info("Clicked 'Loan Amount Calculator' tab.");

            emiCalculatorPage.enterEMIForLoanAmountCalc(emi);
            String value=emiCalculatorPage.getEMIValueForLoanAmountCalc().replaceAll(",", "");
            fileLogger.info("EMI textbox value is {}", value);
            extentLogger.pass("EMI value displayed "+value);


            emiCalculatorPage.enterInterestRate(interestRate);
            Assert.assertEquals(emiCalculatorPage.getInterestRateValue(), interestRate);
            extentLogger.pass("Interest textbox validated");
            fileLogger.info("Interest textbox validated: {}", interestRate);

            emiCalculatorPage.enterLoanTenure(loanTenure);
            Assert.assertEquals(emiCalculatorPage.getLoanTenureValue(), loanTenure);
            extentLogger.pass("Tenure textbox validated");
            fileLogger.info("Tenure textbox validated: {}", loanTenure);

            emiCalculatorPage.enterFeesForLoanAmountCalc(fees);
            Assert.assertEquals(emiCalculatorPage.getFeesValueForLoanAmountCalc().replaceAll(",", ""), fees);
            extentLogger.pass("Fees textbox validated");
            fileLogger.info("Fees textbox validated: {}", fees);

            emiCalculatorPage.moveSlider(By.xpath("//*[@id='loanemislider']/span"));
            extentLogger.pass("EMI Slider moved successfully.");
            fileLogger.info("EMI Slider moved successfully.");
            emiCalculatorPage.moveSlider(By.xpath("//*[@id='loaninterestslider']/span"));
            extentLogger.pass("Interest Slider moved successfully.");
            fileLogger.info("Interest Slider moved successfully.");
            emiCalculatorPage.moveSlider(By.xpath("//*[@id='loantermslider']/span"));
            extentLogger.pass("Tenure Slider moved successfully.");
            fileLogger.info("Tenure Slider moved successfully.");
            toggleTenureAndValidate();
            fileLogger.info("Validated tenure toggle.");
            emiCalculatorPage.moveSlider(By.xpath("//*[@id='loanfeesslider']/span"));
            extentLogger.pass("Fees Slider moved successfully.");
            fileLogger.info("Fees Slider moved successfully.");

        } catch (Exception e) {
            extentLogger.log(Status.FAIL,"Test failed: " + e.getMessage());
            fileLogger.error("Test failed: {}", e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    @Test(priority = 4)
    public void testLoanTenureCalculatorTab() {
        extentLogger = extent.createTest("Loan Tenure Calculator Tab Test");
        fileLogger.info("Starting test: testLoanTenureCalculatorTab");

        try {
            emiCalculatorPage.clickLoanTenureCalculatorTab();
            fileLogger.info("Clicked 'Loan Tenure Calculator' tab.");

            emiCalculatorPage.enterAmountForLoanTenureCalc(loanAmount);
            Assert.assertEquals(emiCalculatorPage.getAmountValueForLoanTenureCalc().replaceAll(",", ""), loanAmount);
            extentLogger.pass("Amount textbox validated");
            fileLogger.info("Amount textbox validated: {}", loanAmount);

            emiCalculatorPage.enterInterestRate(interestRate);
            Assert.assertEquals(emiCalculatorPage.getInterestRateValue(), interestRate);
            extentLogger.pass("Interest textbox validated");
            fileLogger.info("Interest textbox validated: {}", interestRate);

            emiCalculatorPage.enterEMIForLoanTenureCalc(emi);
            String value1=emiCalculatorPage.getEMIValueForLoanTenureCalc().replaceAll(",", "");
            fileLogger.info("EMI textbox value is {}", value1);
            extentLogger.pass("EMI textbox value is "+value1);

            emiCalculatorPage.enterFeesForLoanTenureCalc(fees);
            Assert.assertEquals(emiCalculatorPage.getFeesValueForLoanTenureCalc().replaceAll(",", ""), fees);
            extentLogger.pass("Fees textbox validated");
            fileLogger.info("Fees textbox validated: {}", fees);

            emiCalculatorPage.moveSlider(By.xpath("//*[@id='loanamountslider']/span"));
            extentLogger.pass("Amount Slider moved successfully.");
            fileLogger.info("Amount Slider moved successfully.");
            emiCalculatorPage.moveSlider(By.xpath("//*[@id='loaninterestslider']/span"));
            extentLogger.pass("Interest Slider moved successfully.");
            fileLogger.info("Interest Slider moved successfully.");
            emiCalculatorPage.moveSlider(By.xpath("//*[@id='loanemislider']/span"));
            extentLogger.pass("EMI Slider moved successfully.");
            fileLogger.info("EMI Slider moved successfully.");
            emiCalculatorPage.moveSlider(By.xpath("//*[@id='loanfeesslider']/span"));
            extentLogger.pass("Fees Slider moved successfully.");
            fileLogger.info("Fees Slider moved successfully.");

        } catch (Throwable e) {
            extentLogger.log(Status.FAIL,"Test failed: " + e.getMessage());
            fileLogger.error("Test failed: {}", e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    @Test(priority = 5)
    public void testNavigateAndExtractTable() {
        extentLogger = extent.createTest("Navigate and Extract EMI Table");
        fileLogger.info("Starting test: testNavigateAndExtractTable");

        try {
            emiCalculatorPage.clickHomeLoanEMICalculator();
            extentLogger.info("Navigated to Home Loan EMI Calculator");
            fileLogger.info("Navigated to Home Loan EMI Calculator.");
            extentLogger.info("There is default value entered in the page so we go to table extraction");
            fileLogger.info("Proceeding with table extraction using default values.");
            emiCalculatorPage.scrollIntoViewYearHeader();
            fileLogger.info("Scrolled to year header in the payment schedule.");

            int column = emiCalculatorPage.getPaymentScheduleColumnCount();
            int row = emiCalculatorPage.getPaymentScheduleRowCount();
            fileLogger.info("Payment schedule table: {} columns, {} rows.", column, row);

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Payment Schedule");
            fileLogger.info("Created new Excel workbook and sheet 'Payment Schedule'.");

            int rowcount = 0;
            XSSFRow nrow = sheet.createRow(rowcount++);

            for (int j = 1; j <= column; j++) {
                String value = emiCalculatorPage.getPaymentScheduleHeaderValue(j);
                nrow.createCell(j - 1).setCellValue(value);
            }

            for (int i = 2; i <= row * 2; i += 2) { 
            	nrow = sheet.createRow(rowcount++);
                for (int j = 1; j <= column; j++) {
                    String value = emiCalculatorPage.getPaymentScheduleCellValue(i, j);
                    nrow.createCell(j - 1).setCellValue(value);
                }
            }

            try (FileOutputStream outputStream = new FileOutputStream("PaymentSchedule.xlsx")) {
                workbook.write(outputStream);
                fileLogger.info("Excel file 'PaymentSchedule.xlsx' written successfully.");
            }

            workbook.close();
            extentLogger.pass("Excel file created successfully.");
            String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "Table_Extract_Success");
            // Thread.sleep(3000); // Replaced with explicit wait or removed if not necessary
            extentLogger.addScreenCaptureFromPath(screenshotPath);
            fileLogger.info("Screenshot taken: {}", screenshotPath);


        } catch (Throwable e) {
            extentLogger.log(Status.FAIL,"Test failed: " + e.getMessage());
            String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "Table_Extract_Error");
            extentLogger.addScreenCaptureFromPath(screenshotPath);
            fileLogger.error("Test failed: {}", e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    @Test(priority = 6)
    public void testExcelFileContent() {
        extentLogger = extent.createTest("Validate Excel File Content");
        fileLogger.info("Starting test: testExcelFileContent");

        try {
            File file = new File("PaymentSchedule.xlsx");
            Assert.assertTrue(file.exists(), "Excel file does not exist.");
            fileLogger.info("Verified 'PaymentSchedule.xlsx' exists.");

            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheet("Payment Schedule");
            fileLogger.info("Opened Excel file and retrieved 'Payment Schedule' sheet.");

            Assert.assertNotNull(sheet, "Sheet not found in Excel file.");
            Assert.assertTrue(sheet.getPhysicalNumberOfRows() > 1);
            fileLogger.info("Validated Excel sheet is not empty.");

            workbook.close();
            fileLogger.info("Excel workbook closed.");
            extentLogger.pass("Excel file validated successfully !");

        } catch (Throwable e) {
            extentLogger.log(Status.FAIL,"Validation failed: " + e.getMessage());
            fileLogger.error("Validation failed: {}", e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    public void toggleTenureAndValidate() {
        try {
            String maxLabelText = emiCalculatorPage.getTenureMaxYear();
            fileLogger.info("Current max tenure label: {}", maxLabelText);

            if (maxLabelText.equals("30")) {
                extentLogger.info("Max tenure in Years: " + maxLabelText);
                emiCalculatorPage.toggleTenureToMonth();
                String maxMonth = emiCalculatorPage.getTenureMaxMonth();
                extentLogger.info("Max tenure in Months: " + maxMonth);
                extentLogger.pass("Tenure toggle validated: Years to Months");
                fileLogger.info("Tenure successfully toggled from Years (30) to Months ({})", maxMonth);
                Assert.assertEquals(maxMonth, "360");
            } else  {
                extentLogger.info("Max tenure in Months: " + maxLabelText);

                emiCalculatorPage.toggleTenureToYear();

                String maxYear = emiCalculatorPage.getTenureMaxYear();
                extentLogger.info("Max tenure in Years: " + maxYear);
                extentLogger.pass("Tenure toggle validated: Months to Years");
                fileLogger.info("Tenure successfully toggled from Months (360) to Years ({})", maxYear);
                Assert.assertEquals(maxYear, "30");
            } 
        } catch (Exception e) {
            extentLogger.log(Status.FAIL,"Tenure toggle validation failed: " + e.getMessage());
            fileLogger.error("Tenure toggle validation failed: {}", e.getMessage(), e);
            e.printStackTrace(); // Keep for immediate debug in console, but file logger handles it.
            Assert.fail("Exception during tenure toggle validation: " + e.getMessage()); // Fail the test if an exception occurs
        }
    }

    @AfterClass
    public void tearDownDriver() {
        if (driver != null) {
            driver.quit();
            fileLogger.info("WebDriver quit.");
        }
    }

    @AfterSuite
    public void flushReport() {
        ExtentReporterSetup.flushReport();
        fileLogger.info("Extent Report flushed.");
        // Ensure Log4j2 logs are flushed as well if needed (often handled automatically on shutdown)
        LogManager.shutdown(); // Explicitly shut down Log4j2
        fileLogger.info("Log4j2 shutdown."); // This log might not appear if shutdown is immediate
    }
}