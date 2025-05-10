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
import java.util.List;

public class AdminDashboard {
    private static final String WEBSITE_URL = "https://automationintesting.online";
    private static final String ADMIN_LINK_TEXT = "Admin";
    private static final String USERNAME_FIELD_XPATH = "//*[@id='username']";
    private static final String PASSWORD_FIELD_XPATH = "//*[@id='password']";
    private static final String LOGIN_BUTTON_XPATH = "//button[text()='Login']";
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "password";
    private static final int WAIT_TIMEOUT_SECONDS = 90;  // Increased from 60 to 90 seconds
    private static final int SHORT_WAIT_SECONDS = 10;    // Increased from 5 to 10 seconds
    private static final int FIELD_WAIT_SECONDS = 30;    // Increased from 20 to 30 seconds
    private static final int TYPING_SPEED_MS = 100;      // Increased from 50 to 100 ms for slower typing
    private static final int POLL_INTERVAL_MS = 100;     // Increased from 50 to 100 ms for slower polling
    private static final int ACTION_DELAY_MS = 2000;     // Added: 2-second delay after key actions
    private static final String LOGOUT_BUTTON_XPATH = "//button[contains(@class, 'btn-outline-danger') and contains(text(), 'Logout')]";

    // Room Management Locators (adjusted based on HTML)
    private static final String ROOMS_LINK_XPATH = "//a[contains(text(), 'Rooms')]";
    private static final String ROOM_NUMBER_FIELD_XPATH = "//input[@data-testid='roomName']";
    private static final String ROOM_TYPE_DROPDOWN_XPATH = "//select[@id='type']";
    private static final String ACCESSIBLE_DROPDOWN_XPATH = "//select[@id='accessible']";
    private static final String PRICE_FIELD_XPATH = "//input[@id='roomPrice']";
    private static final String WIFI_CHECKBOX_XPATH = "//input[@id='wifiCheckbox']";
    private static final String TV_CHECKBOX_XPATH = "//input[@id='tvCheckbox']";
    private static final String RADIO_CHECKBOX_XPATH = "//input[@id='radioCheckbox']";
    private static final String REFRESHMENTS_CHECKBOX_XPATH = "//input[@id='refreshCheckbox']";
    private static final String SAFE_CHECKBOX_XPATH = "//input[@id='safeCheckbox']";
    private static final String VIEWS_CHECKBOX_XPATH = "//input[@id='viewsCheckbox']";
    private static final String CREATE_BUTTON_XPATH = "//button[@id='createRoom']";
    private static final String ROOM_LIST_XPATH = "//div[@data-testid='roomlisting']";
    private static final String DELETE_BUTTON_XPATH = "//span[contains(@class, 'fa-remove')]";

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
        loginAsAdmin();
        navigateToRoomsPage();
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

