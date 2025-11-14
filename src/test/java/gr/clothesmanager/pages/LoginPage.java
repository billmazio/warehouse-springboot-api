package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import gr.clothesmanager.constants.TestConstants;

/**
 * Page Object for Login page
 * Handles user authentication and login validations
 *
 * @author Bill Maziotis
 */
public class LoginPage extends BasePage {

    private static final String USERNAME_INPUT = "username-input";
    private static final String PASSWORD_INPUT = "password-input";
    private static final String SIGN_IN_BUTTON = "sign-in-button";
    private static final String USERNAME_ERROR = "username-error";
    private static final String PASSWORD_ERROR = "password-error";
    private static final String LOGIN_ERROR = "login-error";

    public LoginPage(Page page) {
        super(page);
    }

    public LoginPage open() {
        navigate();
        waitForPageLoad();
        return this;
    }

    public void enterUsername(String username) {
        fillByTestId(USERNAME_INPUT, username);
    }

    public void enterPassword(String password) {
        fillByTestId(PASSWORD_INPUT, password);
    }

    public void clearUsername() {
        clearByTestId(USERNAME_INPUT);
    }

    public void clearPassword() {
        clearByTestId(PASSWORD_INPUT);
    }

    public void clickSignIn() {
        clickByTestId(SIGN_IN_BUTTON);
    }

    /**
     * Complete login workflow - enters credentials and submits
     * @param username Username to enter
     * @param password Password to enter
     * @return DashboardPage after successful navigation
     */
    public DashboardPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickSignIn();
        return new DashboardPage(page);
    }

    /**
     * Convenience method to login as admin using default credentials
     * @return DashboardPage after successful login
     */
    public DashboardPage loginAsAdmin() {
        return loginAs(TestConstants.ADMIN_USERNAME, TestConstants.ADMIN_PASSWORD);
    }

    /**
     * Attempts login with invalid credentials (stays on login page)
     * @param username Invalid username
     * @param password Invalid password
     * @return LoginPage (stays on same page due to error)
     */
    public LoginPage attemptLoginWith(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickSignIn();
        return this; // Stay on LoginPage because login will fail
    }

    public boolean isUsernameErrorVisible() {
        return isVisible(USERNAME_ERROR);
    }

    public boolean isPasswordErrorVisible() {
        return isVisible(PASSWORD_ERROR);
    }

    public boolean isLoginErrorVisible() {
        return isVisible(LOGIN_ERROR);
    }

    /**
     * Get username error locator for complex assertions
     * @return Locator for username error element
     */
    public Locator getUsernameError() {
        return getByTestId(USERNAME_ERROR);
    }

    /**
     * Get password error locator for complex assertions
     * @return Locator for password error element
     */
    public Locator getPasswordError() {
        return getByTestId(PASSWORD_ERROR);
    }

    /**
     * Get login error locator for complex assertions
     * @return Locator for login error element
     */
    public Locator getLoginError() {
        return getByTestId(LOGIN_ERROR);
    }

    /**
     * Get page title for verification
     * @return Page title text
     */
    public String getPageTitle() {
        return getTitle();
    }

    /**
     * Wait for login page to fully load
     */
    private void waitForPageLoad() {
        waitForVisible(USERNAME_INPUT);
        waitForVisible(PASSWORD_INPUT);
        waitForNetworkIdle();
    }
}