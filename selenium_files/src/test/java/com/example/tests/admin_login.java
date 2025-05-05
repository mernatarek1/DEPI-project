package com.example.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;

public class admin_login {
    // Constants for locators
    private static final String ADMIN_LINK_TEXT = "Admin";
    private static final String USERNAME_FIELD_XPATH = "//*[@id='username']";
    private static final String PASSWORD_FIELD_XPATH = "//*[@id='password']";
    private static final String LOGIN_BUTTON_XPATH = "//*[@id='doLogin']";

    // Constants for field values
    private static final String USERNAME_VALUE = "admin";
    private static final String PASSWORD_VALUE = "password123";

    // Constants for URL and wait durations
    private static final String WEBSITE_URL = "https://automationintesting.online";
    private static final int WAIT_TIMEOUT_SECONDS = 30;
    private static final int SLEEP_MILLIS = 2000;
    private static final int POST_LOGIN_SLEEP_MILLIS = 5000;

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

            // Locate and click the "Admin" link
            WebElement adminLink = wait.until(
                    ExpectedConditions.elementToBeClickable(By.linkText(ADMIN_LINK_TEXT))
            );
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block: 'center'});", adminLink);
            adminLink.click();
            System.out.println("Admin link clicked successfully!");
            System.out.println("Current URL after first click: " + driver.getCurrentUrl());

            // Wait for the page to settle after the click
            Thread.sleep(SLEEP_MILLIS);

            try {
                // Fill the username field
                fillField(driver, wait, USERNAME_FIELD_XPATH, USERNAME_VALUE, "username");
                // Fill the password field
                fillField(driver, wait, PASSWORD_FIELD_XPATH, PASSWORD_VALUE, "password");
            } catch (Exception e) {
                System.out.println("Could not fill form fields: " + e.getMessage());
            }

            Thread.sleep(SLEEP_MILLIS);

            try {
                // Locate the login button
                WebElement loginButton = wait.until(
                        ExpectedConditions.elementToBeClickable(By.xpath(LOGIN_BUTTON_XPATH))
                );
                Thread.sleep(SLEEP_MILLIS);
                // Scroll to the button to ensure it's in view
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].scrollIntoView({block: 'center'});", loginButton);
                // Check if the button is part of a form and submit the form
                WebElement form = (WebElement) ((JavascriptExecutor) driver).executeScript(
                        "return arguments[0].closest('form');", loginButton
                );
                if (form != null) {
                    // Trigger form submission
                    ((JavascriptExecutor) driver).executeScript("arguments[0].submit();", form);
                    System.out.println("Submitted the form containing the 'login' button");
                } else {
                    // Fallback: Trigger a click with JavaScript
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].dispatchEvent(new Event('click', { bubbles: true }));", loginButton
                    );
                    System.out.println("Triggered click event on the 'login' button");
                    Thread.sleep(POST_LOGIN_SLEEP_MILLIS);
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