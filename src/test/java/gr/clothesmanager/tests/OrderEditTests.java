package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.OrdersPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static gr.clothesmanager.helpers.AuthenticationHelper.loginAsAdmin;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright(HeadlessChromeOptions.class)
public class OrderEditTests {

    @Test
    @DisplayName("Should edit order successfully")
    public void shouldEditOrderSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        OrdersPage ordersPage = dashboardPage.navigateToOrders().waitForLoad();

        ordersPage.editFirstOrder("1", TestConstants.STATUS_COMPLETED);

        assertTrue(ordersPage.isUpdateOrderButtonVisible(),
                "Update order button should be visible");
    }
}