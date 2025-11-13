package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.base.BaseTest;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.MaterialsPage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;

@UsePlaywright
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Material Create Tests")
public class MaterialCreateTests extends BaseTest {
    
    @Test
    @Order(1)
    @DisplayName("TC_012: Should add material successfully")
    public void shouldAddMaterialSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        MaterialsPage materialsPage = dashboardPage.navigateToMaterials();
        materialsPage.waitForLoad();
        
        materialsPage.addMaterial(
            "Μπλούζα Polo",
            TestConstants.SIZE_MEDIUM,
            "10",
            TestConstants.STORE_KENTRIKA
        );
        
        assertFalse(materialsPage.isAddMaterialModalVisible(),
            "Add material modal should be hidden after submission");
    }
    
    @ParameterizedTest
    @Order(2)
    @ValueSource(strings = {"EXTRA SMALL", "SMALL", "MEDIUM", "LARGE", "EXTRA LARGE"})
    @DisplayName("TC_013: Should add materials with different sizes")
    public void shouldAddMaterialsWithDifferentSizes(String size, Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        MaterialsPage materialsPage = dashboardPage.navigateToMaterials();
        materialsPage.waitForLoad();
        
        materialsPage.addMaterial(
            "Μπλούζα",
            size,
            "10",
            TestConstants.STORE_KENTRIKA
        );
        
        assertFalse(materialsPage.isAddMaterialModalVisible(),
            "Modal should close after adding material with size: " + size);
    }
}