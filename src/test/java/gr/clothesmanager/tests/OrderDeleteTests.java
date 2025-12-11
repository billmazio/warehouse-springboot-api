package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.helpers.AuthenticationHelper;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.MaterialsPage;
import gr.clothesmanager.pages.OrdersPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@UsePlaywright(HeadlessChromeOptions.class)
public class OrderDeleteTests {
    
    @Test
    @DisplayName("Should delete order successfully")
    public void shouldDeleteOrderSuccessfully(Page page) {
        DashboardPage dashboardPage = AuthenticationHelper.loginAsAdmin(page);
        MaterialsPage materialsPage = dashboardPage.navigateToMaterials();
        materialsPage.waitForLoad();

        String uniqueName = "Μπλούζα_" + System.currentTimeMillis();

        materialsPage.addMaterial(uniqueName, TestConstants.SIZE_MEDIUM, "10", TestConstants.STORE_KENTRIKA);

        dashboardPage.goToDashboard();

        OrdersPage ordersPage = dashboardPage.navigateToOrders();
        ordersPage.waitForLoad();

        ordersPage.createOrder(
                "1",
                "2025-12-31",
                TestConstants.STORE_KENTRIKA,
                uniqueName,
                TestConstants.SIZE_MEDIUM,
                "admin",
                TestConstants.STATUS_PENDING
        );

        ordersPage.waitForLoad();
        ordersPage.goToLastPage();
        
        ordersPage.deleteOrder(uniqueName);

        Assertions.assertThat(ordersPage.ordersList()).doesNotContain(uniqueName);
    }
}