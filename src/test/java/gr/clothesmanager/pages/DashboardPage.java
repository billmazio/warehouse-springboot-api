package gr.clothesmanager.pages;

import com.microsoft.playwright.Page;
import gr.clothesmanager.constants.TestConstants;

import java.util.List;

public class DashboardPage extends BasePage {

    private static final String LOGOUT_BUTTON = "logout-button";
    private static final String CARD_NAME = "card-name";

    private static final String CARD_MATERIALS = "card-materials";
    private static final String CARD_ORDERS = "card-orders";
    private static final String CARD_STORES = "card-stores";
    private static final String CARD_USERS = "card-users";
    
    public DashboardPage(Page page) {
        super(page);
    }

    public DashboardPage waitForLoad() {
        waitForUrl("**/dashboard**", TestConstants.DEFAULT_TIMEOUT);
        waitForNetworkIdle();
        return this;
    }

    public MaterialsPage navigateToMaterials() {
        navigateToSection(CARD_MATERIALS, "**/manage-materials**");
        return new MaterialsPage(page);
    }
    
    public OrdersPage navigateToOrders() {
        navigateToSection(CARD_ORDERS, "**/manage-orders**");
        return new OrdersPage(page);
    }
    
    public StoresPage navigateToStores() {
        navigateToSection(CARD_STORES, "**/manage-stores**");
        return new StoresPage(page);
    }
    
    public UsersPage navigateToUsers() {
        navigateToSection(CARD_USERS, "**/manage-users**");
        return new UsersPage(page);
    }
    
    private void navigateToSection(String sectionTestId, String expectedUrl) {
        String sectionSelector = "[data-test-section='" + sectionTestId + "']";
        waitForSelector(sectionSelector, TestConstants.DEFAULT_TIMEOUT);
        click(sectionSelector);
        waitForUrl(expectedUrl, TestConstants.DEFAULT_TIMEOUT);
        waitForNetworkIdle();
        pause(TestConstants.WAIT_FOR_LOAD);
    }

    public LoginPage logout() {
        clickByTestId(LOGOUT_BUTTON);
        return new LoginPage(page);
    }

    public boolean isLogoutButtonVisible() {
        return isVisible(LOGOUT_BUTTON);
    }

    public List<String> getCardHeadings() {
        page.locator("[data-test='" + CARD_NAME + "']").first().waitFor();
        return page.locator("[data-test='" + CARD_NAME + "']").locator("h3").allInnerTexts();
    }
    
    public boolean isOnDashboard() {
        return getCurrentUrl().contains("/dashboard");
    }
}