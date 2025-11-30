package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.StoresPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static gr.clothesmanager.helpers.AuthenticationHelper.loginAsAdmin;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright(HeadlessChromeOptions.class)
public class StoreCreateTests {

    @Test
    @DisplayName("Should create new store successfully")
    public void shouldCreateStoreSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        StoresPage storesPage = dashboardPage.navigateToStores();
        storesPage.waitForLoad();
        // Debug: Check if form is visible
        System.out.println("Store create form visible: " + page.locator(".store-create-form").isVisible());
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("stores-debug.png")));
        String uniqueStore = TestConstants.uniqueStoreName("ΔΥΤΙΚΑ");

        storesPage.createStore(
                uniqueStore,
                "Αθήνα",
                TestConstants.STATUS_ACTIVE
        );

        Assertions.assertThat(storesPage.storeExists(uniqueStore)).isTrue();
    }
}