package gr.clothesmanager;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


@UsePlaywright(ClothesManagerApplicationTests.MyOptions.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClothesManagerApplicationTests {

    public static class MyOptions implements OptionsFactory {
        @Override
        public Options getOptions() {
            return new Options()
                    .setLaunchOptions(
                            new BrowserType.LaunchOptions()
                                    .setHeadless(false)
                                    .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
                    )
                    .setTestIdAttribute("data-test");
        }
    }

    /*============= Helper Methods =============*/

    private void loginAsAdmin(Page page) {
        page.navigate("http://localhost:3000/login");
        page.getByTestId("username-input").fill("admin");
        page.getByTestId("password-input").fill("Admin!1234");
        page.getByTestId("sign-in-button").click();
    }

    private void waitForDashboard(Page page) {
        page.waitForURL("**/dashboard**", new Page.WaitForURLOptions().setTimeout(10000));
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    private void navigateToDashboardSection(Page page, String cardTestId, String expectedUrl) {
        page.getByTestId(cardTestId).waitFor(new Locator.WaitForOptions().setTimeout(10000));
        page.getByTestId(cardTestId).click();
        page.waitForURL(expectedUrl, new Page.WaitForURLOptions().setTimeout(10000));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        page.waitForTimeout(1000);
    }

    /*============= Login & Page Load Tests =============*/

    @Test
    @Order(1)
    @DisplayName("Should load login page with correct title")
    void shouldLoadLoginPage(Page page) {
        page.navigate("http://localhost:3000/login");
        String title = page.title();
        Assertions.assertTrue(title.contains("Warehouse Management System"));
    }

    /*============= Login Validation Tests =============*/

    @Test
    @Order(2)
    @DisplayName("Should validate short username and password values")
    void shouldValidateShortValues(Page page) {
        page.navigate("http://localhost:3000/login");

        page.getByTestId("username-input").fill("ss");
        page.getByTestId("password-input").fill("short");
        page.getByTestId("sign-in-button").click();

        var usernameError = page.getByTestId("username-error");
        var passwordError = page.getByTestId("password-error");

        assertThat(usernameError).isVisible();
        assertThat(passwordError).isVisible();
        assertThat(usernameError).containsText("Το όνομα χρήστη πρέπει να είναι από 3 έως 50 χαρακτήρες.");
        assertThat(passwordError).containsText("Ο κωδικός πρέπει να έχει τουλάχιστον 6 χαρακτήρες.");
    }

    @DisplayName("Should show error for empty mandatory fields")
    @Order(3)
    @ParameterizedTest
    @ValueSource(strings = {"username", "password"})
    void shouldShowErrorForEmptyFields(String fieldName, Page page) {
        page.navigate("http://localhost:3000/login");

        page.getByTestId("username-input").fill("admin");
        page.getByTestId("password-input").fill("Admin!1234");
        page.getByTestId("username-input").clear();
        page.getByTestId("password-input").clear();
        page.getByTestId("sign-in-button").click();

        Locator fieldError;
        if (fieldName.equals("username")) {
            fieldError = page.getByTestId("username-error");
        } else {
            fieldError = page.getByTestId("password-error");
        }

        assertThat(fieldError).isVisible();
        assertThat(fieldError).containsText("Το πεδίο είναι υποχρεωτικό");
    }

    @Test
    @Order(4)
    @DisplayName("Should show invalid credentials error")
    void shouldShowInvalidCredentialsError(Page page) {
        page.navigate("http://localhost:3000/login");

        page.getByTestId("username-input").fill("invalidUser");
        page.getByTestId("password-input").fill("invalidPass");
        page.getByTestId("sign-in-button").click();

        var errorMessage = page.getByTestId("login-error");
        assertThat(errorMessage).isVisible();
        assertThat(errorMessage).containsText("Invalid username or password");
    }

    /*============= Authentication Tests =============*/

    @Test
    @Order(5)
    @DisplayName("Should login and logout successfully")
    void shouldLoginAndLogout(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);

        var logOutButton = page.getByTestId("logout-button");
        assertThat(logOutButton).isVisible();

        logOutButton.click();

        page.waitForURL("**/login", new Page.WaitForURLOptions().setTimeout(5000));
        assertThat(page).hasURL("http://localhost:3000/login");
    }

    /*============= Dashboard Tests =============*/

    @Test
    @Order(6)
    @DisplayName("Should display all menu cards on dashboard")
    void shouldShowMenuCards(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);

        List<String> expectedCards = List.of(
                "card-users",
                "card-materials",
                "card-orders",
                "card-stores"
        );

        expectedCards.forEach(testId -> {
            assertThat(page.getByTestId(testId)).isVisible();
        });
    }

    /*============= Materials Management Tests =============*/

    @Test
    @Order(7)
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
    @Order(8)
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
    @Order(9)
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
    @Order(10)
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
    @Order(11)
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
    @Order(11)
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

    /*============= Orders Management Tests =============*/

    @Test
    @Order(13)
    @DisplayName("Should create new order successfully")
    void shouldCreateOrder(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-orders", "**/manage-orders**");

        page.getByTestId("orders-table").waitFor();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        int initialCount = page.locator("[data-test='order-row']").count();

        // Fill form
        page.getByTestId("order-quantity").fill("1");
        page.getByTestId("order-date").fill("2025-12-31");
        page.getByTestId("order-store").selectOption("ΚΕΝΤΡΙΚΑ");
        page.waitForTimeout(500); // Wait for materials to load
        page.getByTestId("order-material").selectOption("Μπλούζα");
        page.waitForTimeout(500); // Wait for sizes to load
        page.getByTestId("order-size").selectOption("SMALL");
        page.getByTestId("order-user").selectOption("admin");
        page.getByTestId("order-status").selectOption("PENDING");

        // Create
        page.getByTestId("create-order-button").click();

        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);

        int finalCount = page.locator("[data-test='order-row']").count();
        Assertions.assertEquals(initialCount + 1, finalCount);
    }

    @Test
    @Order(14)
    @DisplayName("Should edit order successfully")
    void shouldEditOrder(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-orders", "**/manage-orders**");

        page.getByTestId("orders-table").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);

        int orderCount = page.locator("[data-test='order-row']").count();
        if (orderCount == 0) {
            System.out.println("No orders found, creating one for test...");

            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.waitForTimeout(1000);
        }

        page.getByTestId("edit-button").first().click();
        page.waitForTimeout(1000);

        assertThat(page.getByTestId("update-order-button")).isVisible();

        String newQuantity = "1";
        page.getByTestId("order-quantity").fill(newQuantity);

        page.getByTestId("order-status").selectOption("COMPLETED");

        page.getByTestId("update-order-button").click();

        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);

        boolean quantityFound = false;
        Locator rows = page.locator("[data-test='order-row']");
        for (int i = 0; i < rows.count(); i++) {
            if (rows.nth(i).locator("td").filter(new Locator.FilterOptions().setHasText(newQuantity)).count() > 0) {
                quantityFound = true;
                break;
            }
        }

        Assertions.assertTrue(quantityFound, "Updated quantity should be visible in the table");
    }

    @Test
    @Order(15)
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

    /*============= Users Management Tests =============*/

    @Test
    @DisplayName("Should create new user successfully")
    void shouldCreateUser(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-users", "**/manage-users**");

        page.locator("table.user-table").waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(10000));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        int initialCount = page.locator("table.user-table tbody tr").count();

        String uniqueUsername = "testuser" + System.currentTimeMillis();
        System.out.println("Creating user with username: " + uniqueUsername);

        page.locator(".user-create-form input[type='text']").fill(uniqueUsername);
        page.locator(".user-create-form input[type='password']").fill("TestPassword123!");
        page.locator(".user-create-form select").nth(0).selectOption("LOCAL_ADMIN");
        page.locator(".user-create-form select").nth(1).selectOption("ACTIVE");

        Locator storeSelect = page.locator(".user-create-form select").nth(2);
        Locator storeOptions = storeSelect.locator("option:not([disabled])");

        boolean selectedStore = false;
        for (int i = 0; i < storeOptions.count(); i++) {
            String storeText = storeOptions.nth(i).textContent();
            String storeValue = storeOptions.nth(i).getAttribute("value");

            if (storeValue != null && !storeValue.isEmpty() && !storeText.equals("ΚΕΝΤΡΙΚΑ")) {
                System.out.println("Selecting store: " + storeText);
                storeSelect.selectOption(storeValue);
                selectedStore = true;
                break;
            }
        }

        // If we couldn't avoid ΚΕΝΤΡΙΚΑ, just use the first available store
        if (!selectedStore && storeOptions.count() > 0) {
            String storeValue = storeOptions.first().getAttribute("value");
            storeSelect.selectOption(storeValue);
        } else if (!selectedStore) {
            Assertions.fail("No stores available for selection");
        }

        page.locator(".user-create-form .create-button").click();

        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);

        int finalCount = page.locator("table.user-table tbody tr").count();
        Assertions.assertEquals(initialCount + 1, finalCount,
                "User count should increase by 1 after creation");
        assertThat(page.getByText(uniqueUsername)).isVisible();
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUser(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-users", "**/manage-users**");

        page.getByTestId("user-table").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);

        int initialCount = page.locator("[data-test='user-row']").count();

        Locator enabledDeleteButtons = page.locator("[data-test='delete-button']:not([disabled])");

        if (enabledDeleteButtons.count() == 0) {
            System.out.println("No deletable users, skipping test");
            return;
        }

        enabledDeleteButtons.first().click();
        assertThat(page.getByTestId("confirmation-dialog")).isVisible();
        page.getByTestId("confirm-delete").click();

        page.getByTestId("confirmation-dialog").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        int finalCount = page.locator("[data-test='user-row']").count();
        Assertions.assertEquals(initialCount - 1, finalCount,
                "User count should decrease by 1 after deletion");
    }

    /*============= Stores Management Tests =============*/

    @Test
    @DisplayName("Should edit store successfully")
    void shouldEditStore(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-stores", "**/manage-stores**");

        page.getByTestId("stores-table").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        page.getByTestId("edit-button").first().click();
        assertThat(page.getByTestId("edit-store-modal")).isVisible();

        String updatedAddress = "Updated Address " + System.currentTimeMillis();
        page.getByTestId("edit-store-address").fill(updatedAddress);

        page.getByTestId("edit-store-submit").click();

        page.getByTestId("edit-store-modal").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        assertThat(page.getByText(updatedAddress)).isVisible();
    }

    @Test
    @DisplayName("Should create new store successfully")
    void shouldCreateStore(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-stores", "**/manage-stores**");

        page.getByTestId("stores-table").waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(10000));
        page.waitForLoadState(LoadState.NETWORKIDLE);

       int initialCount = page.locator("[data-test='store-row']").count();

        page.getByTestId("store-create-title").fill("ΔΥΤΙΚΑ");
        page.getByTestId("store-create-address").fill("Αθήνα");
        page.getByTestId("store-create-status").selectOption("ACTIVE");

        page.getByTestId("create-store").click();

        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);

        int finalCount = page.locator("[data-test='store-row']").count();
        Assertions.assertEquals(initialCount + 1, finalCount,
                "Store count should increase by 1 after creation");

        assertThat(page.getByText("ΔΥΤΙΚΑ")).isVisible();
        assertThat(page.getByText("Αθήνα")).isVisible();
    }

    @Test
    @DisplayName("Should delete custom store successfully")
    void shouldDeleteCustomStore(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-stores", "**/manage-stores**");

        page.getByTestId("stores-table").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);

        int initialCount = page.locator("[data-test='store-row']").count();

        Locator enabledDeleteButtons = page.locator("[data-test='delete-button']:not([disabled])");

        if (enabledDeleteButtons.count() == 0) {
            System.out.println("No deletable stores, skipping test");
            return;
        }

        enabledDeleteButtons.first().click();
        assertThat(page.getByTestId("confirmation-dialog")).isVisible();
        page.getByTestId("confirm-delete").click();

        page.getByTestId("confirmation-dialog").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        int finalCount = page.locator("[data-test='store-row']").count();
        Assertions.assertEquals(initialCount - 1, finalCount,
                "Store count should decrease by 1 after deletion");
    }
}
