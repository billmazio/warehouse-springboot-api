package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import gr.clothesmanager.constants.TestConstants;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for Dashboard page
 * Handles navigation to different sections (materials, orders, stores, users)
 * and displays main menu cards
 *
 * @author Bill Maziotis
 */
public class DashboardPage extends BasePage {

    private static final String LOGOUT_BUTTON = "logout-button";
    private static final String CARD_MATERIALS = "card-materials";
    private static final String CARD_ORDERS = "card-orders";
    private static final String CARD_STORES = "card-stores";
    private static final String CARD_USERS = "card-users";
    private static final String MENU_CARDS = "menu-cards";
    private static final String BACK_TO_DASHBOARD = "back-to-dashboard";

    private final Locator logoutButton;
    private final Locator cardMaterials;
    private final Locator cardOrders;
    private final Locator cardStores;
    private final Locator cardUsers;
    private final Locator menuCards;
    private final Locator backToDashboard;

    public DashboardPage(Page page) {
        super(page);
        this.logoutButton = page.getByTestId(LOGOUT_BUTTON);
        this.cardMaterials = page.getByTestId(CARD_MATERIALS);
        this.cardOrders = page.getByTestId(CARD_ORDERS);
        this.cardStores = page.getByTestId(CARD_STORES);
        this.cardUsers = page.getByTestId(CARD_USERS);
        this.menuCards = page.getByTestId(MENU_CARDS);
        this.backToDashboard = page.getByTestId(BACK_TO_DASHBOARD);
    }

    public DashboardPage waitForLoad() {
        waitForUrl("**/dashboard**");
        waitForNetworkIdle();
        menuCards.waitFor();
        return this;
    }

    public MaterialsPage navigateToMaterials() {
        navigateToSection(cardMaterials, "**/manage-materials**");
        return new MaterialsPage(page);
    }

    public OrdersPage navigateToOrders() {
        navigateToSection(cardOrders, "**/manage-orders**");
        return new OrdersPage(page);
    }

    public StoresPage navigateToStores() {
        navigateToSection(cardStores, "**/manage-stores**");
        return new StoresPage(page);
    }

    public UsersPage navigateToUsers() {
        navigateToSection(cardUsers, "**/manage-users**");
        return new UsersPage(page);
    }

    private void navigateToSection(Locator card, String expectedUrl) {
        card.waitFor();
        card.click();
        waitForUrl(expectedUrl);
        waitForNetworkIdle();
        pause(TestConstants.WAIT_FOR_LOAD);
    }

    public void logout() {
        logoutButton.click();
        new LoginPage(page);
    }

    public boolean isLogoutButtonVisible() {
        return logoutButton.isVisible();
    }

    public List<String> getCardHeadings() {
        List<Locator> cardLocators = Arrays.asList(cardUsers, cardMaterials, cardOrders, cardStores);

        return cardLocators.stream()
                .map(card -> card.locator("h3").textContent())
                .collect(Collectors.toList());
    }

    public void goToDashboard() {
        backToDashboard.click();
        waitForNetworkIdle();
        pause(1000);
    }
}