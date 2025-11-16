package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.StoresPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static gr.clothesmanager.helpers.AuthenticationHelper.loginAsAdmin;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UsePlaywright(HeadlessChromeOptions.class)
public class StoreDeleteTests {
    
    @Test
    @DisplayName("Should delete custom store successfully")
    public void shouldDeleteCustomStoreSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        StoresPage storesPage = dashboardPage.navigateToStores();
        storesPage.waitForLoad();
        
        int enabledDeleteCount = storesPage.getEnabledDeleteButtonCount();
        
        if (enabledDeleteCount == 0) {
            System.out.println("No deletable stores, skipping test");
            return;
        }
        
        int initialCount = storesPage.getStoreCount();
        
        storesPage.deleteFirstEnabledStore();
        
        int finalCount = storesPage.getStoreCount();
        assertEquals(initialCount - 1, finalCount,
            "Store count should decrease by 1 after deletion");
    }
}