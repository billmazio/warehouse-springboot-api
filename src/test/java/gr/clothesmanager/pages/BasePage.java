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
        page.setDefaultTimeout(60000);
        page.setDefaultNavigationTimeout(60000);
    }


    protected void navigate() {
        page.navigate(TestConstants.LOGIN_URL);
        waitForNetworkIdle();
    }

    protected void waitForUrl(String urlPattern) {
        page.waitForURL(urlPattern,
                new Page.WaitForURLOptions().setTimeout(TestConstants.DEFAULT_TIMEOUT));
    }

    protected void waitForNetworkIdle() {
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    protected void pause(int milliseconds) {
        page.waitForTimeout(milliseconds);
    }

    protected void waitForVisible(String testId) {
        page.getByTestId(testId).waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE)
        );
    }

    protected void waitForHidden(String testId) {
        page.getByTestId(testId).waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN)
        );
    }

    protected void waitForSelector(String selector) {
        page.locator(selector).waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(TestConstants.DEFAULT_TIMEOUT)
        );
    }

    protected void clickByTestId(String testId) {
        page.getByTestId(testId).click();
    }

    protected void fillByTestId(String testId, String text) {
        page.getByTestId(testId).fill(text);
    }

    protected void clearByTestId(String testId) {
        page.getByTestId(testId).clear();
    }

    protected void selectOptionByTestId(String testId, String value) {
        page.getByTestId(testId).selectOption(value);
    }

    protected void click(String selector) {
        page.locator(selector).click();
    }

    protected Locator getByTestId(String testId) {
        return page.getByTestId(testId);
    }

    protected boolean isVisible(String testId) {
        return page.getByTestId(testId).isVisible();
    }

    protected int getCountByTestId(String testId) {
        return page.getByTestId(testId).count();
    }

    protected String getTitle() {
        return page.title();
    }

    public void goToLastPage() {
        while (hasNextPage()) {
            page.getByTestId("pagination-next").click(new Locator.ClickOptions().setForce(true));
            pause(500);
            waitForNetworkIdle();
        }
    }

    protected boolean hasNextPage() {
        return !page.getByTestId("pagination-next").isDisabled();
    }
}