package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import gr.clothesmanager.base.BaseTest;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.MaterialsPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("Material Edit Tests")
public class MaterialEditTests extends BaseTest {
    
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
        
        assertFalse(materialsPage.isEditModalVisible(),
            "Edit modal should be hidden after submission");
    }
}