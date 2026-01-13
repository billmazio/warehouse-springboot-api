package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.helpers.AuthenticationHelper;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.MaterialsPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@UsePlaywright(HeadlessChromeOptions.class)
public class MaterialSearchTests {

    @Test
    @DisplayName("Should search materials by product name")
    public void shouldSearchMaterialsByProductName(Page page) {
        DashboardPage dashboardPage = AuthenticationHelper.loginAsAdmin(page);
        MaterialsPage materialsPage = dashboardPage.navigateToMaterials();
        materialsPage.waitForLoad();
        
        materialsPage.searchByProductName("Μπλούζα");

       Assertions.assertThat(materialsPage.currentProduct()).isEqualTo("Μπλούζα");
    }
    
    @Test
    @DisplayName("Should filter materials by size")
    public void shouldFilterMaterialsBySize(Page page) {
        DashboardPage dashboardPage = AuthenticationHelper.loginAsAdmin(page);
        MaterialsPage materialsPage = dashboardPage.navigateToMaterials();
        materialsPage.waitForLoad();
        
        materialsPage.filterBySize(TestConstants.SIZE_SMALL);

        Assertions.assertThat(materialsPage.currentFilter()).isEqualTo("SMALL");
    }
}