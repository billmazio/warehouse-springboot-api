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
public class MaterialDeleteTests {
    
    @Test
    @DisplayName("Should delete material successfully")
    public void shouldDeleteMaterialSuccessfully(Page page) {
        DashboardPage dashboardPage = AuthenticationHelper.loginAsAdmin(page);
        MaterialsPage materialsPage = dashboardPage.navigateToMaterials();
        materialsPage.waitForLoad();

        String uniqueName = "Μπλούζα_" + System.currentTimeMillis();

        materialsPage.addMaterial(uniqueName, TestConstants.SIZE_MEDIUM, "10", TestConstants.STORE_KENTRIKA);

        materialsPage.goToLastPage();
        
        materialsPage.deleteMaterial(uniqueName);
        
        Assertions.assertThat(materialsPage.materialsList()).doesNotContain(uniqueName);
    }
}