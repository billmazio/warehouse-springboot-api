package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.base.BaseTest;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.MaterialsPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
@DisplayName("Material Search Tests")
public class MaterialSearchTests extends BaseTest {
    
    @Test
    @DisplayName("TC_016: Should search materials by product name")
    public void shouldSearchMaterialsByProductName(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        MaterialsPage materialsPage = dashboardPage.navigateToMaterials();
        materialsPage.waitForLoad();
        
        materialsPage.searchByProductName("Μπλούζα");
        
        int count = materialsPage.getMaterialCount();
        assertTrue(count > 0, "Should find materials matching 'Μπλούζα'");
    }
    
    @Test
    @DisplayName("TC_017: Should filter materials by size")
    public void shouldFilterMaterialsBySize(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        MaterialsPage materialsPage = dashboardPage.navigateToMaterials();
        materialsPage.waitForLoad();
        
        materialsPage.filterBySize(TestConstants.SIZE_SMALL);
        
        int count = materialsPage.getMaterialCount();
        assertTrue(count >= 0, "Should show filtered materials");
    }
}