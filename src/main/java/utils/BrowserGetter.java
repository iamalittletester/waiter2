package utils;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.System.getProperty;

public class BrowserGetter {

    public WebDriver getDriver() {
        switch (getProperty("browser").toLowerCase()) {
            case "chrome":
                System.out.println("Chrome was chosen!");
                return getChromeDriver();
            case "firefox":
                System.out.println("Firefox was chosen!");
                return getFirefoxDriver();
            case "edge":
                System.out.println("Edge was chosen!");
                return getEdgeDriver();
            case "safari":
                System.out.println("Safari was chosen!");
                return getSafariDriver();
            case "chrome_s":
                System.out.println("Chrome with custom size was chosen!");
                return getChromeDriverCustomSize(parseInt(getProperty("width")), parseInt(getProperty("height")));
            case "firefox_s":
                System.out.println("Firefox with custom size was chosen!");
                return getFirefoxDriverCustomSize(parseInt(getProperty("width")), parseInt(getProperty("height")));
            case "edge_s":
                System.out.println("Edge with custom size was chosen!");
                return getEdgeDriverCustomSize(parseInt(getProperty("width")), parseInt(getProperty("height")));
            case "safari_s":
                System.out.println("Safari with custom size was chosen!");
                return getSafariDriverCustomSize(parseInt(getProperty("width")), parseInt(getProperty("height")));
            case "chrome_h":
                System.out.println("Chrome headless was chosen!");
                return getChromeHeadless();
            case "firefox_h":
                System.out.println("Firefox headless was chosen!");
                return getFirefoxHeadless();
            case "edge_h":
                System.out.println("Edge headless was chosen!");
                return getEdgeHeadless();
            case "chrome_a":
                System.out.println("Chrome with arguments was chosen!");
                return getChromeWithArguments();
            case "firefox_a":
                System.out.println("Firefox with arguments was chosen!");
                return getFirefoxWithArguments();
            case "edge_a":
                System.out.println("Edge with arguments was chosen!");
                return getEdgeWithArguments();
            default:
                throw new RuntimeException("Unsupported browser! Will not start any browser!");
        }
    }

    public WebDriver getChromeDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().window().maximize();
        return driver;
    }

    public WebDriver getFirefoxDriver() {
        WebDriver driver = new FirefoxDriver();
        driver.manage().window().maximize();
        return driver;
    }

    public WebDriver getEdgeDriver() {
        WebDriver driver = new EdgeDriver();
        driver.manage().window().maximize();
        return driver;
    }

    public WebDriver getSafariDriver() {
        WebDriver driver = new SafariDriver();
        driver.manage().window().maximize();
        return driver;
    }

    public WebDriver getChromeDriverCustomSize(int width, int height) {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().window().setSize(new Dimension(width, height));
        return driver;
    }

    public WebDriver getEdgeDriverCustomSize(int width, int height) {
        WebDriver driver = new EdgeDriver();
        driver.manage().window().setSize(new Dimension(width, height));
        return driver;
    }

    public WebDriver getFirefoxDriverCustomSize(int width, int height) {
        WebDriver driver = new FirefoxDriver();
        driver.manage().window().setSize(new Dimension(width, height));
        return driver;
    }

    public WebDriver getSafariDriverCustomSize(int width, int height) {
        WebDriver driver = new SafariDriver();
        driver.manage().window().setSize(new Dimension(width, height));
        return driver;
    }

    public WebDriver getChromeHeadless() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(List.of("--headless=new", "--remote-allow-origins=*"));
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        return driver;
    }

    public WebDriver getFirefoxHeadless() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.addArguments("--headless");
        WebDriver driver = new FirefoxDriver(firefoxOptions);
        driver.manage().window().maximize();
        return driver;
    }

    public WebDriver getEdgeHeadless() {
        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.addArguments("headless");
        WebDriver driver = new EdgeDriver(edgeOptions);
        driver.manage().window().maximize();
        return driver;
    }

    public WebDriver getChromeWithArguments() {
        ChromeOptions options = new ChromeOptions();
        String[] arguments = getProperty("arguments").split(",");
        options.addArguments(List.of(arguments));
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        return driver;
    }

    public WebDriver getFirefoxWithArguments() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        String[] arguments = getProperty("arguments").split(",");
        firefoxOptions.addArguments(List.of(arguments));
        WebDriver driver = new FirefoxDriver(firefoxOptions);
        driver.manage().window().maximize();
        return driver;
    }

    public WebDriver getEdgeWithArguments() {
        EdgeOptions edgeOptions = new EdgeOptions();
        String[] arguments = getProperty("arguments").split(",");
        edgeOptions.addArguments(List.of(arguments));
        WebDriver driver = new EdgeDriver(edgeOptions);
        driver.manage().window().maximize();
        return driver;
    }
}
