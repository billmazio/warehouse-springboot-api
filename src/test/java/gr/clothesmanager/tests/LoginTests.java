package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.helpers.AuthenticationHelper;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.LoginPage;
import org.junit.jupiter.api.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UsePlaywright(HeadlessChromeOptions.class)
public class LoginTests {

    @Test
    @Order(1)
    @DisplayName("Should load login page with correct title")
    public void shouldLoadLoginPageWithCorrectTitle(Page page) {
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();

        String title = loginPage.getPageTitle();
        Assertions.assertThat(title).contains("Warehouse Management System");
    }

    @Test
    @Order(2)
    @DisplayName("Should validate short username and password values")
    public void shouldValidateShortValues(Page page) {
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();

        loginPage.attemptLoginWith("ss", "short");

        assertThat(loginPage.getUsernameError()).isVisible();
        assertThat(loginPage.getPasswordError()).isVisible();
        assertThat(loginPage.getUsernameError()).containsText(
                "Το όνομα χρήστη πρέπει να είναι από 3 έως 50 χαρακτήρες.");
        assertThat(loginPage.getPasswordError()).containsText(
                "Ο κωδικός πρέπει να έχει τουλάχιστον 6 χαρακτήρες.");
    }

    @Order(3)
    @ParameterizedTest
    @ValueSource(strings = {"username", "password"})
    @DisplayName("Should show error for empty mandatory fields")
    public void shouldShowErrorForEmptyFields(String fieldName, Page page) {
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();

        loginPage.enterUsername(TestConstants.ADMIN_USERNAME);
        loginPage.enterPassword(TestConstants.ADMIN_PASSWORD);
        loginPage.clearUsername();
        loginPage.clearPassword();
        loginPage.clickSignIn();

        if (fieldName.equals("username")) {
            assertThat(loginPage.getUsernameError()).isVisible();
            assertThat(loginPage.getUsernameError()).containsText("Το πεδίο είναι υποχρεωτικό");
        } else {
            assertThat(loginPage.getPasswordError()).isVisible();
            assertThat(loginPage.getPasswordError()).containsText("Το πεδίο είναι υποχρεωτικό");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Should show invalid credentials error")
    public void shouldShowInvalidCredentialsError(Page page) {
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();

        loginPage.attemptLoginWith("invalidUser", "invalidPass");

        assertThat(loginPage.getLoginError()).isVisible();
        assertThat(loginPage.getLoginError()).containsText("Invalid username or password");
    }

    @Test
    @Order(5)
    @DisplayName("Should login and logout successfully")
    public void shouldLoginAndLogoutSuccessfully(Page page) {
        DashboardPage dashboardPage = AuthenticationHelper.loginAsAdmin(page);

        Assertions.assertThat(dashboardPage.isLogoutButtonVisible()).isTrue();

        dashboardPage.logout();

        assertThat(page).hasURL(TestConstants.LOGIN_URL);
    }
}