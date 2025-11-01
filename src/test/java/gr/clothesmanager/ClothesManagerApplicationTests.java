package gr.clothesmanager;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import jakarta.validation.Valid;
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
                    .setHeadless(false)
                    .setLaunchOptions(
                            new BrowserType.LaunchOptions()
                                    .setArgs(Arrays.asList("--no-sandbox", "--disable-gpu"))
                    );


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
        var signInButton = page.getByText("Sign In");

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
        var signInButton = page.getByText("Sign In");

        usernameField.fill("ss");
        passwordField.fill("short");
        signInButton.click();
        page.waitForTimeout(500);

        assertThat(page.locator(".error-message")).hasCount(2);
    }

    @Test
    void shouldShowInvalidCredentialsError(Page page) {
        page.navigate("http://localhost:3000/login");

        var usernameField = page.getByPlaceholder("Username");
        var passwordField = page.getByPlaceholder("Password");
        var signInButton = page.getByText("Sign In");

        usernameField.fill("invalidUser");
        passwordField.fill("invalidPass");
        signInButton.click();
        page.waitForTimeout(500);

        var errorMessage = page.getByRole(AriaRole.ALERT);
        assertThat(errorMessage).containsText("Invalid username or password");
    }

    @Test
    void shouldSearchByKeyWord(Page page) {
        // Login
        page.navigate("http://localhost:3000/login");
        page.waitForSelector("input[placeholder='Username']");
        page.locator("input[placeholder='Username']").fill("admin");
        page.locator("input[placeholder='Password']").fill("Admin!1234");
        page.locator("button:has-text('Sign In')").click();
        page.waitForURL("**/dashboard**", new Page.WaitForURLOptions().setTimeout(10000));

        // Navigate to dashboard
        page.navigate("http://localhost:3000/dashboard");

        // Wait for specific card to be visible
        page.locator("text=Διαχείριση Ενδυμάτων").waitFor(new Locator.WaitForOptions().setTimeout(10000));

        // Count cards
        int cardCount = page.locator(".card").count();
        System.out.println("Found " + cardCount + " cards");
        Assertions.assertEquals(4, cardCount, "Expected 4 cards on dashboard!");

        // Click materials card
        page.locator("div.card:has-text('Διαχείριση Ενδυμάτων')").click();
        page.waitForURL("**/manage-materials**");
        page.locator("input[placeholder*='Φίλτρο']").fill("Μπλούζα");
        page.waitForTimeout(3000);
        page.locator("button:has-text('Επεξεργασία')").first().click();
    }

    @DisplayName("Using title")
    @Test
    void title(Page page) {
        page.navigate("http://localhost:3000/login");
        page.locator("input[placeholder='Username']").fill("admin");
        page.locator("input[placeholder='Password']").fill("Admin!1234");
        page.locator("button:has-text('Sign In')").click();

        List<String> alertMessages = page.locator(".error-message").allTextContents();
        Assertions.assertTrue(alertMessages.isEmpty());

        page.getByText("Διαχείριση Χρηστών").first().click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Διαχείριση Χρηστών"))).isVisible();
    }
}
