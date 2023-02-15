package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Waiter {
    public static final int LONG_TIMEOUT = 120;
    public static final int MEDIUM_TIMEOUT = 60;
    public static final int TIMEOUT = 30;
    public static final int TINY_TIMEOUT = 10;

    private WebDriver driver;

    public Waiter(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * See {@link  utils.Waiter#waitForPageLoadComplete(int)}   label}
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
     * See {@link  utils.Waiter#waitForJQuery(int)}   label}
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
     * See {@link  utils.Waiter#get(String, int)}   label}
     * Wait for up to TIMEOUT seconds for page to load,
     * then for up to TIMEOUT seconds for jQuery to load if it exists
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

    /**
     * See {@link  utils.Waiter#click(WebElement, int)}   label}
     * Wait for up to TIMEOUT seconds for click to be successful
     */
    public void click(WebElement elementToClick) {
        click(elementToClick, TIMEOUT);
    }

    /**
     * Wait method for successfully clicking an element provided as a WebElement from an @FindBy definition
     * The click is retried if any exception occurs when attempting to click
     * (like StaleElementException or NoSuchElementException)
     * No need to couple with a 'wait for element displayed' method
     * @param elementToClick - page element to click on
     * @param specificTimeout - wait for up to this many seconds for the click to be successful
     */
    public void click(WebElement elementToClick, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver,
                Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    elementToClick.click();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully click on '" + elementToClick + "' within "
                    + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link  utils.Waiter#click(By, int)}   label}
     * Wait for up to TIMEOUT seconds for click to be successful
     */
    public void click(By selectorForElementToClick) {
        click(selectorForElementToClick, TIMEOUT);
    }

    /**
     * Wait method for successfully clicking an element whose selector is provided as a By selector
     * Inside the wait method is where the corresponding WebElement will be extracted based
     * on the By selector
     * The click is retried if any exception occurs when attempting to click
     * (like StaleElementException or NoSuchElementException)
     * No need to couple with a 'wait for element displayed' method
     * @param selectorForElementToClick - selector for identifying the page element
     * @param specificTimeout - wait for up to this many seconds for the click to be successful
     */
    public void click(By selectorForElementToClick, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver,
                Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    driver.findElement(selectorForElementToClick).click();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully click on '" + selectorForElementToClick + "' within "
                    + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearSendKeysAndTab_WaitAttributeValueEqualsText(WebElement, String, int)}
     */
    public void clearSendKeysAndTab_WaitAttributeValueEqualsText(WebElement inputToTypeInto, String text) {
        clearSendKeysAndTab_WaitAttributeValueEqualsText(inputToTypeInto, text, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), type into the input the desired text, then hit TAB.
     * The method exits successfully when, after hitting TAB, the text is still the typed one.
     * For that, the method will wait for the attribute "value"'s value to equal the typed text.
     * The TAB is useful when the input is actioned by JS, which might change the value typed
     * once the cursor moves into a different field.
     * @param inputToTypeInto - input to type into
     * @param text - what to type into the input
     * @param specificTimeout - wait for up to this many seconds for the condition to be successful
     */
    public void clearSendKeysAndTab_WaitAttributeValueEqualsText(WebElement inputToTypeInto, String text, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    inputToTypeInto.sendKeys(Keys.TAB);
                    return inputToTypeInto.getAttribute("value").equals(text);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeInto + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearAndSendKeys_WaitAttributeValueEqualsText(WebElement, String, int)}
     */
    public void clearAndSendKeys_WaitAttributeValueEqualsText(WebElement inputToTypeInto, String text) {
        clearAndSendKeys_WaitAttributeValueEqualsText(inputToTypeInto, text, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), then type into the input the desired text.
     * The method exits successfully when, after typing, the text is still the typed one.
     * For that, the method will wait for attribute "value"'s value to equal the typed text.
     * @param inputToTypeInto - input to type into
     * @param text - what to type into the input
     * @param specificTimeout - wait for up to this many seconds for the condition to be successful
     */
    public void clearAndSendKeys_WaitAttributeValueEqualsText(WebElement inputToTypeInto, String text, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    return inputToTypeInto.getAttribute("value").equals(text);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeInto + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearSendKeysAndTab_WaitAttributeValueEqualsText(By, String, int)}
     */
    public void clearSendKeysAndTab_WaitAttributeValueEqualsText(By inputToTypeIntoBy, String text) {
        clearSendKeysAndTab_WaitAttributeValueEqualsText(inputToTypeIntoBy, text, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), type into the input the desired text, then hit TAB.
     * The method exits successfully when, after hitting TAB, the text is still the typed one.
     * For that, the method will wait for attribute "value"'s value to equal the typed text.
     * The TAB is useful when the input is actioned by JS, which might change the value typed
     * once the cursor moves into a different field.
     * @param inputToTypeIntoBy - By for input to type into
     * @param text - what to type into the input
     * @param specificTimeout - wait for up to this many seconds for the click to be successful
     */
    public void clearSendKeysAndTab_WaitAttributeValueEqualsText(By inputToTypeIntoBy, String text, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    WebElement inputToTypeInto = driver.findElement(inputToTypeIntoBy);
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    inputToTypeInto.sendKeys(Keys.TAB);
                    return inputToTypeInto.getAttribute("value").equals(text);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeIntoBy + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearAndSendKeys_WaitAttributeValueEqualsText(By, String, int)}
     */
    public void clearAndSendKeys_WaitAttributeValueEqualsText(By inputToTypeIntoBy, String text) {
        clearAndSendKeys_WaitAttributeValueEqualsText(inputToTypeIntoBy, text, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), then type into the input the desired text.
     * The method exits successfully when, after typing, the text is still the typed one.
     * For that, the method will wait for attribute "value"'s value to equal the typed text.
     * @param inputToTypeIntoBy - By for input to type into
     * @param text - what to type into the input
     * @param specificTimeout - wait for up to this many seconds for the click to be successful
     */
    public void clearAndSendKeys_WaitAttributeValueEqualsText(By inputToTypeIntoBy, String text, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    WebElement inputToTypeInto = driver.findElement(inputToTypeIntoBy);
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    return inputToTypeInto.getAttribute("value").equals(text);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeIntoBy + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearSendKeysAndTab_WaitAttributeValueEqualsAnotherText(WebElement, String, String, int)}
     */
    public void clearSendKeysAndTab_WaitAttributeValueEqualsAnotherText(WebElement inputToTypeInto, String text,
                                                                        String expectedText) {
        clearSendKeysAndTab_WaitAttributeValueEqualsAnotherText(inputToTypeInto, text, expectedText, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), type into the input the desired text, then hit TAB.
     * The method exits successfully when, after hitting TAB, the text is another expected one.
     * For that, the method will wait for attribute "value"'s value to equal the expected text.
     * The TAB is useful when the input is actioned by JS, which might change the value typed
     * once the cursor moves into a different field.
     * @param inputToTypeInto - input to type into
     * @param text - what to type into the input
     * @param expectedText - what the text in the field should be after typing
     * @param specificTimeout - wait for up to this many seconds for the condition to be successful
     */
    public void clearSendKeysAndTab_WaitAttributeValueEqualsAnotherText(WebElement inputToTypeInto,
                                                                        String text, String expectedText,
                                                                        int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    inputToTypeInto.sendKeys(Keys.TAB);
                    return inputToTypeInto.getAttribute("value").equals(expectedText);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeInto + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearAndSendKeys_WaitAttributeValueEqualsAnotherText(WebElement, String, String, int)}
     */
    public void clearAndSendKeys_WaitAttributeValueEqualsAnotherText(WebElement inputToTypeInto,
                                                                     String text, String expectedText) {
        clearAndSendKeys_WaitAttributeValueEqualsAnotherText(inputToTypeInto, text, expectedText, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), then type into the input the desired text.
     * The method exits successfully when, after typing, the text is another expected one.
     * For that, the method will wait for the attribute "value"'s value to equal the expected text.
     * @param inputToTypeInto - input to type into
     * @param text - what to type into the input
     * @param expectedText - what the text in the field should be after typing
     * @param specificTimeout - wait for up to this many seconds for the condition to be successful
     */
    public void clearAndSendKeys_WaitAttributeValueEqualsAnotherText(WebElement inputToTypeInto, String text,
                                                                     String expectedText, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    return inputToTypeInto.getAttribute("value").equals(expectedText);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeInto + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearSendKeysAndTab_WaitAttributeValueEqualsAnotherText(By, String, String, int)}
     */
    public void clearSendKeysAndTab_WaitAttributeValueEqualsAnotherText(By inputToTypeIntoBy,
                                                                        String text, String expectedText) {
        clearSendKeysAndTab_WaitAttributeValueEqualsAnotherText(inputToTypeIntoBy, text, expectedText, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), type into the input the desired text, then hit TAB.
     * The method exits successfully when, after hitting TAB, the text is another expected one.
     * For that, the method will wait for the attribute "value"'s value to equal an expected text.
     * The TAB is useful when the input is actioned by JS, which might change the value typed
     * once the cursor moves into a different field.
     * @param inputToTypeIntoBy - By for input to type into
     * @param text - what to type into the input
     * @param expectedText - the text that needs to be displayed in the field after typing
     * @param specificTimeout - wait for up to this many seconds for the click to be successful
     */
    public void clearSendKeysAndTab_WaitAttributeValueEqualsAnotherText(By inputToTypeIntoBy, String text,
                                                                        String expectedText, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    WebElement inputToTypeInto = driver.findElement(inputToTypeIntoBy);
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    inputToTypeInto.sendKeys(Keys.TAB);
                    return inputToTypeInto.getAttribute("value").equals(expectedText);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeIntoBy + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearAndSendKeys_WaitAttributeValueEqualsAnotherText(By, String, String, int)}
     */
    public void clearAndSendKeys_WaitAttributeValueEqualsAnotherText(By inputToTypeIntoBy, String text, String expectedText) {
        clearAndSendKeys_WaitAttributeValueEqualsAnotherText(inputToTypeIntoBy, text, expectedText, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), then type into the input the desired text.
     * The method exits successfully when, after typing, the text is another, expected text.
     * For that, the method will wait for the attribute "value"'s value to equal the expected text.
     * @param inputToTypeIntoBy - By for input to type into
     * @param text - what to type into the input
     * @param expectedText - the text that will be displayed in the field after typing
     * @param specificTimeout - wait for up to this many seconds for the click to be successful
     */
    public void clearAndSendKeys_WaitAttributeValueEqualsAnotherText(By inputToTypeIntoBy, String text,
                                                                     String expectedText, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    WebElement inputToTypeInto = driver.findElement(inputToTypeIntoBy);
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    return inputToTypeInto.getAttribute("value").equals(expectedText);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeIntoBy + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearSendKeysAndTab_WaitGetTextEqualsText(WebElement, String, int)}
     */
    public void clearSendKeysAndTab_WaitGetTextEqualsText(WebElement inputToTypeInto, String text) {
        clearSendKeysAndTab_WaitGetTextEqualsText(inputToTypeInto, text, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), type into the input the desired text, then hit TAB.
     * The method exits successfully when, after hitting TAB, the text is still the typed one.
     * For that, the method will wait for the element text to equal the typed text.
     * The TAB is useful when the input is actioned by JS, which might change the value typed
     * once the cursor moves into a different field.
     * @param inputToTypeInto - input to type into
     * @param text - what to type into the input
     * @param specificTimeout - wait for up to this many seconds for the condition to be successful
     */
    public void clearSendKeysAndTab_WaitGetTextEqualsText(WebElement inputToTypeInto, String text, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    inputToTypeInto.sendKeys(Keys.TAB);
                    return inputToTypeInto.getText().equals(text);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeInto + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearAndSendKeys_WaitGetTextEqualsText(WebElement, String, int)}
     */
    public void clearAndSendKeys_WaitGetTextEqualsText(WebElement inputToTypeInto, String text) {
        clearAndSendKeys_WaitGetTextEqualsText(inputToTypeInto, text, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), then type into the input the desired text.
     * The method exits successfully when, after typing, the text is still the typed one.
     * For that, the method will wait for the element text to equal the typed text.
     * @param inputToTypeInto - input to type into
     * @param text - what to type into the input
     * @param specificTimeout - wait for up to this many seconds for the condition to be successful
     */
    public void clearAndSendKeys_WaitGetTextEqualsText(WebElement inputToTypeInto, String text, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    return inputToTypeInto.getText().equals(text);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeInto + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearSendKeysAndTab_WaitGetTextEqualsText(By, String, int)}
     */
    public void clearSendKeysAndTab_WaitGetTextEqualsText(By inputToTypeIntoBy, String text) {
        clearSendKeysAndTab_WaitGetTextEqualsText(inputToTypeIntoBy, text, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), type into the input the desired text, then hit TAB.
     * The method exits successfully when, after hitting TAB, the text is still the typed one.
     * For that, the method will wait for element text to equal the typed text.
     * The TAB is useful when the input is actioned by JS, which might change the value typed
     * once the cursor moves into a different field.
     * @param inputToTypeIntoBy - By for input to type into
     * @param text - what to type into the input
     * @param specificTimeout - wait for up to this many seconds for the click to be successful
     */
    public void clearSendKeysAndTab_WaitGetTextEqualsText(By inputToTypeIntoBy, String text, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    WebElement inputToTypeInto = driver.findElement(inputToTypeIntoBy);
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    inputToTypeInto.sendKeys(Keys.TAB);
                    return inputToTypeInto.getText().equals(text);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeIntoBy + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearAndSendKeys_WaitGetTextEqualsText(By, String, int)}
     */
    public void clearAndSendKeys_WaitGetTextEqualsText(By inputToTypeIntoBy, String text) {
        clearAndSendKeys_WaitAttributeValueEqualsText(inputToTypeIntoBy, text, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), then type into the input the desired text.
     * The method exits successfully when, after typing, the text is still the typed one.
     * For that, the method will wait for the element text to equal the typed text.
     * @param inputToTypeIntoBy - By for input to type into
     * @param text - what to type into the input
     * @param specificTimeout - wait for up to this many seconds for the click to be successful
     */
    public void clearAndSendKeys_WaitGetTextEqualsText(By inputToTypeIntoBy, String text, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    WebElement inputToTypeInto = driver.findElement(inputToTypeIntoBy);
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    return inputToTypeInto.getText().equals(text);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeIntoBy + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearSendKeysAndTab_WaitGetTextEqualsAnotherText(WebElement, String, String, int)}
     */
    public void clearSendKeysAndTab_WaitGetTextEqualsAnotherText(WebElement inputToTypeInto, String text,
                                                                        String expectedText) {
        clearSendKeysAndTab_WaitGetTextEqualsAnotherText(inputToTypeInto, text, expectedText, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), type into the input the desired text, then hit TAB.
     * The method exits successfully when, after hitting TAB, the text is another expected one.
     * For that, the method will wait for the element inner text to equal the expected text.
     * The TAB is useful when the input is actioned by JS, which might change the value typed
     * once the cursor moves into a different field.
     * @param inputToTypeInto - input to type into
     * @param text - what to type into the input
     * @param expectedText - what the text in the field should be after typing
     * @param specificTimeout - wait for up to this many seconds for the condition to be successful
     */
    public void clearSendKeysAndTab_WaitGetTextEqualsAnotherText(WebElement inputToTypeInto,
                                                                        String text, String expectedText,
                                                                        int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    inputToTypeInto.sendKeys(Keys.TAB);
                    return inputToTypeInto.getText().equals(expectedText);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeInto + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearAndSendKeys_WaitGetTextEqualsAnotherText(WebElement, String, String, int)}
     */
    public void clearAndSendKeys_WaitGetTextEqualsAnotherText(WebElement inputToTypeInto,
                                                                     String text, String expectedText) {
        clearAndSendKeys_WaitGetTextEqualsAnotherText(inputToTypeInto, text, expectedText, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), then type into the input the desired text.
     * The method exits successfully when, after typing, the text is another expected one.
     * For that, the method will wait for the element inner text to equal the expected text.
     * @param inputToTypeInto - input to type into
     * @param text - what to type into the input
     * @param expectedText - what the text in the field should be after typing
     * @param specificTimeout - wait for up to this many seconds for the condition to be successful
     */
    public void clearAndSendKeys_WaitGetTextEqualsAnotherText(WebElement inputToTypeInto, String text,
                                                                     String expectedText, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    return inputToTypeInto.getText().equals(expectedText);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeInto + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearSendKeysAndTab_WaitGetTextEqualsAnotherText(By, String, String, int)}
     */
    public void clearSendKeysAndTab_WaitGetTextEqualsAnotherText(By inputToTypeIntoBy,
                                                                        String text, String expectedText) {
        clearSendKeysAndTab_WaitGetTextEqualsAnotherText(inputToTypeIntoBy, text, expectedText, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), type into the input the desired text, then hit TAB.
     * The method exits successfully when, after hitting TAB, the text is another expected one.
     * For that, the method will wait for the element inner text to equal an expected text.
     * The TAB is useful when the input is actioned by JS, which might change the value typed
     * once the cursor moves into a different field.
     * @param inputToTypeIntoBy - By for input to type into
     * @param text - what to type into the input
     * @param expectedText - the text that needs to be displayed in the field after typing
     * @param specificTimeout - wait for up to this many seconds for the click to be successful
     */
    public void clearSendKeysAndTab_WaitGetTextEqualsAnotherText(By inputToTypeIntoBy, String text,
                                                                        String expectedText, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    WebElement inputToTypeInto = driver.findElement(inputToTypeIntoBy);
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    inputToTypeInto.sendKeys(Keys.TAB);
                    return inputToTypeInto.getText().equals(expectedText);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeIntoBy + "' within " + specificTimeout + " seconds.");
        }
    }

    /**
     * See {@link Waiter#clearAndSendKeys_WaitGetTextEqualsAnotherText(By, String, String, int)}
     */
    public void clearAndSendKeys_WaitGetTextEqualsAnotherText(By inputToTypeIntoBy, String text, String expectedText) {
        clearAndSendKeys_WaitGetTextEqualsAnotherText(inputToTypeIntoBy, text, expectedText, TIMEOUT);
    }

    /**
     * Clear input first (so as not to concatenate to existing text), then type into the input the desired text.
     * The method exits successfully when, after typing, the text is another, expected text.
     * For that, the method will wait for the element inner text to equal the expected text.
     * @param inputToTypeIntoBy - By for input to type into
     * @param text - what to type into the input
     * @param expectedText - the text that will be displayed in the field after typing
     * @param specificTimeout - wait for up to this many seconds for the click to be successful
     */
    public void clearAndSendKeys_WaitGetTextEqualsAnotherText(By inputToTypeIntoBy, String text,
                                                                     String expectedText, int specificTimeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(specificTimeout));
        try {
            ExpectedCondition<Boolean> condition = arg -> {
                try {
                    WebElement inputToTypeInto = driver.findElement(inputToTypeIntoBy);
                    inputToTypeInto.clear();
                    inputToTypeInto.sendKeys(text);
                    return inputToTypeInto.getText().equals(expectedText);
                } catch (Exception e) {
                    return false;
                }
            };
            wait.until(condition);
        }
        catch (TimeoutException e) {
            throw new RuntimeException("waiter2.FAILURE: Could not successfully type the text '" + text +
                    "' into input '" + inputToTypeIntoBy + "' within " + specificTimeout + " seconds.");
        }
    }
}
