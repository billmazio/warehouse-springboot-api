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

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoreAndUserTests extends BasePlaywrightTest {

    @Test
    @Order(1)
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
    }

    @Test
    @Order(2)
    @DisplayName("Should create new user successfully")
    void shouldCreateUser(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-users", "**/manage-users**");

        page.getByTestId("user-table").waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(10000));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        int initialCount = page.locator("[data-test='user-row']").count();

        String uniqueUsername = "testuser" + System.currentTimeMillis();
        System.out.println("Creating user with username: " + uniqueUsername);

        page.getByTestId("user-create-username").fill(uniqueUsername);
        page.getByTestId("user-create-password").fill("TestPassword123!");
        page.getByTestId("user-create-role").selectOption("LOCAL_ADMIN");
        page.getByTestId("user-create-status").selectOption("ACTIVE");
        page.getByTestId("user-create-store").selectOption("ΔΥΤΙΚΑ");

        page.locator(".user-create-form .create-button").click();

        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);

        int finalCount = page.locator("[data-test='user-row']").count();
        Assertions.assertEquals(initialCount + 1, finalCount,
                "User count should increase by 1 after creation");
        assertThat(page.getByText(uniqueUsername)).isVisible();
    }

    @Test
    @Order(3)
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
    @Order(4)
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

    @Test
    @Order(5)
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