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
public class OrderEditTests {

    @Test
    @DisplayName("Should edit order successfully")
    public void shouldEditOrderSuccessfully(Page page) {
        DashboardPage dashboardPage = AuthenticationHelper.loginAsAdmin(page);

        MaterialsPage materialsPage = dashboardPage.navigateToMaterials();
        materialsPage.waitForLoad();

        String uniqueMaterial = TestConstants.uniqueMaterialName("EditTest");

        materialsPage.addMaterial(
                uniqueMaterial,
                TestConstants.SIZE_MEDIUM,
                "10",
                TestConstants.STORE_KENTRIKA
        );

        dashboardPage.goToDashboard();

        OrdersPage ordersPage = dashboardPage.navigateToOrders();
        ordersPage.waitForLoad();

        ordersPage.createOrder(
                "1",
                "2025-12-31",
                TestConstants.STORE_KENTRIKA,
                uniqueMaterial,
                TestConstants.SIZE_MEDIUM,
                "admin",
                TestConstants.STATUS_PENDING
        );

        ordersPage.goToLastPage();
        ordersPage.editOrder("1", TestConstants.STATUS_COMPLETED);
        ordersPage.waitForLoad();

        Assertions.assertThat(ordersPage.orderExists(uniqueMaterial));
    }
}