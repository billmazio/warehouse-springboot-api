package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.base.BaseTest;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.OrdersPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@UsePlaywright
@DisplayName("Order Create Tests")
public class OrderCreateTests extends BaseTest {
    
    @Test
    @DisplayName("TC_018: Should create new order successfully")
    public void shouldCreateOrderSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        OrdersPage ordersPage = dashboardPage.navigateToOrders();
        ordersPage.waitForLoad();
        
        int initialCount = ordersPage.getOrderCount();
        
        ordersPage.createOrder(
            "1",
            "2025-12-31",
            TestConstants.STORE_KENTRIKA,
            "Μπλούζα",
            TestConstants.SIZE_SMALL,
            "admin",
            TestConstants.STATUS_PENDING
        );
        
        int finalCount = ordersPage.getOrderCount();
        assertEquals(initialCount + 1, finalCount,
            "Order count should increase by 1 after creation");
    }
}