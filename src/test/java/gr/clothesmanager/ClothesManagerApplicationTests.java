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

        @DisplayName("Mandatory fields")
        @ParameterizedTest
        @ValueSource(strings = {"Username","Password"})
        void mandatoryFields(String fieldName,Page page) {
            page.navigate("http://localhost:3000/login");

            var usernameField = page.getByPlaceholder("Username");
            var passwordField = page.getByPlaceholder("Password");
            var signInButton = page.getByText("Sign In");

            usernameField.fill("admin");
            passwordField.fill("Admin!1234");

            usernameField.clear();
            passwordField.clear();

            signInButton.click();

            Locator allErrors = page.locator(".error-message");
            int count = allErrors.count();

            for (int i = 0; i < count; i++) {
                Locator error = allErrors.nth(i);
                System.out.println("Error " + (i + 1) + ": " + error.textContent());
                assertThat(error).isVisible();
            }

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
