package fproject;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.File;
import org.apache.logging.log4j.LogManager; // Added for Log4j2
import org.apache.logging.log4j.Logger;     // Added for Log4j2

public class ScreenshotUtils {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class); // Log4j2 Logger

    public static String takeScreenshot(WebDriver driver, String screenshotName) {
        String path = System.getProperty("user.dir") + "/test-output/screenshots/" + screenshotName + ".png";
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        (src).renameTo(new File(path));
		logger.info("Screenshot saved: {}", path);
        return path;
    }
}