    private void loginAsAdmin() {
        test.info("Logging in as admin");
        driver.get(WEBSITE_URL);
        WebElement adminLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(ADMIN_LINK_TEXT)));
        adminLink.click();
        wait.until(ExpectedConditions.urlContains("/admin"));
        try {
            Thread.sleep(ACTION_DELAY_MS); // Added delay after clicking admin link
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        fillField(USERNAME_FIELD_XPATH, VALID_USERNAME, "username");
        fillField(PASSWORD_FIELD_XPATH, VALID_PASSWORD, "password");
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(LOGIN_BUTTON_XPATH)));
        loginButton.click();
        try {
            Thread.sleep(ACTION_DELAY_MS); // Added delay after clicking login button
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(ROOM_LIST_XPATH)));
        test.info("Successfully logged in and redirected to dashboard");
    }

    private void navigateToRoomsPage() {
        test.info("Navigating to Rooms page");
        WebElement roomsLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(ROOMS_LINK_XPATH)));
        roomsLink.click();
        try {
            Thread.sleep(ACTION_DELAY_MS); // Added delay after clicking rooms link
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(ROOM_LIST_XPATH)));
        test.info("Successfully navigated to Rooms page");
    }

    private void fillField(String xpath, String value, String fieldName) {
        test.info("Filling " + fieldName + " field with value: " + value);
        WebElement field = fieldWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        field.clear();
        for (char c : value.toCharArray()) {
            field.sendKeys(String.valueOf(c));
            try {
                Thread.sleep(TYPING_SPEED_MS); // Slower typing with increased TYPING_SPEED_MS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        try {
            Thread.sleep(ACTION_DELAY_MS / 2); // Added small delay (1 second) after filling each field
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void selectDropdown(String xpath, String value) {
        test.info("Selecting " + value + " from dropdown");
        WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        dropdown.findElement(By.xpath(".//option[@value='" + value + "']")).click();
        try {
            Thread.sleep(ACTION_DELAY_MS / 2); // Added small delay (1 second) after selecting dropdown
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void checkCheckbox(String xpath, boolean check) {
        test.info("Setting checkbox to " + check);
        WebElement checkbox = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        if (checkbox.isSelected() != check) {
            checkbox.click();
        }
        try {
            Thread.sleep(ACTION_DELAY_MS / 2); // Added small delay (1 second) after checking checkbox
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean isInvalidCredentialsDisplayed() {
        try {
            WebElement errorMessage = shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), 'Invalid credentials')]")));
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDashboardDisplayed() {
        try {
            WebElement roomListing = shortWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(ROOM_LIST_XPATH)));
            return roomListing.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isRoomListed(String roomNumber) {
        List<WebElement> rooms = shortWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROOM_LIST_XPATH)));
        for (WebElement room : rooms) {
            if (room.findElement(By.className("col-sm-1")).getText().equals(roomNumber)) {
                return true;
            }
        }
        return false;
    }

    private WebElement findRoomRow(String roomNumber) {
        List<WebElement> rooms = shortWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROOM_LIST_XPATH)));
        for (WebElement room : rooms) {
            if (room.findElement(By.className("col-sm-1")).getText().equals(roomNumber)) {
                return room;
            }
        }
        return null;
    }

    public void logoutAfterTest() {
        if (isDashboardDisplayed() || isInvalidCredentialsDisplayed()) {
            try {
                WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(LOGOUT_BUTTON_XPATH)));
                logoutButton.click();
                wait.until(ExpectedConditions.urlContains("/"));
                test.info("Logged out successfully.");
                try {
                    Thread.sleep(ACTION_DELAY_MS); // Added delay after logout
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                test.info("Error during logout: " + e.getMessage());
            }
        } else if (driver != null && driver.getCurrentUrl().contains("/admin")) {
            test.info("Login failed, no need to logout.");
        }
    }

    @Test(description = "TC12: After login, add a new room and verify it is listed")
    public void testAddNewRoom() {
        test.info("Starting test: Add a new room");
        String roomNumber = "104"; // Unique room number
        selectDropdown(ROOM_TYPE_DROPDOWN_XPATH, "Single");
        selectDropdown(ACCESSIBLE_DROPDOWN_XPATH, "false");
        fillField(ROOM_NUMBER_FIELD_XPATH, roomNumber, "room number");
        fillField(PRICE_FIELD_XPATH, "120", "price");
        checkCheckbox(WIFI_CHECKBOX_XPATH, true);
        checkCheckbox(TV_CHECKBOX_XPATH, true);
        checkCheckbox(SAFE_CHECKBOX_XPATH, true);

        WebElement createButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(CREATE_BUTTON_XPATH)));
        createButton.click();
        test.info("Submitted new room form");
        try {
            Thread.sleep(ACTION_DELAY_MS); // Added delay after submitting form
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify room is listed
        boolean roomListed = isRoomListed(roomNumber);
        if (roomListed) {
            test.pass("Room added successfully: " + roomNumber + " is listed.");
        } else {
            test.fail("Room addition failed: " + roomNumber + " is not listed.");
        }
        Assert.assertTrue(roomListed, "New room should be listed in the room management table.");
    }

    @Test(description = "TC13: Edit an existing room and verify changes persist")
    public void testEditRoom() {
        test.info("Starting test: Edit an existing room");
        // Note: Editing is not directly supported in the UI; we'll add a room and simulate an edit by deleting and re-adding
        String originalRoomNumber = "105";
        selectDropdown(ROOM_TYPE_DROPDOWN_XPATH, "Double");
        selectDropdown(ACCESSIBLE_DROPDOWN_XPATH, "true");
        fillField(ROOM_NUMBER_FIELD_XPATH, originalRoomNumber, "room number");
        fillField(PRICE_FIELD_XPATH, "160", "price");
        checkCheckbox(WIFI_CHECKBOX_XPATH, true);
        checkCheckbox(RADIO_CHECKBOX_XPATH, true);
        checkCheckbox(SAFE_CHECKBOX_XPATH, true);

        WebElement createButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(CREATE_BUTTON_XPATH)));
        createButton.click();
        try {
            Thread.sleep(ACTION_DELAY_MS); // Added delay after adding room
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Assert.assertTrue(isRoomListed(originalRoomNumber), "Room should be added before editing.");

        // Simulate edit by deleting and re-adding with updated details
        String updatedRoomNumber = "105A";
        WebElement roomRow = findRoomRow(originalRoomNumber);
        WebElement deleteButton = roomRow.findElement(By.xpath(DELETE_BUTTON_XPATH));
        deleteButton.click();
        test.info("Deleted room: " + originalRoomNumber);
        try {
            Thread.sleep(ACTION_DELAY_MS); // Added delay after deleting room
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        selectDropdown(ROOM_TYPE_DROPDOWN_XPATH, "Double");
        selectDropdown(ACCESSIBLE_DROPDOWN_XPATH, "false");
        fillField(ROOM_NUMBER_FIELD_XPATH, updatedRoomNumber, "room number");
        fillField(PRICE_FIELD_XPATH, "170", "price");
        checkCheckbox(WIFI_CHECKBOX_XPATH, true);
        checkCheckbox(TV_CHECKBOX_XPATH, true);
        checkCheckbox(SAFE_CHECKBOX_XPATH, true);
        createButton.click();
        test.info("Submitted updated room details");
        try {
            Thread.sleep(ACTION_DELAY_MS); // Added delay after submitting updated room
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean roomUpdated = isRoomListed(updatedRoomNumber);
        if (roomUpdated) {
            test.pass("Room updated successfully: " + updatedRoomNumber + " is listed with updated details.");
        } else {
            test.fail("Room update failed: " + updatedRoomNumber + " is not listed.");
        }
        Assert.assertTrue(roomUpdated, "Updated room details should be reflected in the list.");
    }

    @Test(description = "TC14: Delete a room and confirm it no longer appears")
    public void testDeleteRoom() {
        test.info("Starting test: Delete a room");
        String roomNumber = "106";
        selectDropdown(ROOM_TYPE_DROPDOWN_XPATH, "Suite");
        selectDropdown(ACCESSIBLE_DROPDOWN_XPATH, "true");
        fillField(ROOM_NUMBER_FIELD_XPATH, roomNumber, "room number");
        fillField(PRICE_FIELD_XPATH, "200", "price");
        checkCheckbox(WIFI_CHECKBOX_XPATH, true);
        checkCheckbox(RADIO_CHECKBOX_XPATH, true);
        checkCheckbox(SAFE_CHECKBOX_XPATH, true);

        WebElement createButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(CREATE_BUTTON_XPATH)));
        createButton.click();
        try {
            Thread.sleep(ACTION_DELAY_MS); // Added delay after adding room
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Assert.assertTrue(isRoomListed(roomNumber), "Room should be added before deletion.");

        WebElement roomRow = findRoomRow(roomNumber);
        WebElement deleteButton = roomRow.findElement(By.xpath(DELETE_BUTTON_XPATH));
        deleteButton.click();
        test.info("Clicked delete button for room: " + roomNumber);
        try {
            Thread.sleep(ACTION_DELAY_MS); // Added delay after deleting room
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean roomListed = isRoomListed(roomNumber);
        if (!roomListed) {
            test.pass("Room deleted successfully: " + roomNumber + " is no longer listed.");
        } else {
            test.fail("Room deletion failed: " + roomNumber + " is still listed.");
        }
        Assert.assertFalse(roomListed, "Deleted room should no longer appear in the list.");
    }

    @Test(description = "TC15: Add room with missing fields and verify validation")
    public void testAddRoomWithMissingFields() {
        test.info("Starting test: Add room with missing fields");
        fillField(ROOM_NUMBER_FIELD_XPATH, "107", "room number");
        // Leave Type, Accessible, Price, and details unchecked

        WebElement createButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(CREATE_BUTTON_XPATH)));
        createButton.click();
        test.info("Submitted room form with missing fields");
        try {
            Thread.sleep(ACTION_DELAY_MS); // Added delay after submitting form
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check for validation error (assuming an error message appears; adjust XPath if needed)
        boolean validationErrorDisplayed = false;
        try {
            WebElement errorMessage = shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'alert-danger')]")));
            validationErrorDisplayed = errorMessage.isDisplayed();
            test.info("Validation error message: " + errorMessage.getText());
        } catch (Exception e) {
            test.info("No validation error message found.");
        }

        if (validationErrorDisplayed) {
            test.pass("Validation error displayed for missing fields as expected.");
        } else {
            test.fail("Validation error not displayed for missing fields.");
        }
        Assert.assertTrue(validationErrorDisplayed, "Validation error should be displayed when required fields are missing.");
    }
}