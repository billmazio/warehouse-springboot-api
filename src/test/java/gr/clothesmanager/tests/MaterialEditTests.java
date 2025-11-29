package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.MaterialsPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static gr.clothesmanager.helpers.AuthenticationHelper.loginAsAdmin;


@UsePlaywright(HeadlessChromeOptions.class)
public class MaterialEditTests {
    
    @Test
    @DisplayName("Should edit material successfully")
    public void shouldEditMaterialSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        MaterialsPage materialsPage = dashboardPage.navigateToMaterials();
        materialsPage.waitForLoad();
        
        materialsPage.editFirstMaterial(
            "Edited Material Name",
            TestConstants.SIZE_SMALL,
            "50"
        );

        Assertions.assertThat(materialsPage.isEditModalVisible()).isFalse();
    }
}