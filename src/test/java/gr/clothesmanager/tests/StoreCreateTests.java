package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.base.BaseTest;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.StoresPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Store Create Tests")
public class StoreCreateTests extends BaseTest {
    
    @Test
    @DisplayName("Should create new store successfully")
    public void shouldCreateStoreSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        StoresPage storesPage = dashboardPage.navigateToStores();
        storesPage.waitForLoad();
        
        int initialCount = storesPage.getStoreCount();
        
        storesPage.createStore(
            TestConstants.STORE_DYTIKA,
            "Αθήνα",
            TestConstants.STATUS_ACTIVE
        );
        
        int finalCount = storesPage.getStoreCount();
        assertEquals(initialCount + 1, finalCount,
            "Store count should increase by 1 after creation");
    }
}