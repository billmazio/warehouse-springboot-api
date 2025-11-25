package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.MaterialsPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static gr.clothesmanager.helpers.AuthenticationHelper.loginAsAdmin;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright(HeadlessChromeOptions.class)
public class MaterialDeleteTests {
    
    @Test
    @DisplayName("Should delete material successfully")
    public void shouldDeleteMaterialSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        MaterialsPage materialsPage = dashboardPage.navigateToMaterials();
        materialsPage.waitForLoad();
        
        int initialCount = materialsPage.getMaterialCount();
        Assertions.assertThat(initialCount).isGreaterThan(0);
        
        materialsPage.deleteFirstMaterial();
        
        int finalCount = materialsPage.getMaterialCount();
        Assertions.assertThat(finalCount).isEqualTo(initialCount - 1);
    }
}