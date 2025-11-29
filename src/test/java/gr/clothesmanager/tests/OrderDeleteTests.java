package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.OrdersPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static gr.clothesmanager.helpers.AuthenticationHelper.loginAsAdmin;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright(HeadlessChromeOptions.class)
public class OrderDeleteTests {
    
    @Test
    @DisplayName("Should delete order successfully")
    public void shouldDeleteOrderSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        OrdersPage ordersPage = dashboardPage.navigateToOrders();
        ordersPage.waitForLoad();
        
        int initialCount = ordersPage.getOrderCount();
        assertTrue(initialCount > 0, "Should have orders to delete");
        
        ordersPage.deleteFirstOrder();
        
        int finalCount = ordersPage.getOrderCount();
        Assertions.assertThat(finalCount).isEqualTo(initialCount - 1);
    }
}