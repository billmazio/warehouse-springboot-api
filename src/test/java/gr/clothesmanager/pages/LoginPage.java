package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import gr.clothesmanager.constants.TestConstants;
import org.springframework.security.access.method.P;

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

    public DashboardPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickSignIn();
        return new DashboardPage(page);
    }

    public DashboardPage loginAsAdmin() {
        return loginAs(TestConstants.ADMIN_USERNAME, TestConstants.ADMIN_PASSWORD);
    }

    public void attemptLoginWith(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickSignIn();
    }

    public Locator getUsernameError() {
        return getByTestId(USERNAME_ERROR);
    }

    public Locator getPasswordError() {
        return getByTestId(PASSWORD_ERROR);
    }

    public Locator getLoginError() {
        return getByTestId(LOGIN_ERROR);
    }

    public String getPageTitle() {
        return getTitle();
    }

    private void waitForPageLoad() {
        waitForVisible(USERNAME_INPUT);
        waitForVisible(PASSWORD_INPUT);
        waitForNetworkIdle();
    }
}