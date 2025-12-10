package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.helpers.AuthenticationHelper;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.MaterialsPage;
import org.junit.jupiter.api.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@UsePlaywright(HeadlessChromeOptions.class)
public class MaterialCreateTests {

    @ParameterizedTest
    @ValueSource(strings = {"EXTRA SMALL", "SMALL", "MEDIUM", "LARGE", "EXTRA LARGE"})
    @DisplayName("Should add materials with different sizes")
    public void shouldAddMaterialsWithDifferentSizes(String size, Page page) {
        DashboardPage dashboardPage = AuthenticationHelper.loginAsAdmin(page);
        MaterialsPage materialsPage = dashboardPage.navigateToMaterials();
        materialsPage.waitForLoad();

        String uniqueName = "Μπλούζα_" + System.currentTimeMillis();

        materialsPage.addMaterial(
                uniqueName,
                size,
                "10",
                TestConstants.STORE_KENTRIKA
        );

        Assertions.assertThat(materialsPage.isAddMaterialModalVisible()).isFalse();
    }
}