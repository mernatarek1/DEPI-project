package com.example.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
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

public class AdminLoginTest {
    private static final String WEBSITE_URL = "https://automationintesting.online";
    private static final String ADMIN_LINK_TEXT = "Admin";
    private static final String USERNAME_FIELD_XPATH = "//*[@id='username']";
    private static final String PASSWORD_FIELD_XPATH = "//*[@id='password']";
    private static final String LOGIN_BUTTON_XPATH = "//button[text()='Login']";
    private static final String VALID_USERNAME = "admin";
    private static final String INVALID_USERNAME = "not admin";
    private static final String VALID_PASSWORD = "password123";
    private static final String INVALID_PASSWORD = "pass5";
    private static final String INVALID_PASSWORD_but_open = "password";
    private static final int WAIT_TIMEOUT_SECONDS = 60;
    private static final int SHORT_WAIT_SECONDS = 5; // Increased to 5 to address test failures
    private static final int FIELD_WAIT_SECONDS = 20;
    private static final int TYPING_SPEED_MS = 50;
    private static final int POLL_INTERVAL_MS = 50;
    private static final String LOGOUT_BUTTON_XPATH = "//button[contains(@class, 'btn-outline-danger') and contains(text(), 'Logout')]";

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
    }

    @AfterMethod
    public void tearDown() {
        logoutAfterTest();
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

    private void navigateToLoginPage() {
        test.info("Navigating to the login page");
        driver.get(WEBSITE_URL);
        System.out.println("Navigated to homepage. Current URL: " + driver.getCurrentUrl());
        WebElement adminLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(ADMIN_LINK_TEXT)));
        adminLink.click();
        wait.until(ExpectedConditions.urlContains("/admin"));
        System.out.println("Clicked Admin link. Current URL: " + driver.getCurrentUrl());
    }

    private void fillField(String xpath, String value, String fieldName) {
        test.info("Filling " + fieldName + " field with value: " + value);
        WebElement field = fieldWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        field.clear();
        System.out.println("Filling " + fieldName + " field with value: " + value);
        for (char c : value.toCharArray()) {
            field.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(TYPING_SPEED_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Filled " + fieldName + " field.");
    }

    private void submitLoginForm() {
        test.info("Submitting the login form");
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(LOGIN_BUTTON_XPATH)));
        loginButton.click();
        System.out.println("Clicked the 'Login' button");
        System.out.println("Waiting for dashboard to load...");
    }

    private boolean isInvalidCredentialsDisplayed() {
        try {
            WebElement errorMessage = shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Invalid credentials')]")));
            System.out.println("Invalid credentials message detected: " + errorMessage.getText());
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            System.out.println("Invalid credentials message not found. Error: " + e.getMessage());
            return false;
        }
    }

    private boolean isDashboardDisplayed() {
        try {
            WebElement roomListing = shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-testid='roomlisting')]")));
            return roomListing.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void logoutAfterTest() {
        if (isDashboardDisplayed() || isInvalidCredentialsDisplayed()) {
            try {
                WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(LOGOUT_BUTTON_XPATH)));
                logoutButton.click();
                wait.until(ExpectedConditions.urlContains("/"));
                System.out.println("Logged out successfully after the test.");
                test.info("Logged out successfully.");
            } catch (Exception e) {
                System.err.println("Error during logout: " + e.getMessage());
                test.info("Error during logout: " + e.getMessage());
            }
        } else if (driver != null && driver.getCurrentUrl().contains("/admin")) {
            System.out.println("Login failed, no need to logout.");
            test.info("Login failed, no need to logout.");
        }
    }

    @Test(priority = 1, description = "TC01: Verify admin login with valid credentials")
    public void testValidAdminLogin() throws InterruptedException {

        test.info("Starting test: Verify admin login with valid credentials");
        navigateToLoginPage();
        fillField(USERNAME_FIELD_XPATH, VALID_USERNAME, "username");
        fillField(PASSWORD_FIELD_XPATH, VALID_PASSWORD, "password");
        submitLoginForm();
        boolean invalidCredentialsDisplayed = isInvalidCredentialsDisplayed();
        boolean dashboardDisplayed = isDashboardDisplayed();

        if (!invalidCredentialsDisplayed && dashboardDisplayed) {
            test.pass("Login successful: Invalid credentials message not displayed, and dashboard displayed.");
        } else {
            test.fail("Login failed: Invalid credentials message displayed: " + invalidCredentialsDisplayed +
                    ", Dashboard displayed: " + dashboardDisplayed);
        }

        Assert.assertFalse(invalidCredentialsDisplayed, "Invalid credentials message should not be displayed.");
        Assert.assertTrue(dashboardDisplayed, "Admin dashboard should be displayed after successful login.");
    }

    @Test(priority = 2 ,description = "TC02: Verify login with invalid username/password")
    public void testInvalidAdminLogin() throws InterruptedException {

        test.info("Starting test: Verify login with invalid username/password");
        navigateToLoginPage();
        fillField(USERNAME_FIELD_XPATH, INVALID_USERNAME, "username");
        fillField(PASSWORD_FIELD_XPATH, INVALID_PASSWORD, "password");
        submitLoginForm();
        boolean invalidCredentialsDisplayed = isInvalidCredentialsDisplayed();
        boolean dashboardDisplayed = isDashboardDisplayed();

        if (invalidCredentialsDisplayed && !dashboardDisplayed) {
            test.pass("Invalid login handled correctly: Invalid credentials message displayed, and dashboard not displayed.");
        } else {
            test.fail("Invalid login failed: Invalid credentials message displayed: " + invalidCredentialsDisplayed +
                    ", Dashboard displayed: " + dashboardDisplayed);
        }

        Assert.assertTrue(invalidCredentialsDisplayed, "Invalid credentials message should be displayed.");
        Assert.assertFalse(dashboardDisplayed, "Dashboard should not be displayed with invalid credentials.");
    }

    @Test(priority = 3 ,description = "TC03: Verify login with blank username/password")
    public void testNoDataadminLogin() throws InterruptedException {

        test.info("Starting test: Verify login with blank username/password");
        navigateToLoginPage();
        submitLoginForm();
        boolean invalidCredentialsDisplayed = isInvalidCredentialsDisplayed();
        boolean dashboardDisplayed = isDashboardDisplayed();

        if (invalidCredentialsDisplayed && !dashboardDisplayed) {
            test.pass("Blank login handled correctly: Invalid credentials message displayed, and dashboard not displayed.");
        } else {
            test.fail("Blank login failed: Invalid credentials message displayed: " + invalidCredentialsDisplayed +
                    ", Dashboard displayed: " + dashboardDisplayed);
        }

        Assert.assertTrue(invalidCredentialsDisplayed, "Invalid credentials message should be displayed for blank fields.");
        Assert.assertFalse(dashboardDisplayed, "Dashboard should not be displayed with blank credentials.");
    }

    @Test(priority = 4 ,description = "TC04: Verify login with invalid password")
    public void testInvalidAdminLoginbutpass() throws InterruptedException {

        test.info("Starting test: Verify login with invalid password");
        navigateToLoginPage();
        fillField(USERNAME_FIELD_XPATH, VALID_USERNAME, "username");
        fillField(PASSWORD_FIELD_XPATH, INVALID_PASSWORD_but_open, "password");
        submitLoginForm();
        boolean invalidCredentialsDisplayed = isInvalidCredentialsDisplayed();
        boolean dashboardDisplayed = isDashboardDisplayed();

        if (invalidCredentialsDisplayed && !dashboardDisplayed) {
            test.pass("Invalid password handled correctly: Invalid credentials message displayed, and dashboard not displayed.");
        } else {
            test.fail("Invalid password login failed: Invalid credentials message displayed: " + invalidCredentialsDisplayed +
                    ", Dashboard displayed: " + dashboardDisplayed);
        }

        Assert.assertTrue(invalidCredentialsDisplayed, "Invalid credentials message should be displayed.");
        Assert.assertFalse(dashboardDisplayed, "Dashboard should not be displayed with invalid password.");
    }
}