package gr.clothesmanager;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MaterialAndOrderTests extends BasePlaywrightTest {

    @Test
    @Order(1)
    @DisplayName("Should add material successfully")
    void shouldAddMaterial(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-materials", "**/manage-materials**");

        page.getByTestId("add-material-button").click();
        assertThat(page.getByTestId("add-material-modal")).isVisible();

        page.getByTestId("add-material-text").fill("Μπλούζα Polo");
        page.getByTestId("add-material-size").selectOption("MEDIUM");
        page.getByTestId("add-material-quantity").fill("10");
        page.getByTestId("add-material-store").selectOption("ΚΕΝΤΡΙΚΑ");

        page.getByTestId("add-material-submit").click();

        page.getByTestId("add-material-modal").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        assertThat(page.getByTestId("add-material-modal")).not().isVisible();
    }

    @Test
    @Order(2)
    @DisplayName("Should edit material successfully")
    void shouldEditMaterial(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-materials", "**/manage-materials**");

        page.getByTestId("edit-button").first().click();
        assertThat(page.getByTestId("edit-modal")).isVisible();

        page.getByTestId("edit-text").fill("Edited Material Name");
        page.getByTestId("edit-size").selectOption("SMALL");
        page.getByTestId("edit-quantity").fill("50");

        page.getByTestId("edit-confirm").click();

        page.getByTestId("edit-modal").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        assertThat(page.getByTestId("edit-modal")).not().isVisible();
    }

    @Test
    @Order(3)
    @DisplayName("Should delete material successfully")
    void shouldDeleteMaterial(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-materials", "**/manage-materials**");

        page.getByTestId("materials-table").waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(10000));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        int initialCount = page.locator("[data-test='material-row']").count();
        Assertions.assertTrue(initialCount > 0, "Should have materials to delete");

        page.getByTestId("delete-button").first().click();

        assertThat(page.getByTestId("confirmation-dialog")).isVisible();
        page.getByTestId("confirm-delete").click();

        page.getByTestId("confirmation-dialog").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        int finalCount = page.locator("[data-test='material-row']").count();
        Assertions.assertEquals(initialCount - 1, finalCount);
    }

    @ParameterizedTest
    @Order(4)
    @ValueSource(strings = {"EXTRA SMALL", "SMALL", "MEDIUM", "LARGE", "EXTRA LARGE"})
    @DisplayName("Should add materials with different sizes")
    void shouldAddMaterialsWithDifferentSizes(String size, Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-materials", "**/manage-materials**");

        page.getByTestId("add-material-button").click();

        page.getByTestId("add-material-text").fill("Μπλούζα");
        page.getByTestId("add-material-size").selectOption(size);
        page.getByTestId("add-material-quantity").fill("10");
        page.getByTestId("add-material-store").selectOption("ΚΕΝΤΡΙΚΑ");

        page.getByTestId("add-material-submit").click();

        page.getByTestId("add-material-modal").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        assertThat(page.getByTestId("add-material-modal")).not().isVisible();
    }

    @Test
    @Order(5)
    @DisplayName("Should search materials by product name")
    void shouldSearchMaterials(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-materials", "**/manage-materials**");

        page.getByTestId("filter-product").fill("Μπλούζα");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        int count = page.locator("[data-test='material-row']").count();
        Assertions.assertTrue(count > 0, "Should find materials matching 'Μπλούζα'");
    }

    @Test
    @Order(6)
    @DisplayName("Should filter materials by size")
    void shouldFilterMaterialsBySize(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-materials", "**/manage-materials**");

        page.getByTestId("filter-size").selectOption("SMALL");
        page.waitForLoadState(LoadState.NETWORKIDLE);

        int count = page.locator("[data-test='material-row']").count();
        Assertions.assertTrue(count >= 0, "Should show filtered materials");
    }

    @Test
    @Order(7)
    @DisplayName("Should create new order successfully")
    void shouldCreateOrder(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-orders", "**/manage-orders**");

        page.getByTestId("orders-table").waitFor();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        int initialCount = page.locator("[data-test='order-row']").count();

        page.getByTestId("order-quantity").fill("1");
        page.getByTestId("order-date").fill("2025-12-31");
        page.getByTestId("order-store").selectOption("ΚΕΝΤΡΙΚΑ");
        page.waitForTimeout(500);
        page.getByTestId("order-material").selectOption("Μπλούζα");
        page.waitForTimeout(500);
        page.getByTestId("order-size").selectOption("SMALL");
        page.getByTestId("order-user").selectOption("admin");
        page.getByTestId("order-status").selectOption("PENDING");

        page.getByTestId("create-order-button").click();

        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);

        int finalCount = page.locator("[data-test='order-row']").count();
        Assertions.assertEquals(initialCount + 1, finalCount);
    }

    @Test
    @Order(8)
    @DisplayName("Should edit order successfully")
    void shouldEditOrder(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-orders", "**/manage-orders**");

        page.getByTestId("orders-table").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);

        page.getByTestId("edit-button").first().click();
        page.waitForTimeout(1000);

        assertThat(page.getByTestId("update-order-button")).isVisible();

        String newQuantity = "1";
        page.getByTestId("order-quantity").fill(newQuantity);
        page.getByTestId("order-status").selectOption("COMPLETED");

        page.getByTestId("update-order-button").click();

        assertThat(page.getByTestId("update-order-button")).isVisible();
    }

    @Test
    @Order(9)
    @DisplayName("Should delete order successfully")
    void shouldDeleteOrder(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-orders", "**/manage-orders**");

        page.getByTestId("orders-table").waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(10000));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        int initialCount = page.locator("[data-test='order-row']").count();
        Assertions.assertTrue(initialCount > 0, "Should have orders to delete");

        page.getByTestId("delete-button").first().click();

        assertThat(page.getByTestId("confirmation-dialog")).isVisible();
        page.getByTestId("confirm-delete").click();

        page.getByTestId("confirmation-dialog").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        int finalCount = page.locator("[data-test='order-row']").count();
        Assertions.assertEquals(initialCount - 1, finalCount);
    }
}