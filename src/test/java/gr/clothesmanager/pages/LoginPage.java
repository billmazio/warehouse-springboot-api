package gr.clothesmanager.pages;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import gr.clothesmanager.constants.TestConstants;
import lombok.Getter;

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

    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator signInButton;
    @Getter
    private final Locator usernameError;
    @Getter
    private final Locator passwordError;
    @Getter
    private final Locator loginError;

    public LoginPage(Page page) {
        super(page);
        this.usernameInput = page.getByTestId(USERNAME_INPUT);
        this.passwordInput = page.getByTestId(PASSWORD_INPUT);
        this.signInButton = page.getByTestId(SIGN_IN_BUTTON);
        this.usernameError = page.getByTestId(USERNAME_ERROR);
        this.passwordError = page.getByTestId(PASSWORD_ERROR);
        this.loginError = page.getByTestId(LOGIN_ERROR);
    }

    public LoginPage open() {
        navigate();
        waitForPageLoad();
        return this;
    }

    public void enterUsername(String username) {
        usernameInput.fill(username);
    }

    public void enterPassword(String password) {
        passwordInput.fill(password);
    }

    public void clearUsername() {
        usernameInput.clear();
    }

    public void clearPassword() {
        passwordInput.clear();
    }

    public void clickSignIn() {
        signInButton.click();
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

    public String getPageTitle() {
        return getTitle();
    }

    private void waitForPageLoad() {
        usernameInput.isVisible();
        passwordInput.isVisible();
        waitForNetworkIdle();
    }
}