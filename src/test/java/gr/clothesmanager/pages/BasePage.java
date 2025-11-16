package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import gr.clothesmanager.constants.TestConstants;

/**
 * Base Page Object class
 * Contains common methods used by all page objects
 * All page classes extend this base class
 *
 * @author Bill Maziotis
 */
public abstract class BasePage {

    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    /**
     * Navigates to a URL
     */
    protected void navigate() {
        page.navigate(TestConstants.LOGIN_URL);
        waitForNetworkIdle();
    }

    /**
     * Waits for URL to match pattern
     * @param urlPattern URL pattern to wait for (supports wildcards)
     */
    protected void waitForUrl(String urlPattern) {
        page.waitForURL(urlPattern,
                new Page.WaitForURLOptions().setTimeout(TestConstants.DEFAULT_TIMEOUT));
    }

    /**
     * Waits for network to be idle (no pending requests)
     * Use after actions that trigger API calls
     */
    protected void waitForNetworkIdle() {
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * Hard wait for specified milliseconds
     * Use sparingly - prefer explicit waits
     * @param milliseconds Time to wait in milliseconds
     */
    protected void pause(int milliseconds) {
        page.waitForTimeout(milliseconds);
    }

    /**
     * Waits for element to be visible by test ID
     * @param testId data-test attribute value
     */
    protected void waitForVisible(String testId) {
        page.getByTestId(testId).waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE)
        );
    }

    /**
     * Waits for element to be hidden by test ID
     * @param testId data-test attribute value
     */
    protected void waitForHidden(String testId) {
        page.getByTestId(testId).waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN)
        );
    }

    /**
     * Waits for element to be visible by CSS selector
     * @param selector CSS selector
     */
    protected void waitForSelector(String selector) {
        page.locator(selector).waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(TestConstants.DEFAULT_TIMEOUT)
        );
    }

    /**
     * Clicks element by test ID
     * @param testId data-test attribute value
     */
    protected void clickByTestId(String testId) {
        page.getByTestId(testId).click();
    }

    /**
     * Fills input field by test ID
     * @param testId data-test attribute value
     * @param text Text to fill
     */
    protected void fillByTestId(String testId, String text) {
        page.getByTestId(testId).fill(text);
    }

    /**
     * Clears input field by test ID
     * @param testId data-test attribute value
     */
    protected void clearByTestId(String testId) {
        page.getByTestId(testId).clear();
    }

    /**
     * Selects option from dropdown by test ID
     * @param testId data-test attribute value
     * @param value Option value to select
     */
    protected void selectOptionByTestId(String testId, String value) {
        page.getByTestId(testId).selectOption(value);
    }

    /**
     * Clicks element by CSS selector
     * @param selector CSS selector
     */
    protected void click(String selector) {
        page.locator(selector).click();
    }

    /**
     * Fills input field by CSS selector
     * @param selector CSS selector
     * @param text Text to fill
     */
    protected void fill(String selector, String text) {
        page.locator(selector).fill(text);
    }

    /**
     * Gets Locator by test ID
     * Useful for complex assertions in tests
     * @param testId data-test attribute value
     * @return Locator for the element
     */
    protected Locator getByTestId(String testId) {
        return page.getByTestId(testId);
    }

    /**
     * Gets Locator by CSS selector
     * @param selector CSS selector
     * @return Locator for the element
     */
    protected Locator getLocator(String selector) {
        return page.locator(selector);
    }

    /**
     * Gets text content of element by test ID
     * @param testId data-test attribute value
     * @return Text content of the element
     */
    protected String getText(String testId) {
        return page.getByTestId(testId).textContent();
    }

    /**
     * Checks if element is visible by test ID
     * @param testId data-test attribute value
     * @return true if element is visible
     */
    protected boolean isVisible(String testId) {
        return page.getByTestId(testId).isVisible();
    }

    /**
     * Checks if element is visible by CSS selector
     * @param selector CSS selector
     * @return true if element is visible
     */
    protected boolean isVisibleBySelector(String selector) {
        return page.locator(selector).isVisible();
    }

    /**
     * Gets count of elements matching selector
     * @param selector CSS selector
     * @return Number of matching elements
     */
    protected int getCount(String selector) {
        return page.locator(selector).count();
    }

    /**
     * Gets current page URL
     * @return Current URL
     */
    protected String getCurrentUrl() {
        return page.url();
    }

    /**
     * Gets page title
     * @return Page title
     */
    protected String getTitle() {
        return page.title();
    }
}