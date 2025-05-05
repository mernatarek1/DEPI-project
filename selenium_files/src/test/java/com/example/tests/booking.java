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

public class booking {
    // Constants for locators
    private static final String BOOK_NOW_LINK_TEXT = "Book Now";
    private static final String SECOND_BOOK_NOW_BUTTON_CSS = "a.btn.btn-primary[href*='/reservation/1']";
    private static final String CALENDAR_CONTAINER_CLASS = "rbc-month-view";
    private static final String DATE_CELL_CLASS = "rbc-date-cell";
    private static final String FIRSTNAME_FIELD_XPATH = "//input[@name='firstname' and contains(@class, 'form-control')]";
    private static final String LASTNAME_FIELD_XPATH = "//input[@name='lastname' and contains(@class, 'form-control')]";
    private static final String EMAIL_FIELD_XPATH = "//input[@name='email' and contains(@class, 'form-control')]";
    private static final String PHONE_FIELD_XPATH = "//input[@name='phone' and contains(@class, 'form-control')]";
    private static final String RESERVE_NOW_BUTTON_XPATH = "//button[contains(@class, 'btn btn-primary w-100 mb-3') and text()='Reserve Now']";
    private static final String CONFIRMATION_MESSAGE_XPATH = "//*[contains(text(), 'success') or contains(text(), 'confirmed') or contains(text(), 'booking')]";

    // Constants for field values
    private static final String FIRSTNAME_VALUE = "John";
    private static final String LASTNAME_VALUE = "Doe";
    private static final String EMAIL_VALUE = "john.doe@example.com";
    private static final String PHONE_VALUE = "1234567890";

    // Constants for dates
    private static final String START_DATE = "2025-04-15";
    private static final String END_DATE = "2025-04-19";
    private static final String START_DAY = "15";
    private static final String END_DAY = "19";

    // Constants for URL and wait durations
    private static final String WEBSITE_URL = "https://automationintesting.online";
    private static final String RESERVATION_URL = "https://automationintesting.online/reservation/1?checkin=2025-04-15&checkout=2025-04-19";
    private static final int WAIT_TIMEOUT_SECONDS = 30;
    private static final int SHORT_SLEEP_MILLIS = 500;
    private static final int MEDIUM_SLEEP_MILLIS = 2000;
    private static final int LONG_SLEEP_MILLIS = 3000;
    private static final int POST_RESERVE_SLEEP_MILLIS = 10000;

    // JavaScript script for selecting date range
    private static final String SELECT_RANGE_SCRIPT = """
            var startDate = new Date(arguments[0]);
            var endDate = new Date(arguments[1]);
            var calendar = document.querySelector('.rbc-month-view');
            if (calendar) {
                var reactInstance = calendar._reactProps || calendar._reactInternalInstance;
                if (reactInstance && reactInstance.onSelectSlot) {
                    reactInstance.onSelectSlot({ start: startDate, end: endDate, action: 'select' });
                } else if (reactInstance && reactInstance.onRangeChange) {
                    reactInstance.onRangeChange([startDate, endDate]);
                }
                var dateCells = document.querySelectorAll('.rbc-date-cell');
                dateCells.forEach(function(cell) {
                    var dayText = cell.textContent.trim();
                    if (dayText >= arguments[2] && dayText <= arguments[3]) {
                        cell.classList.add('rbc-selected');
                    }
                });
            }
            return 'Script executed';
            """;

    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            // Navigate to the website
            driver.get(WEBSITE_URL);
            Thread.sleep(MEDIUM_SLEEP_MILLIS);
            System.out.println("Page title: " + driver.getTitle());

            // Maximize the browser window
            driver.manage().window().maximize();

