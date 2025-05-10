package com.example.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import utils.ExtentReportManager;

import java.time.Duration;
import java.util.List;

public class send_msg {
    private static final String WEBSITE_URL = "https://automationintesting.online";
    private static final int WAIT_TIMEOUT_SECONDS = 60;
    private static final int SHORT_WAIT_SECONDS = 5;
    private static final int FIELD_WAIT_SECONDS = 20;
    private static final int TYPING_SPEED_MS = 50;
    private static final int POLL_INTERVAL_MS = 50;
    private static final int POST_CLICK_WAIT_MS = 1000; // Add this constant for wait after click

    // Contact Form Locators
    private static final String CONTACT_LINK_XPATH = "//a[contains(text(), 'Contact')]";
    private static final String CONTACT_SECTION_XPATH = "//section[@id='contact']";
    private static final String NAME_FIELD_XPATH = "//input[@data-testid='ContactName']";
    private static final String EMAIL_FIELD_XPATH = "//input[@data-testid='ContactEmail']";
    private static final String PHONE_FIELD_XPATH = "//input[@data-testid='ContactPhone']";
    private static final String SUBJECT_FIELD_XPATH = "//input[@data-testid='ContactSubject']";
    private static final String MESSAGE_FIELD_XPATH = "//textarea[@data-testid='ContactDescription']";
    private static final String SUBMIT_BUTTON_XPATH = "//button[contains(@class, 'btn-primary') and text()='Submit']";
    private static final String SUCCESS_MESSAGE_XPATH = "//h3[contains(text(), 'Thanks for getting in touch')]";
    private static final String ERROR_MESSAGE_XPATH = "//div[contains(@class, 'alert-danger')]//p";

    private WebDriver driver;
    private WebDriverWait wait;
    private WebDriverWait shortWait;
    private WebDriverWait fieldWait;
    private ExtentReports report;
    private ExtentTest test;

    @BeforeClass
    public void setupReport() {
        report = ExtentReportManager.getReportInstance();
    }

