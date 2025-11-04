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

    // ============= Helper Methods =============

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
    }

    // ============= Login & Page Load Tests =============

    @Test
    @DisplayName("Should load login page with correct title")
    void contextLoads(Page page) {
        page.navigate("http://localhost:3000/login");
        String title = page.title();
        Assertions.assertTrue(title.contains("Warehouse Management System"));
    }

    // ============= Login Validation Tests =============

    @DisplayName("Should show error for empty mandatory fields")
    @ParameterizedTest
    @ValueSource(strings = {"username", "password"})
    void mandatoryFields(String fieldName, Page page) {
        page.navigate("http://localhost:3000/login");

        page.getByTestId("username-input").fill("admin");
        page.getByTestId("password-input").fill("Admin!1234");
        page.getByTestId("username-input").clear();
        page.getByTestId("password-input").clear();
        page.getByTestId("sign-in-button").click();  // Fixed

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

    @Test
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

    // ============= Authentication Tests =============

    @Test
    @DisplayName("Should show logout button after successful login")
    void shouldShowLogout(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);

        var logOutButton = page.getByTestId("logout-button");
        assertThat(logOutButton).isVisible();

        logOutButton.click();

        // Verify redirect to login page after logout
        page.waitForURL("**/login", new Page.WaitForURLOptions().setTimeout(5000));
        assertThat(page).hasURL("http://localhost:3000/login");
    }

    // ============= Dashboard Tests =============

    @Test
    @DisplayName("Should display all 4 menu cards on dashboard")
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

    @ParameterizedTest
    @ValueSource(strings = {"EXTRA SMALL", "SMALL", "MEDIUM", "LARGE", "EXTRALARGE"})
    @DisplayName("Should edit material with different sizes")
    void shouldEditMaterialWithDifferentSizes(String size, Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-materials", "**/manage-materials**");

        page.getByTestId("edit-button").first().click();
        assertThat(page.getByTestId("edit-modal")).isVisible();

        page.getByTestId("edit-text").fill("Material with " + size);
        page.getByTestId("edit-size").selectOption(size);
        page.getByTestId("edit-quantity").fill("10");

        page.getByTestId("edit-confirm").click();

        page.getByTestId("edit-modal").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        assertThat(page.getByTestId("edit-modal")).not().isVisible();
    }

    @ParameterizedTest
    @ValueSource(strings = {"EXTRA SMALL", "SMALL", "MEDIUM", "LARGE", "EXTRA LARGE"})
    @DisplayName("Should add material or cancel if duplicate exists")
    void shouldAddOrCancelMaterial(String size, Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);
        navigateToDashboardSection(page, "card-materials", "**/manage-materials**");

        page.getByTestId("add-material-button").first().click();

        page.getByTestId("add-material-text").fill("Μπλούζα Polo");
        page.getByTestId("add-material-size").selectOption(size);
        page.getByTestId("add-material-quantity").fill("10");
        page.getByTestId("add-material-store").selectOption("ΚΕΝΤΡΙΚΑ");

        boolean materialExists = page.locator("tr", new Page.LocatorOptions().setHasText(size))
                .locator("td")
                .count() > 0;

        if (materialExists) {
            page.getByTestId("add-material-cancel").click();
        } else {
            page.getByTestId("add-material-submit").click();
        }

        page.getByTestId("add-store-modal").waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        assertThat(page.getByTestId("add-store-modal")).not().isVisible();

        page.getByTestId("back-to-dashboard").click();
    }

    //todo fix method about visible fields or not because of the roles ADMIN and simple USER using assertJ!!!
    //todo fix method about dropDown lists that is in the chapter 6 of udemy course the way how to do!!!
}
