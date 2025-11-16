package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.MaterialsPage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static gr.clothesmanager.helpers.AuthenticationHelper.loginAsAdmin;
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UsePlaywright(HeadlessChromeOptions.class)
public class MaterialCreateTests {
    
    @Test
    @Order(1)
    @DisplayName("Should add material successfully")
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
    @DisplayName("Should add materials with different sizes")
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