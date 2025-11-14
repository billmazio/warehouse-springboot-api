package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.base.BaseTest;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.OrdersPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
@DisplayName("Order Delete Tests")
public class OrderDeleteTests extends BaseTest {
    
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
        assertEquals(initialCount - 1, finalCount,
            "Order count should decrease by 1 after deletion");
    }
}