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

public class send_msg {
    // Constants for field locators
    private static final String NAME_FIELD_XPATH = "//*[@id='name']";
    private static final String EMAIL_FIELD_XPATH = "//*[@id='email']";
    private static final String PHONE_FIELD_XPATH = "//*[@id='phone']";
    private static final String SUBJECT_FIELD_XPATH = "//*[@id='subject']";
    private static final String DESCRIPTION_FIELD_XPATH = "//*[@id='description']";
    private static final String SUBMIT_BUTTON_XPATH = "//*[@id='contact']/div/div/div/div/div/form/div[6]/button";
    private static final String CONFIRMATION_MESSAGE_XPATH = "//*[contains(text(), 'Thanks for getting in touch')]";

    // Constants for field values
    private static final String NAME_VALUE = "merna tarek";
    private static final String EMAIL_VALUE = "fakeemail@gmail.com";
    private static final String PHONE_VALUE = "01234567890";
    private static final String SUBJECT_VALUE = "that is the subject okaaaaaay";
    private static final String DESCRIPTION_VALUE = "this is the description that i need to write it";

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

            // Wait for the page to settle
            Thread.sleep(SLEEP_MILLIS);

            try {
                // Fill the name field
                fillField(driver, wait, NAME_FIELD_XPATH, NAME_VALUE, "name");
                // Fill the email field
                fillField(driver, wait, EMAIL_FIELD_XPATH, EMAIL_VALUE, "email");
                // Fill the phone field
                fillField(driver, wait, PHONE_FIELD_XPATH, PHONE_VALUE, "phone");
                // Fill the subject field
                fillField(driver, wait, SUBJECT_FIELD_XPATH, SUBJECT_VALUE, "subject");
                // Fill the description field
                fillField(driver, wait, DESCRIPTION_FIELD_XPATH, DESCRIPTION_VALUE, "description");
            } catch (Exception e) {
                System.out.println("Could not fill form fields: " + e.getMessage());
            }

            try {
                // Locate the submit button
                WebElement submitButton = wait.until(
                        ExpectedConditions.elementToBeClickable(By.xpath(SUBMIT_BUTTON_XPATH))
                );
                // Scroll to the button to ensure it's in view
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].scrollIntoView({block: 'center'});", submitButton);
                // Check if the button is part of a form and submit the form
                WebElement form = (WebElement) ((JavascriptExecutor) driver).executeScript(
                        "return arguments[0].closest('form');", submitButton
                );
                if (form != null) {
                    // Trigger form submission
                    ((JavascriptExecutor) driver).executeScript("arguments[0].submit();", form);
                    System.out.println("Submitted the form containing the 'submit' button");
                } else {
                    // Fallback: Trigger a click with JavaScript
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].dispatchEvent(new Event('click', { bubbles: true }));", submitButton
                    );
                    System.out.println("Triggered click event on the 'submit' button");
                }

                // Wait for confirmation message or UI change
                try {
                    WebElement confirmationMessage = wait.until(
                            ExpectedConditions.visibilityOfElementLocated(By.xpath(CONFIRMATION_MESSAGE_XPATH))
                    );
                    System.out.println("Confirmation message found: " + confirmationMessage.getText());
                    System.out.println("Remained on the same page with differences: " + driver.getCurrentUrl());
                } catch (Exception e) {
                    System.out.println("Could not find confirmation message: " + e.getMessage());
                    // Print page source for debugging
                    System.out.println("Page source:\n" + driver.getPageSource());
                }

            } catch (Exception e) {
                System.out.println("Could not click the 'submit' button: " + e.getMessage());
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
        Thread.sleep(SLEEP_MILLIS);
    }
}