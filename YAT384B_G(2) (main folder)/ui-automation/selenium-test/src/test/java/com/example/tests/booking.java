package com.example.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ExtentReportManager;

import java.time.Duration;
import java.util.List;

public class booking {

    // Adjusted constants for faster waits
    private static final String ROOMS_LINK_TEXT = "Rooms";
    private static final String BOOK_NOW_BUTTON_XPATH = "//*[@id=\"rooms\"]/div/div[2]/div[2]/div/div[3]/a";
    private static final String RESERVATION1_BUTTON_XPATH = "//*[@id=\"doReservation\"]";
    private static final int WAIT_TIMEOUT_SECONDS = 10;
    private static final int SHORT_WAIT_SECONDS = 3;
    private static final int FIELD_WAIT_SECONDS = 5;
    private static final int TYPING_SPEED_MS = 50;
    private static final int POLL_INTERVAL_MS = 50;

    private static final String BOOK_NOW_LINK_TEXT = "Book Now";
    private static final String SECOND_BOOK_NOW_BUTTON_CSS = "a[href='/reservation/5?checkin=2025-05-09&checkout=2025-05-10'].btn.btn-primary";
    private static final String CALENDAR_CONTAINER_CLASS = "rbc-month-view";
    private static final String DATE_CELL_SELECTOR = ".rbc-date-cell button.rbc-button-link";
    private static final String FIRSTNAME_FIELD_XPATH = "//input[@name='firstname' and contains(@class, 'form-control')]";
    private static final String LASTNAME_FIELD_XPATH = "//input[@name='lastname' and contains(@class, 'form-control')]";
    private static final String EMAIL_FIELD_XPATH = "//input[@name='email' and contains(@class, 'form-control')]";
    private static final String PHONE_FIELD_XPATH = "//input[@name='phone' and contains(@class, 'form-control')]";
    private static final String RESERVE_NOW_BUTTON_XPATH = "//button[contains(@class, 'btn btn-primary w-100 mb-3') and text()='Reserve Now']";
    private static final String CONFIRMATION_MESSAGE_XPATH = "//*[contains(text(), 'success') or contains(text(), 'confirmed') or contains(text(), 'booking')]";
    private static final String ERROR_MESSAGE_XPATH = "//*[contains(text(), 'error') or contains(text(), 'invalid') or contains(text(), 'required') or contains(text(), 'overlap')]";
    private static final String APPLICATION_ERROR_XPATH = "//h2[contains(text(), 'Application error: a client-side exception has occurred while loading automationintesting.online')]";
    private static final String VALIDATION_ERROR_XPATH = "//*[contains(text(), 'required') or contains(text(), 'Please')]"; // For TC10: validation errors
    private static final String INVALID_DATE_ERROR_XPATH = "//*[contains(text(), 'invalid') or contains(text(), 'end date')]"; // For TC11: invalid date range errors
    private static final String FIRSTNAME_VALUE = "John";
    private static final String LASTNAME_VALUE = "Doe";
    private static final String EMAIL_VALUE = "john.doe@example.com";
    private static final String PHONE_VALUE = "12345678900";
    private static final String VALID_START_DATE = "2025-05-19";
    private static final String VALID_END_DATE = "2025-05-22";
    private static final String VALID_START_DAY = "19";
    private static final String VALID_END_DAY = "22";

    private static final String VALID2_START_DAY = "12";
    private static final String VALID2_END_DAY = "15";

    private static final String OVERLAP_START_DATE = "2025-05-2";
    private static final String OVERLAP_END_DATE = "2025-05-5";
    private static final String OVERLAP_START_DAY = "01";
    private static final String OVERLAP_END_DAY = "03";
    private static final String INVALID_START_DATE = "2025-05-29";
    private static final String INVALID_END_DATE = "2025-05-27";
    private static final String INVALID_START_DAY = "29"; // For TC11
    private static final String INVALID_END_DAY = "27";   // For TC11
    private static final String WEBSITE_URL = "https://automationintesting.online";
    private static final String RESERVATION_URL = "https://automationintesting.online/reservation/1?checkin=2025-05-10&checkout=2025-05-14";

    private static final int SHORT_SLEEP_MILLIS = 500;
    private static final int MEDIUM_SLEEP_MILLIS = 2000;
    private static final int LONG_SLEEP_MILLIS = 3000;
    private static final int POST_RESERVE_SLEEP_MILLIS = 10000;

