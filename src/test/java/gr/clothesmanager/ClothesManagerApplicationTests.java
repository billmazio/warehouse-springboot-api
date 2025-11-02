package gr.clothesmanager;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
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
            return new Options().setLaunchOptions(
                            new BrowserType.LaunchOptions()
                                    .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
                    ).setHeadless(false)
                    .setTestIdAttribute("data-test");
        }
    }

    @Test
    void contextLoads(Page page) {
        page.navigate("http://localhost:3000/login");
        String title = page.title();

        Assertions.assertTrue(title.contains("Warehouse Management System"));
    }

    @DisplayName("Mandatory fields")
    @ParameterizedTest
    @ValueSource(strings = {"username", "password"})
    void mandatoryFields(String fieldName, Page page) {
        page.navigate("http://localhost:3000/login");

        var usernameField = page.getByPlaceholder("Username");
        var passwordField = page.getByPlaceholder("Password");
        var signInButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In"));

        usernameField.fill("admin");
        passwordField.fill("Admin!1234");

        usernameField.clear();
        passwordField.clear();

        signInButton.click();

        Locator fieldError;

        if (fieldName.equals("username")) {
            fieldError = usernameField.locator("..").locator(".error-message");
        } else {
            fieldError = passwordField.locator("..").locator(".error-message");
        }

        assertThat(fieldError).isVisible();
        assertThat(fieldError).containsText("Το πεδίο είναι υποχρεωτικό");
    }

    @Test
    void shouldValidateShortValues(Page page) {
        page.navigate("http://localhost:3000/login");

        var usernameField = page.getByPlaceholder("Username");
        var passwordField = page.getByPlaceholder("Password");
        var signInButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In"));

        usernameField.fill("ss");
        passwordField.fill("short");
        signInButton.click();

        assertThat(page.locator(".error-message")).hasCount(2);
    }

    @Test
    void shouldShowInvalidCredentialsError(Page page) {
        page.navigate("http://localhost:3000/login");

        var usernameField = page.getByPlaceholder("Username");
        var passwordField = page.getByPlaceholder("Password");
        var signInButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In"));

        usernameField.fill("invalidUser");
        passwordField.fill("invalidPass");
        signInButton.click();

        var errorMessage = page.getByRole(AriaRole.ALERT);
        assertThat(errorMessage).containsText("Invalid username or password");
    }

    @Test
    void shouldShowLogout(Page page) {
        page.navigate("http://localhost:3000/login");

        var usernameField = page.getByPlaceholder("Username");
        var passwordField = page.getByPlaceholder("Password");
        var signInButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In"));

        usernameField.fill("admin");
        passwordField.fill("Admin!1234");
        signInButton.click();

        var logOutButton = page.locator(".logout-btn");
        logOutButton.click();
    }

    @Test
    void shouldShowMenuCards(Page page) {
        page.navigate("http://localhost:3000/login");

        var usernameField = page.getByPlaceholder("Username");
        var passwordField = page.getByPlaceholder("Password");
        var signInButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In"));

        usernameField.fill("admin");
        passwordField.fill("Admin!1234");
        signInButton.click();
        page.waitForURL("**/dashboard**", new Page.WaitForURLOptions().setTimeout(10000));

        int cardCount = page.locator(".card").count();
        System.out.println("Found " + cardCount + " cards");
        Assertions.assertEquals(4, cardCount, "Expected 4 cards on dashboard!");
    }

    @Test
    void shouldSearchByKeyWord(Page page) {
        page.navigate("http://localhost:3000/login");

        var usernameField = page.getByPlaceholder("Username");
        var passwordField = page.getByPlaceholder("Password");
        var signInButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In"));

        usernameField.fill("admin");
        passwordField.fill("Admin!1234");
        signInButton.click();
        page.waitForURL("**/dashboard**", new Page.WaitForURLOptions().setTimeout(10000));

        // Wait for specific card to be visible
        page.getByText("Διαχείριση Ενδυμάτων").waitFor(new Locator.WaitForOptions().setTimeout(10000));

        // Click materials card
        page.getByText("Διαχείριση Ενδυμάτων").click();
        page.waitForURL("**/manage-materials**");
        page.locator("input[placeholder*='Φίλτρο']").fill("Μπλούζα");
        page.waitForTimeout(3000);
        page.locator("button:has-text('Επεξεργασία')").first().click();
    }

    @DisplayName("Using title")
    @Test
    void title(Page page) {
        page.navigate("http://localhost:3000/login");

        var usernameField = page.getByPlaceholder("Username");
        var passwordField = page.getByPlaceholder("Password");
        var signInButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In"));

        usernameField.fill("admin");
        passwordField.fill("Admin!1234");
        signInButton.click();
        List<String> alertMessages = page.locator(".error-message").allTextContents();
        Assertions.assertTrue(alertMessages.isEmpty());

        page.getByText("Διαχείριση Χρηστών").first().click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Διαχείριση Χρηστών"))).isVisible();
    }
}
