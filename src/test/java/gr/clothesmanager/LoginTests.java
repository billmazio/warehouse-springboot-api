package gr.clothesmanager;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
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
public class LoginTests extends BasePlaywrightTest {

    @Test
    @Order(1)
    @DisplayName("Should load login page with correct title")
    public void shouldLoadLoginPage(Page page) {
        page.navigate("http://localhost:3000/login");
        String title = page.title();
        Assertions.assertTrue(title.contains("Warehouse Management System"));
    }

    @Test
    @Order(2)
    @DisplayName("Should validate short username and password values")
    public void shouldValidateShortValues(Page page) {
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
    public void shouldShowErrorForEmptyFields(String fieldName, Page page) {
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
    public void shouldShowInvalidCredentialsError(Page page) {
        page.navigate("http://localhost:3000/login");

        page.getByTestId("username-input").fill("invalidUser");
        page.getByTestId("password-input").fill("invalidPass");
        page.getByTestId("sign-in-button").click();

        var errorMessage = page.getByTestId("login-error");
        assertThat(errorMessage).isVisible();
        assertThat(errorMessage).containsText("Invalid username or password");
    }

    @Test
    @Order(5)
    @DisplayName("Should login and logout successfully")
    public void shouldLoginAndLogout(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);

        var logOutButton = page.getByTestId("logout-button");
        assertThat(logOutButton).isVisible();

        logOutButton.click();

        page.waitForURL("**/login", new Page.WaitForURLOptions().setTimeout(5000));
        assertThat(page).hasURL("http://localhost:3000/login");
    }
}