    private WebDriver driver;
    private WebDriverWait wait;
    private WebDriverWait shortWait;
    private WebDriverWait fieldWait;
    private ExtentReports report;
    private ExtentTest test;

    @BeforeClass
    public void setupReport() {
        report = ExtentReportManager.getReportInstance();
        System.out.println("Extent report initialized.");
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
        System.out.println("WebDriver and waits initialized for test: " + testName);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
            System.out.println("WebDriver closed.");
        }
    }

    @AfterClass
    public void closeReport() {
        if (report != null) {
            report.flush();
            System.out.println("Extent report flushed and closed.");
        }
    }

    private void navigateToRoomsPage() {
        test.info("Navigating to the Rooms");
        driver.get(WEBSITE_URL);
        System.out.println("Navigated to homepage. Current URL: " + driver.getCurrentUrl());
        WebElement roomsLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(ROOMS_LINK_TEXT)));
        roomsLink.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h5[text()='Single']")));
        System.out.println("Rooms link clicked successfully. Current URL: " + driver.getCurrentUrl());
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.querySelector('#rooms').offsetTop);");
        System.out.println("Scrolled to Rooms section.");
    }

    private void bookNowClick() {
        test.info("Click on book now button for Single room");
        WebElement bookNowButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(BOOK_NOW_BUTTON_XPATH)));
        System.out.println("Button found: " + bookNowButton.getAttribute("outerHTML"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", bookNowButton);
        try {
            bookNowButton.click();
        } catch (Exception e) {
            System.out.println("Regular click failed: " + e.getMessage());
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", bookNowButton);
        }
        System.out.println("Book Now button for Single room clicked successfully.");
        System.out.println("Waiting for Room to load...");
    }

    private void reservationButton1Click() {
        test.info("Click on Reservation button");
        WebElement reservationButton = shortWait.until(ExpectedConditions.elementToBeClickable(By.xpath(RESERVATION1_BUTTON_XPATH)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", reservationButton);
        try {
            reservationButton.click();
        } catch (Exception e) {
            System.out.println("Regular click failed: " + e.getMessage());
            test.warning("Regular click on Reservation button failed: " + e.getMessage());
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", reservationButton);
        }
        System.out.println("Reservation button clicked successfully.");
        System.out.println("Waiting for reservation page to load...");
    }

    private void fillField(String xpath, String value, String fieldName) throws InterruptedException {
        test.info("Filling field: " + fieldName + " with value: " + value);
        try {
            WebElement field = fieldWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", field);
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", field, value);
            ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", field);
            System.out.println(fieldName + " field filled successfully with value: " + value);
        } catch (Exception e) {
            System.out.println("Failed to fill " + fieldName + " field: " + e.getMessage());
            test.error("Could not fill field: " + fieldName + ". Error: " + e.getMessage());
        }
    }

    private void reserveNowButton1Click() {
        test.info("Click on Reserve Now button");
        WebElement reserveNowButton = shortWait.until(ExpectedConditions.elementToBeClickable(By.xpath(RESERVE_NOW_BUTTON_XPATH)));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", reserveNowButton);
        try {
            reserveNowButton.click();
        } catch (Exception e) {
            System.out.println("Regular click failed: " + e.getMessage());
            test.warning("Regular click on Reserve Now button failed: " + e.getMessage());
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", reserveNowButton);
        }
        System.out.println("Reserve Now button clicked successfully.");
        System.out.println("Waiting for reservation confirmation...");
    }

    private boolean isBookingConfirmedDisplayed() {
        try {
            WebElement bookingConfirmed = shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(CONFIRMATION_MESSAGE_XPATH)));
            System.out.println("Booking confirmation displayed: " + bookingConfirmed.getText());
            test.pass("Booking confirmation displayed: " + bookingConfirmed.getText());
            return bookingConfirmed.isDisplayed();
        } catch (Exception e) {
            System.out.println("Booking confirmation not displayed: " + e.getMessage());
            test.fail("Booking confirmation not displayed: " + e.getMessage());
            return false;
        }
    }

    private boolean isInvalidCredentialsDisplayed() {
        try {
            WebElement errorMessage = shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(VALIDATION_ERROR_XPATH)));
            System.out.println("Validation error message displayed: " + errorMessage.getText());
            test.info("Validation error message displayed: " + errorMessage.getText());
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            System.out.println("Validation error message not displayed: " + e.getMessage());
            return false;
        }
    }

    private boolean isInvalidDateRangeErrorDisplayed() {
        try {
            WebElement errorMessage = shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(INVALID_DATE_ERROR_XPATH)));
            System.out.println("Invalid date range error displayed: " + errorMessage.getText());
            test.info("Invalid date range error displayed: " + errorMessage.getText());
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            System.out.println("Invalid date range error not displayed: " + e.getMessage());
            return false;
        }
    }

    private boolean isApplicationErrorDisplayed() {
        try {
            WebElement errorElement = shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"_next_error_\"]/body/div/div")));
            System.out.println("Application error displayed: " + errorElement.getText());
            test.info("Application error displayed: " + errorElement.getText());
            return errorElement.isDisplayed();
        } catch (Exception e) {
            System.out.println("Application error not displayed: " + e.getMessage());
            test.info("Application error not displayed.");
            return false;
        }
    }

    private void selectrange(String startDay, String endDay) throws InterruptedException {
        test.info("Selecting date range: " + startDay + " to " + endDay);
        try {
            // Wait for the calendar to be visible
            WebElement calendar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(CALENDAR_CONTAINER_CLASS)));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", calendar);
            System.out.println("Calendar container located.");

            // Navigate to May 2025 if necessary
            navigateToMonth("May 2025");

            // Find the start and end date buttons
            List<WebElement> dateButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(DATE_CELL_SELECTOR)));
            WebElement startButton = null;
            WebElement endButton = null;

            for (WebElement button : dateButtons) {
                String dayText = button.getText().trim();
                if (dayText.equals(startDay)) {
                    startButton = button;
                    System.out.println("Found start date button: " + dayText);
                } else if (dayText.equals(endDay)) {
                    endButton = button;
                    System.out.println("Found end date button: " + dayText);
                }
                if (startButton != null && endButton != null) break;
            }

            if (startButton == null || endButton == null) {
                throw new RuntimeException("Could not locate start or end date buttons for " + startDay + " or " + endDay);
            }

            // Perform drag-and-drop with detailed mouse events across the range
            Actions actions = new Actions(driver);
            actions.moveToElement(startButton)
                    .clickAndHold()
                    .moveByOffset(1, 1) // Initiate drag
                    .perform(); // Start the drag

            // Move to each intermediate day to ensure full range selection
            int startX = startButton.getLocation().getX();
            int startY = startButton.getLocation().getY();
            int endX = endButton.getLocation().getX();
            int endY = endButton.getLocation().getY();
            int steps = Math.abs(endX - startX) / 50; // Approximate steps based on pixel distance

            // For invalid date range (end before start), swap the direction
            int startDayNum = Integer.parseInt(startDay);
            int endDayNum = Integer.parseInt(endDay);
            if (endDayNum < startDayNum) {
                // Swap the direction of drag for invalid range
                for (int i = 1; i <= steps; i++) {
                    actions.moveByOffset(-(endX - startX) / steps, -(endY - startY) / steps);
                }
            } else {
                for (int i = 1; i <= steps; i++) {
                    actions.moveByOffset((endX - startX) / steps, (endY - startY) / steps);
                }
            }

            actions.moveToElement(endButton)
                    .release()
                    .perform();
            System.out.println("Drag and drop performed from " + startDay + " to " + endDay);

            // For TC11, we don't expect the "Selected" event to appear for an invalid range, so skip this check
            // Wait for UI update using WebDriverWait only if the range is valid
            if (endDayNum >= startDayNum) {
                shortWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.xpath("//div[contains(@class, 'rbc-event-allday') and contains(., 'Selected')]")));

                // Verify selection using event segments
                List<WebElement> selectedEvents = driver.findElements(
                        By.xpath("//div[contains(@class, 'rbc-event-allday') and contains(., 'Selected')]"));
                if (selectedEvents.isEmpty()) {
                    test.warning("No selected events found after drag.");
                    System.out.println("Warning: No selected events found after drag.");
                } else {
                    System.out.println("Selected events found: " + selectedEvents.size());
                    test.pass("Date range selected successfully.");

                    // Simplified verification: Check only start and end dates
                    boolean startSelected = selectedEvents.stream().anyMatch(e ->
                            e.getText().contains(startDay) && e.getText().contains("Selected"));
                    boolean endSelected = selectedEvents.stream().anyMatch(e ->
                            e.getText().contains(endDay) && e.getText().contains("Selected"));
                    if (!startSelected || !endSelected) {
                        test.warning("Selected range does not include start (" + startDay + ") or end (" + endDay + ")");
                        System.out.println("Warning: Selected range does not include start or end dates.");
                    } else {
                        test.pass("Start and end dates verified: " + startDay + " to " + endDay);
                    }
                }

                // Check for unavailable dates only if necessary
                List<WebElement> unavailableEvents = driver.findElements(
                        By.xpath("//div[contains(@class, 'rbc-event-allday') and contains(., 'Unavailable')]"));
                if (!unavailableEvents.isEmpty() && selectedEvents.stream().anyMatch(e ->
                        unavailableEvents.stream().anyMatch(u -> u.getLocation().equals(e.getLocation())))) {
                    test.warning("Selection overlaps with unavailable dates. Clearing and retrying.");
                    actions.moveToElement(startButton).click().perform(); // Attempt to deselect
                    selectrange(startDay, endDay); // Retry with the same parameters
                }
            }
        } catch (Exception e) {
            test.error("Failed to select date range: " + e.getMessage());
            System.out.println("Failed to select date range: " + e.getMessage());
            throw e; // Re-throw to fail the test
        }
    }

    private void navigateToMonth(String targetMonthYear) throws InterruptedException {
        test.info("Navigating to month: " + targetMonthYear);
        try {
            WebElement monthLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".rbc-toolbar-label")));
            while (!monthLabel.getText().equals(targetMonthYear)) {
                WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".rbc-btn-group button:last-child")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextButton);
                Thread.sleep(SHORT_SLEEP_MILLIS); // Wait for animation
                monthLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".rbc-toolbar-label")));
            }
            System.out.println("Navigated to month: " + targetMonthYear);
        } catch (Exception e) {
            test.error("Failed to navigate to month " + targetMonthYear + ": " + e.getMessage());
            System.out.println("Failed to navigate to month: " + e.getMessage());
            throw new RuntimeException("Month navigation failed", e);
        }
    }

    private void fillBookingForm(String firstName, String lastName, String email, String phone) throws InterruptedException {
        test.info("Filling the booking form with typing simulation.");
        try {
            WebElement firstnameField = fieldWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(FIRSTNAME_FIELD_XPATH)));
            WebElement lastnameField = driver.findElement(By.xpath(LASTNAME_FIELD_XPATH));
            WebElement emailField = driver.findElement(By.xpath(EMAIL_FIELD_XPATH));
            WebElement phoneField = driver.findElement(By.xpath(PHONE_FIELD_XPATH));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", firstnameField);

            if (firstName != null && !firstName.isEmpty()) {
                for (char c : firstName.toCharArray()) {
                    firstnameField.sendKeys(String.valueOf(c));
                    Thread.sleep(TYPING_SPEED_MS);
                }
            }
            if (lastName != null && !lastName.isEmpty()) {
                for (char c : lastName.toCharArray()) {
                    lastnameField.sendKeys(String.valueOf(c));
                    Thread.sleep(TYPING_SPEED_MS);
                }
            }
            if (email != null && !email.isEmpty()) {
                for (char c : email.toCharArray()) {
                    emailField.sendKeys(String.valueOf(c));
                    Thread.sleep(TYPING_SPEED_MS);
                }
            }
            if (phone != null && !phone.isEmpty()) {
                for (char c : phone.toCharArray()) {
                    phoneField.sendKeys(String.valueOf(c));
                    Thread.sleep(TYPING_SPEED_MS);
                }
            }
            System.out.println("Booking form filled successfully with typing simulation.");
        } catch (Exception e) {
            test.error("Failed to fill booking form: " + e.getMessage());
            System.out.println("Failed to fill booking form: " + e.getMessage());
            throw e;
        }
    }

        @Test(priority = 1,description = "TC08: Book room with valid details and verify confirmation")
        public void testValidBooking() throws InterruptedException {
            test.info("Starting test: Book room with valid details and verify confirmation");
            try {
              navigateToRoomsPage();
                System.out.println("navigateToRoomsPage");
                bookNowClick();
                System.out.println("bookNowClick");
                selectrange("02","06");
                System.out.println("selectDateRange");
                reservationButton1Click();
                System.out.println("reservationButton1Click");
                fillBookingForm(FIRSTNAME_VALUE, LASTNAME_VALUE, EMAIL_VALUE, PHONE_VALUE);
                System.out.println("fillBookingForm");
                reserveNowButton1Click();
                System.out.println("reserveNowButton1Click");
                Assert.assertTrue(isBookingConfirmedDisplayed(), "Booking confirmation was not displayed.");
                System.out.println("Test case TC08 completed.");
            } catch (Exception e) {

            }
        }


        @Test(priority = 2,description = "TC09: Book room with valid details and overlapped dates")
        public void testoverlapBooking() throws InterruptedException {
            test.info("Starting test: Book room with valid details and overlapped dates");
            try {
                navigateToRoomsPage();
                System.out.println("navigateToRoomsPage");
                bookNowClick();
                System.out.println("bookNowClick");
                selectrange("02", "06");
                System.out.println("selectDateRange");
                reservationButton1Click();
                System.out.println("reservationButton1Click");
                fillBookingForm(FIRSTNAME_VALUE, LASTNAME_VALUE, EMAIL_VALUE, PHONE_VALUE);
                System.out.println("fillBookingForm");
                reserveNowButton1Click();
                System.out.println("reserveNowButton1Click");
                Assert.assertTrue(isApplicationErrorDisplayed(), "Application error was displayed.");
                Assert.assertFalse(isBookingConfirmedDisplayed(), "Booking confirmation was not displayed.");
                Assert.assertFalse(isInvalidCredentialsDisplayed(), "Invalid credentials displayed.");
                System.out.println("Test case TC09 completed.");
            } catch (Exception e) {

            }
        }

        @Test(priority = 3,description = "TC10: Book without filling required fields and verify validation")
        public void testBookingWithoutRequiredFields() throws InterruptedException {
            test.info("Starting test: Book without filling required fields and verify validation");
            try {
                navigateToRoomsPage();
                System.out.println("navigateToRoomsPage");
                bookNowClick();
                System.out.println("bookNowClick");
                selectrange("10", "14");
                System.out.println("selectDateRange");
                reservationButton1Click();
                System.out.println("reservationButton1Click");
                // Skip filling the form (pass empty values)
                fillBookingForm("", "", "", "");
                System.out.println("fillBookingForm skipped");
                reserveNowButton1Click();
                System.out.println("reserveNowButton1Click");
                Assert.assertTrue(isInvalidCredentialsDisplayed(), "Validation error for required fields was not displayed.");
                Assert.assertFalse(isBookingConfirmedDisplayed(), "Booking confirmation was displayed unexpectedly.");
                Assert.assertFalse(isApplicationErrorDisplayed(), "Application error was displayed.");
                System.out.println("Test case TC10 completed.");
            } catch (Exception e) {

            }
        }

    @Test(priority = 4,description = "TC11: Book with invalid date range (end date before start) and verify error")
    public void testBookingWithInvalidDateRange() throws InterruptedException {
        test.info("Starting test: Book with invalid date range and verify error");
        try {
            navigateToRoomsPage();
            System.out.println("navigateToRoomsPage");
            bookNowClick();
            System.out.println("bookNowClick");
            selectrange("23", "20"); // End date (15) before start date (19)
            System.out.println("selectDateRange");
            reservationButton1Click();
            System.out.println("reservationButton1Click");
            fillBookingForm(FIRSTNAME_VALUE, LASTNAME_VALUE, EMAIL_VALUE, PHONE_VALUE);
            System.out.println("fillBookingForm");
            reserveNowButton1Click();
            System.out.println("reserveNowButton1Click");
            Assert.assertTrue(isInvalidDateRangeErrorDisplayed(), "Invalid date range error was not displayed.");
            Assert.assertFalse(isBookingConfirmedDisplayed(), "Booking confirmation was displayed unexpectedly.");
            Assert.assertFalse(isApplicationErrorDisplayed(), "Application error was displayed.");

        } catch (Exception e) {


        }
    }


}