package fproject;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.logging.log4j.LogManager; // Added for Log4j2
import org.apache.logging.log4j.Logger;     // Added for Log4j2

public class ExtentReporterSetup {
    private static final Logger logger = LogManager.getLogger(ExtentReporterSetup.class); // Log4j2 Logger
    private static ExtentReports extent;

    public static ExtentReports setupExtentReport() {
        if (extent == null) {
            ExtentSparkReporter reporter = new ExtentSparkReporter("test-output/ExtendReport.html");
            reporter.config().setReportName("EMI Calculator Automation Test Report");
            reporter.config().setDocumentTitle("Automation Report");
            logger.info("ExtentSparkReporter configured.");

            extent = new ExtentReports();
            extent.attachReporter(reporter);
            extent.setSystemInfo("Project", "EMI Calculator");
            extent.setSystemInfo("Tester", "REST in pieces"); // Consolidated Tester Name
            logger.info("ExtentReports initialized and attached reporter.");
        }
        return extent;
    }

    public static void flushReport() {
        if (extent != null) {
            extent.flush();
            logger.info("ExtentReports flushed.");
        }
    }
}