package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import gr.clothesmanager.constants.TestConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for Dashboard page
 * Handles navigation to different sections (materials, orders, stores, users)
 * and displays main menu cards
 *
 * @author Bill Maziotis
 */
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
        waitForUrl("**/dashboard**");
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
        String sectionSelector = "[data-test='" + sectionTestId + "']";
        waitForSelector(sectionSelector);
        click(sectionSelector);
        waitForUrl(expectedUrl);
        waitForNetworkIdle();
        pause(TestConstants.WAIT_FOR_LOAD);
    }

    public void logout() {
        clickByTestId(LOGOUT_BUTTON);
        new LoginPage(page);
    }

    public boolean isLogoutButtonVisible() {
        return isVisible(LOGOUT_BUTTON);
    }

    public List<String> getCardHeadings() {
        page.getByTestId(CARD_USERS).waitFor();

        List<String> headings = new ArrayList<>();
        headings.add(page.getByTestId(CARD_USERS).locator("h3").textContent());
        headings.add(page.getByTestId(CARD_MATERIALS).locator("h3").textContent());
        headings.add(page.getByTestId(CARD_ORDERS).locator("h3").textContent());
        headings.add(page.getByTestId(CARD_STORES).locator("h3").textContent());

        return headings;
    }

    public void goToDashboard() {
        page.getByTestId("back-to-dashboard").click();
        waitForNetworkIdle();
        pause(1000);
    }
}