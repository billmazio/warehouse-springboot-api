package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.helpers.AuthenticationHelper;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.StoresPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@UsePlaywright(HeadlessChromeOptions.class)
public class StoreDeleteTests {

    @Test
    @DisplayName("Should delete custom store successfully")
    void shouldDeleteCustomStoreSuccessfully(Page page) {
        DashboardPage dashboardPage = AuthenticationHelper.loginAsAdmin(page);
        StoresPage storesPage = dashboardPage.navigateToStores();
        storesPage.waitForLoad();

        String uniqueStore = TestConstants.uniqueStoreName("ΑΝΑΤΟΛΙΚΑ");
        storesPage.createStore(uniqueStore, "ΟΔΟΣ 1", TestConstants.STATUS_ACTIVE);

        storesPage.deleteStore(uniqueStore);

        Assertions.assertThat(storesPage.storesList()).doesNotContain(uniqueStore);
    }
}