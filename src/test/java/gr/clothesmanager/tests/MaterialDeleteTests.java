package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.base.BaseTest;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.MaterialsPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright(HeadlessChromeOptions.class)
public class MaterialDeleteTests extends BaseTest {
    
    @Test
    @DisplayName("Should delete material successfully")
    public void shouldDeleteMaterialSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        MaterialsPage materialsPage = dashboardPage.navigateToMaterials();
        materialsPage.waitForLoad();
        
        int initialCount = materialsPage.getMaterialCount();
        assertTrue(initialCount > 0, "Should have materials to delete");
        
        materialsPage.deleteFirstMaterial();
        
        int finalCount = materialsPage.getMaterialCount();
        assertEquals(initialCount - 1, finalCount,
            "Material count should decrease by 1 after deletion");
    }
}