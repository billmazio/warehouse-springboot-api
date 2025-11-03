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

        page.getByTestId("username-input").fill("admin");
        page.getByTestId("password-input").fill("Admin!1234");
        page.getByTestId("username-input").clear();
        page.getByTestId("password-input").clear();
        page.getByTestId("signInButton").click();

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
    void shouldValidateShortValues(Page page) {
        page.navigate("http://localhost:3000/login");

        page.getByTestId("username-input").fill("ss");
        page.getByTestId("password-input").fill("short");
        page.getByTestId("signInButton").click();

        var usernameError = page.getByTestId("username-error");
        var passwordError = page.getByTestId("password-error");

        assertThat(usernameError).isVisible();
        assertThat(passwordError).isVisible();
        assertThat(usernameError).containsText("Το όνομα χρήστη πρέπει να είναι από 3 έως 50 χαρακτήρες.");
        assertThat(passwordError).containsText("Ο κωδικός πρέπει να έχει τουλάχιστον 6 χαρακτήρες.");
        assertThat(page.locator(".error-message")).hasCount(2);
    }

    @Test
    @DisplayName("Should show invalid credentials error")
    void shouldShowInvalidCredentialsError(Page page) {
        page.navigate("http://localhost:3000/login");

        page.getByTestId("username-input").fill("invalidUser");
        page.getByTestId("password-input").fill("invalidPass");
        page.getByTestId("signInButton").click();

        var errorMessage = page.getByTestId("login-error");
        assertThat(errorMessage).isVisible();
        assertThat(errorMessage).containsText("Invalid username or password");
    }

    @Test
    void shouldShowLogout(Page page) {
        page.navigate("http://localhost:3000/login");

        page.getByTestId("username-input").fill("admin");
        page.getByTestId("password-input").fill("Admin!1234");
        page.getByTestId("signInButton").click();

        page.waitForURL("**/dashboard**", new Page.WaitForURLOptions().setTimeout(10000));

        var logOutButton = page.getByTestId("logout-button");
        assertThat(logOutButton).isVisible();
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

    //todo fix method about visible fields or not because of the roles ADMIN and simple USER using assertJ!!!
    //todo fix method about dropDown lists that is in the chapter 6 of udemy course the way how to do!!!

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
