package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.StoresPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static gr.clothesmanager.helpers.AuthenticationHelper.loginAsAdmin;

@UsePlaywright(HeadlessChromeOptions.class)
public class StoreEditTests {
    
    @Test
    @DisplayName("Should edit store successfully")
    public void shouldEditStoreSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        StoresPage storesPage = dashboardPage.navigateToStores();
        storesPage.waitForLoad();
        
        String updatedAddress = "Updated Address " + System.currentTimeMillis();
        
        storesPage.editFirstStoreAddress(updatedAddress);
        
        assertThat(storesPage.getTextLocator(updatedAddress)).isVisible();
    }
}