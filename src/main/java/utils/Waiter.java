package utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Waiter {
    public static final int TIMEOUT = 30;

    private WebDriver driver;

    /**
     * Wait for the page to finish loading
     * Use the document.readyState property
     * Page and static resources are loaded when property value is 'complete'
     * Wait for up to TIMEOUT seconds
     */
    public void waitForPageLoadComplete() {
        waitForPageLoadComplete(TIMEOUT);
    }

    /**
     * Wait for the page to finish loading
     * Use the document.readyState property
     * Page and static resources are loaded when property value is 'complete'
     *
     * @param specificTimeout - Wait for up to this many seconds
     */
    public void waitForPageLoadComplete(int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            wait.until(driver -> String
                    .valueOf(((JavascriptExecutor)
                            driver).executeScript("return document.readyState"))
                    .equals("complete"));
        } catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Page did not finish loading within " + specificTimeout + " seconds.");
        }
    }

    /**
     * Wait for jQuery to finish loading by looking at the 'active' property
     * Method exits successfully when jQuery finished loading
     * or if there is no jQuery on the page
     * Wait for up to TIMEOUT seconds
     */
    public void waitForJQuery() {
        waitForJQuery(TIMEOUT);
    }

    /**
     * Wait for jQuery to finish loading by looking at the 'active' property
     * Method exits successfully when jQuery finished loading
     * or if there is no jQuery on the page
     *
     * @param specificTimeout - Wait for up to this many seconds
     */
    public void waitForJQuery(int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver,
                Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    return (Boolean) ((JavascriptExecutor)
                            driver).executeScript("return jQuery.active == 0");
                } catch (Exception e) {
                    return true;
                }
            };
            wait.until(condition);
        } catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: JQuery did not finish loading within "
                    + specificTimeout + " seconds.");
        }
    }

    /**
     * Open a URL in the current browser window and wait for page and jQuery to load (if it exists)
     * Wait for up to TIMEOUT seconds for page to load,
     * then for up to TIMEOUT seconds for jQuery to load if it exists
     * @param url - url to open
     */
    public void get(String url) {
        get(url, TIMEOUT);
    }

    /**
     * Open a URL in the current browser window and wait for page and jQuery to load (if it exists)
     * @param url - url to open
     * @param specificTimeout - wait for up to this many seconds for page to load, then for this many
     *                        seconds for jQuery to load, if it exists
     */
    public void get(String url, int specificTimeout) {
        driver.get(url);
        waitForPageLoadComplete(specificTimeout);
        waitForJQuery(specificTimeout);
    }
}