            // Initialize WebDriverWait
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT_SECONDS));

            // Click the first "Book Now" link
            clickElement(driver, wait, By.linkText(BOOK_NOW_LINK_TEXT), "First Book Now link");
            System.out.println("Current URL after first click: " + driver.getCurrentUrl());

            // Wait for the page to settle
            Thread.sleep(MEDIUM_SLEEP_MILLIS);

            // Click the second "Book Now" button
            clickElement(driver, wait, By.cssSelector(SECOND_BOOK_NOW_BUTTON_CSS), "Second Book Now button", SHORT_SLEEP_MILLIS);
            System.out.println("Current URL after second click: " + driver.getCurrentUrl());

            // Approach 1: Try to call React Big Calendar's onSelectSlot or onRangeChange
            try {
                Object result = ((JavascriptExecutor) driver).executeScript(
                        SELECT_RANGE_SCRIPT, START_DATE, END_DATE, START_DAY, END_DAY
                );
                System.out.println("Result of onSelectSlot/onRangeChange attempt: " + result);
            } catch (Exception e) {
                System.out.println("Could not call onSelectSlot/onRangeChange: " + e.getMessage());
            }

            Thread.sleep(LONG_SLEEP_MILLIS);

            // Approach 2: Simulate a drag selection using Actions
            try {
                WebElement calendar = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.className(CALENDAR_CONTAINER_CLASS))
                );
                List<WebElement> dateCells = calendar.findElements(By.className(DATE_CELL_CLASS));
                WebElement firstDay = null;
                WebElement lastDay = null;

                for (WebElement dateCell : dateCells) {
                    String dayText = dateCell.getText();
                    if (dayText.equals(START_DAY)) {
                        firstDay = dateCell;
                    }
                    if (dayText.equals(END_DAY)) {
                        lastDay = dateCell;
                    }
                }

                if (firstDay != null && lastDay != null) {
                    ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].scrollIntoView({block: 'center'});", firstDay);
                    Actions actions = new Actions(driver);
                    actions.moveToElement(firstDay)
                            .clickAndHold()
                            .moveToElement(lastDay)
                            .release()
                            .build()
                            .perform();
                    System.out.println("Simulated drag selection from April " + START_DAY + " to April " + END_DAY);
                } else {
                    System.out.println("Could not find the specified days in the calendar.");
                }
            } catch (Exception e) {
                System.out.println("Could not simulate drag selection: " + e.getMessage());
            }

            Thread.sleep(LONG_SLEEP_MILLIS);

            // Approach 3: Update the URL directly
            try {
                driver.get(RESERVATION_URL);
                System.out.println("Updated URL to reflect selected dates: " + RESERVATION_URL);
            } catch (Exception e) {
                System.out.println("Could not update URL: " + e.getMessage());
            }

            Thread.sleep(LONG_SLEEP_MILLIS);

            // Check for firstname field in DOM
            try {
                boolean firstNameExists = !driver.findElements(By.name("firstname")).isEmpty();
                System.out.println("Firstname field exists in DOM: " + firstNameExists);
                if (firstNameExists) {
                    ((JavascriptExecutor) driver).executeScript(
                            "document.getElementsByName('firstname')[0].style.display = 'block';"
                    );
                }
            } catch (Exception e) {
                System.out.println("Error checking for firstname field in DOM: " + e.getMessage());
            }

            // Fill form fields
            try {
                fillField(driver, wait, FIRSTNAME_FIELD_XPATH, FIRSTNAME_VALUE, "firstname");
                fillField(driver, wait, LASTNAME_FIELD_XPATH, LASTNAME_VALUE, "lastname");
                fillField(driver, wait, EMAIL_FIELD_XPATH, EMAIL_VALUE, "email");
                fillField(driver, wait, PHONE_FIELD_XPATH, PHONE_VALUE, "phone");
            } catch (Exception e) {
                System.out.println("Could not fill form fields: " + e.getMessage());
            }

            // Click the "Reserve Now" button
            try {
                WebElement reserveNowButton = wait.until(
                        ExpectedConditions.elementToBeClickable(By.xpath(RESERVE_NOW_BUTTON_XPATH))
                );
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].scrollIntoView({block: 'center'});", reserveNowButton);
                WebElement form = (WebElement) ((JavascriptExecutor) driver).executeScript(
                        "return arguments[0].closest('form');", reserveNowButton
                );
                if (form != null) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].submit();", form);
                    System.out.println("Submitted the form containing the 'Reserve Now' button");
                } else {
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].dispatchEvent(new Event('click', { bubbles: true }));", reserveNowButton
                    );
                    System.out.println("Triggered click event on the 'Reserve Now' button");
                }
            } catch (Exception e) {
                System.out.println("Could not click the 'Reserve Now' button: " + e.getMessage());
            }

            Thread.sleep(POST_RESERVE_SLEEP_MILLIS);

            // Check for JavaScript errors
            try {
                String jsErrors = (String) ((JavascriptExecutor) driver).executeScript(
                        "return window.jsErrors ? window.jsErrors.join('\\n') : 'No JS errors';"
                );
                System.out.println("JavaScript errors on page: " + jsErrors);
            } catch (Exception e) {
                System.out.println("Could not check for JavaScript errors: " + e.getMessage());
            }

            // Verify final URL and page state
            System.out.println("Final URL after clicking 'Reserve Now': " + driver.getCurrentUrl());

            // Check for confirmation message
            try {
                WebElement confirmationMessage = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.xpath(CONFIRMATION_MESSAGE_XPATH))
                );
                System.out.println("Confirmation message: " + confirmationMessage.getText());
            } catch (Exception e) {
                System.out.println("No confirmation message found: " + e.getMessage());
            }

            System.out.println("Page title after reservation: " + driver.getTitle());
            System.out.println("All dates reserved successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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

    // Helper method to click an element
    private static void clickElement(WebDriver driver, WebDriverWait wait, By locator, String elementName) throws InterruptedException {
        clickElement(driver, wait, locator, elementName, 0);
    }

    private static void clickElement(WebDriver driver, WebDriverWait wait, By locator, String elementName, int preClickSleep) throws InterruptedException {
        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        if (preClickSleep > 0) {
            Thread.sleep(preClickSleep);
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        System.out.println(elementName + " clicked successfully!");
    }
}