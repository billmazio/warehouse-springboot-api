package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

public abstract class BasePage {
    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    protected void navigate(String url) {
        page.navigate(url);
    }

    protected void waitForUrl(String url, int timeout) {
        page.waitForURL(url, new Page.WaitForURLOptions().setTimeout(timeout));
    }

    protected void waitForNetworkIdle() {
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    protected void pause(int milliseconds) {
        page.waitForTimeout(milliseconds);
    }

    protected void clickByTestId(String testId) {
        page.locator("[data-test='" + testId + "']").click();
    }

    protected void fillByTestId(String testId, String text) {
        page.locator("[data-test='" + testId + "']").fill(text);
    }

    protected void clearByTestId(String testId) {page.locator("[data-test='" + testId + "']").clear();}

    protected void selectOptionByTestId(String testId, String value) {page.locator("[data-test='" + testId + "']").selectOption(value);}

    protected void click(String selector) {
        page.locator(selector).click();
    }

    protected void fill(String selector, String text) {
        page.locator(selector).fill(text);
    }

    protected void waitForVisible(String testId) {
        page.locator("[data-test='" + testId + "']").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE)
        );
    }

    protected void waitForHidden(String testId) {
        page.locator("[data-test='" + testId + "']").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN)
        );
    }

    protected void waitForSelector(String selector, int timeout) {
        page.locator(selector).waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(timeout)
        );
    }

    protected Locator getByTestId(String testId) {return page.locator("[data-test='" + testId + "']");}

    protected Locator getLocator(String selector) {
        return page.locator(selector);
    }

    protected String getText(String testId) {return page.locator("[data-test='" + testId + "']").textContent(); }

    protected int getCount(String selector) {return page.locator(selector).count();}

    protected boolean isVisible(String testId) {return page.locator("[data-test='" + testId + "']").isVisible(); }

    protected boolean isVisibleBySelector(String selector) {return page.locator(selector).isVisible();}

    protected String getCurrentUrl() {return page.url();}

    protected String getTitle() {return page.title();}
}