    @BeforeMethod
    public void setUpTest(ITestContext context) {
        String testName = context.getCurrentXmlTest().getName();
        test = report.createTest(testName);
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT_SECONDS), Duration.ofMillis(POLL_INTERVAL_MS));
        shortWait = new WebDriverWait(driver, Duration.ofSeconds(SHORT_WAIT_SECONDS), Duration.ofMillis(POLL_INTERVAL_MS));
        fieldWait = new WebDriverWait(driver, Duration.ofSeconds(FIELD_WAIT_SECONDS), Duration.ofMillis(POLL_INTERVAL_MS));
        user();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @AfterClass
    public void closeReport() {
        if (report != null) {
            report.flush();
        }
    }

    private void user() {
        test.info("I am a user navigating the website");
        driver.get(WEBSITE_URL);
        // Scroll to the contact section
        WebElement contactSection = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(CONTACT_SECTION_XPATH)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", contactSection);
        test.info("Scrolled to the contact section");
    }

    private void navigateToContactPage() {
        test.info("Navigating to Contact page");
        WebElement contactLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(CONTACT_LINK_XPATH)));
        contactLink.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(NAME_FIELD_XPATH)));
        test.info("Successfully navigated to Contact page");
    }

    private void fillField(String xpath, String value, String fieldName) {
        test.info("Filling " + fieldName + " field with value: " + value);
        WebElement field = fieldWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        field.clear();
        for (char c : value.toCharArray()) {
            field.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(TYPING_SPEED_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void clickElement(WebElement element) {
        try {
            // Scroll the element into view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            // Wait for it to be clickable
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            test.info("Clicked element successfully");
            // Add a small wait after the click
            Thread.sleep(POST_CLICK_WAIT_MS);
        } catch (Exception e) {
            test.info("Regular click failed, attempting JavaScript click: " + e.getMessage());
            // Fallback to JavaScript click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            test.info("JavaScript click executed successfully");
            // Add a small wait after the JavaScript click
            try {
                Thread.sleep(POST_CLICK_WAIT_MS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test(description = "TC05: Submit valid contact form (name, email, phone, subject, description) → verify success message")
    public void testSubmitValidContactForm() {
        test.info("Starting test: Submit valid contact form");
        navigateToContactPage();

        String name = "John Doe";
        fillField(NAME_FIELD_XPATH, name, "name");
        fillField(EMAIL_FIELD_XPATH, "john.doe@example.com", "email");
        fillField(PHONE_FIELD_XPATH, "123456789012", "phone"); // 12 chars, within 11-21 range
        fillField(SUBJECT_FIELD_XPATH, "This is a valid subject line", "subject"); // 27 chars, within 5-100 range
        fillField(MESSAGE_FIELD_XPATH, "This is a valid test message to ensure the form submission works as expected.", "message"); // 74 chars, within 20-2000 range

        WebElement submitButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(SUBMIT_BUTTON_XPATH)));
        clickElement(submitButton);
        test.info("Submitted valid contact form");

        WebElement successMessageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(SUCCESS_MESSAGE_XPATH)));
        boolean successMessageDisplayed = successMessageElement.isDisplayed();
        String successMessageText = successMessageElement.getText();
        String expectedSuccessMessage = "Thanks for getting in touch " + name + "!";

        if (successMessageDisplayed && successMessageText.contains(expectedSuccessMessage)) {
            test.pass("Success message displayed: " + successMessageText);
        } else {
            test.fail("Success message not displayed or incorrect. Expected: " + expectedSuccessMessage + ", but found: " + successMessageText);
        }
        Assert.assertTrue(successMessageDisplayed, "Success message should be displayed for valid form submission.");
        Assert.assertTrue(successMessageText.contains(expectedSuccessMessage), "Success message should contain: " + expectedSuccessMessage);
    }

    @Test(description = "TC06: Submit form with missing fields → verify validation errors")
    public void testSubmitFormWithMissingFields() {
        test.info("Starting test: Submit form with missing fields");
        navigateToContactPage();

        // Fill only some fields, leave others blank
        fillField(NAME_FIELD_XPATH, "Jane Doe", "name");
        fillField(EMAIL_FIELD_XPATH, "jane.doe@example.com", "email");
        // Leave phone, subject, and message blank

        WebElement submitButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(SUBMIT_BUTTON_XPATH)));
        clickElement(submitButton);
        test.info("Submitted form with missing fields");

        List<WebElement> errorMessages = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(ERROR_MESSAGE_XPATH)));
        boolean validationErrorDisplayed = !errorMessages.isEmpty();
        boolean phoneErrorFound = false;
        boolean subjectErrorFound = false;
        boolean messageErrorFound = false;

        for (WebElement error : errorMessages) {
            String errorText = error.getText();
            if (errorText.equals("Phone may not be blank")) phoneErrorFound = true;
            if (errorText.equals("Subject may not be blank")) subjectErrorFound = true;
            if (errorText.equals("Message may not be blank")) messageErrorFound = true;
        }

        if (validationErrorDisplayed && phoneErrorFound && subjectErrorFound && messageErrorFound) {
            test.pass("Validation errors displayed for missing fields: Phone, Subject, and Message.");
        } else {
            test.fail("Expected validation errors not fully displayed. Phone error: " + phoneErrorFound + ", Subject error: " + subjectErrorFound + ", Message error: " + messageErrorFound);
        }
        Assert.assertTrue(validationErrorDisplayed, "Validation errors should be displayed when required fields are missing.");
        Assert.assertTrue(phoneErrorFound && subjectErrorFound && messageErrorFound, "Specific errors for Phone, Subject, and Message should be displayed.");
    }

    @Test(description = "TC07: Submit invalid email format and field lengths → verify error")
    public void testSubmitInvalidEmailFormat() {
        test.info("Starting test: Submit invalid email format and field lengths");
        navigateToContactPage();

        fillField(NAME_FIELD_XPATH, "Alice Smith", "name");
        fillField(EMAIL_FIELD_XPATH, "invalid-email", "email"); // Invalid email format
        fillField(PHONE_FIELD_XPATH, "12345", "phone"); // 5 chars, less than 11
        fillField(SUBJECT_FIELD_XPATH, "Hi", "subject"); // 2 chars, less than 5
        fillField(MESSAGE_FIELD_XPATH, "Short", "message"); // 5 chars, less than 20

        WebElement submitButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(SUBMIT_BUTTON_XPATH)));
        clickElement(submitButton);
        test.info("Submitted form with invalid email format and field lengths");

        List<WebElement> errorMessages = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(ERROR_MESSAGE_XPATH)));
        boolean validationErrorDisplayed = !errorMessages.isEmpty();
        boolean emailErrorFound = false;
        boolean phoneLengthErrorFound = false;
        boolean subjectLengthErrorFound = false;
        boolean messageLengthErrorFound = false;

        for (WebElement error : errorMessages) {
            String errorText = error.getText();
            if (errorText.equals("Email may not be blank") || errorText.contains("must be a valid email")) emailErrorFound = true;
            if (errorText.equals("Phone must be between 11 and 21 characters.")) phoneLengthErrorFound = true;
            if (errorText.equals("Subject must be between 5 and 100 characters.")) subjectLengthErrorFound = true;
            if (errorText.equals("Message must be between 20 and 2000 characters.")) messageLengthErrorFound = true;
        }

        if (validationErrorDisplayed && emailErrorFound && phoneLengthErrorFound && subjectLengthErrorFound && messageLengthErrorFound) {
            test.pass("Validation errors displayed for invalid email, phone length, subject length, and message length.");
        } else {
            test.fail("Expected validation errors not fully displayed. Email error: " + emailErrorFound + ", Phone length error: " + phoneLengthErrorFound + ", Subject length error: " + subjectLengthErrorFound + ", Message length error: " + messageLengthErrorFound);
        }
        Assert.assertTrue(validationErrorDisplayed, "Validation errors should be displayed for invalid email and field lengths.");
        Assert.assertTrue(emailErrorFound && phoneLengthErrorFound && subjectLengthErrorFound && messageLengthErrorFound, "Specific errors for email, phone, subject, and message lengths should be displayed.");
    }
}