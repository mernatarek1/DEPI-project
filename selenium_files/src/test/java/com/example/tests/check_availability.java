package com.example.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import java.time.Duration;
import java.util.List;

public class check_availability {




    private static final String CheckIn_FIELD_XPATH = "//*[@id=\"booking\"]/div/div/div/form/div/div[1]/div[1]/div/input";
    private static final String CheckOut_FIELD_XPATH = "//*[@id=\"booking\"]/div/div/div/form/div/div[2]/div[1]/div/input";
    private static final String checkava_BUTTON_XPATH = "//*[@id=\"booking\"]/div/div/div/form/div/div[4]/button";

    // Constants for field values
    private static final String CheckIn_VALUE = "21/04/2025";
    private static final String CheckOut_VALUE = "25/04/2025";

    // Constants for URL and wait durations
    private static final String WEBSITE_URL = "https://automationintesting.online";
    private static final int WAIT_TIMEOUT_SECONDS = 30;
    private static final int SLEEP_MILLIS = 2000;

    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();

        // Initialize ChromeDriver
        WebDriver driver = new ChromeDriver();

        try {
            // Navigate to the website
            driver.get(WEBSITE_URL);
            Thread.sleep(SLEEP_MILLIS);
            // Print the page title to verify navigation
            System.out.println("Page title: " + driver.getTitle());

            // Maximize the browser window
            driver.manage().window().maximize();

            // Initialize WebDriverWait with Duration
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT_SECONDS));

            // Wait for the page to settle after the click
            Thread.sleep(SLEEP_MILLIS);

            try {
                // Fill the username field
                fillField(driver, wait, CheckIn_FIELD_XPATH, "  ", "Check In");
                Thread.sleep(SLEEP_MILLIS);
                fillField(driver, wait, CheckIn_FIELD_XPATH, CheckIn_VALUE, "Check In");
                Thread.sleep(SLEEP_MILLIS);
                // Fill the password field
                fillField(driver, wait, CheckOut_FIELD_XPATH, "  ", "Check Out");
                Thread.sleep(SLEEP_MILLIS);
                fillField(driver, wait, CheckOut_FIELD_XPATH, CheckOut_VALUE, "Check Out");
                Thread.sleep(SLEEP_MILLIS);
            } catch (Exception e) {
                System.out.println("Could not fill form fields: " + e.getMessage());
            }

            Thread.sleep(SLEEP_MILLIS);

            try {
                // Locate the login button
                WebElement checkava_BUTTON = wait.until(
                        ExpectedConditions.elementToBeClickable(By.xpath(checkava_BUTTON_XPATH))
                );
                Thread.sleep(SLEEP_MILLIS);
                // Scroll to the button to ensure it's in view
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].scrollIntoView({block: 'center'});", checkava_BUTTON);
                // Check if the button is part of a form and submit the form
                WebElement form = (WebElement) ((JavascriptExecutor) driver).executeScript(
                        "return arguments[0].closest('form');", checkava_BUTTON
                );
                if (form != null) {
                    // Trigger form submission
                    ((JavascriptExecutor) driver).executeScript("arguments[0].submit();", form);
                    System.out.println("Submitted the form containing the 'check availability' button");
                } else {
                    // Fallback: Trigger a click with JavaScript
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].dispatchEvent(new Event('click', { bubbles: true }));", checkava_BUTTON
                    );
                    System.out.println("Triggered click event on the 'login' button");
                    Thread.sleep(SLEEP_MILLIS);
                }
            } catch (Exception e) {
                System.out.println("Could not click the 'login' button: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the browser
            driver.quit();
        }
    }

    // Helper method to fill a field using JavaScript
    private static void fillField(WebDriver driver, WebDriverWait wait, String xpath, String value, String fieldName) throws InterruptedException {
        WebElement field = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath(xpath))
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", field);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", field, value);
        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", field);
        System.out.println("Filled " + fieldName + " field");
    }
}

