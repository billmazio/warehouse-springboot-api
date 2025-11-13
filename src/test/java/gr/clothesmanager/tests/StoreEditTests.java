package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.base.BaseTest;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.StoresPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright
@DisplayName("Store Edit Tests")
public class StoreEditTests extends BaseTest {
    
    @Test
    @DisplayName("TC_008: Should edit store successfully")
    public void shouldEditStoreSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        StoresPage storesPage = dashboardPage.navigateToStores();
        storesPage.waitForLoad();
        
        String updatedAddress = "Updated Address " + System.currentTimeMillis();
        
        storesPage.editFirstStoreAddress(updatedAddress);
        
        assertThat(storesPage.getTextLocator(updatedAddress)).isVisible();
    }
}