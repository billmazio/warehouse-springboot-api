package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.base.BaseTest;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.LoginPage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UsePlaywright
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Login Functionality Tests")
public class LoginTests extends BaseTest {
    
    @Test
    @Order(1)
    @DisplayName("TC_001: Should load login page with correct title")
    public void shouldLoadLoginPageWithCorrectTitle(Page page) {
        LoginPage loginPage = getLoginPage(page).open();
        
        String title = loginPage.getPageTitle();
        assertTrue(title.contains("Warehouse Management System"),
            "Page title should contain 'Warehouse Management System'");
    }

    @Test
    @Order(2)
    @DisplayName("TC_002: Should validate short username and password values")
    public void shouldValidateShortValues(Page page) {
        LoginPage loginPage = getLoginPage(page).open();

        loginPage.loginWithInvalidCredentials("ss", "short");

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
    @DisplayName("TC_003: Should show error for empty mandatory fields")
    public void shouldShowErrorForEmptyFields(String fieldName, Page page) {
        LoginPage loginPage = getLoginPage(page).open();
        
        loginPage.enterUsername(TestConstants.ADMIN_USERNAME)
                 .enterPassword(TestConstants.ADMIN_PASSWORD)
                 .clearUsername()
                 .clearPassword()
                 .clickSignIn();
        
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
    @DisplayName("TC_004: Should show invalid credentials error")
    public void shouldShowInvalidCredentialsError(Page page) {
        LoginPage loginPage = getLoginPage(page).open();
        
        loginPage.loginWithInvalidCredentials("invalidUser", "invalidPass");
        
        assertThat(loginPage.getLoginError()).isVisible();
        assertThat(loginPage.getLoginError()).containsText("Invalid username or password");
    }
    
    @Test
    @Order(5)
    @DisplayName("TC_005: Should login and logout successfully")
    public void shouldLoginAndLogoutSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        
        assertTrue(dashboardPage.isLogoutButtonVisible(), "Logout button should be visible");

        LoginPage loginPage = dashboardPage.logout();
        
        assertThat(page).hasURL(TestConstants.LOGIN_URL);
    }
}