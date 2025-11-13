package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import gr.clothesmanager.constants.TestConstants;


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
        navigate(TestConstants.LOGIN_URL);
        return this;
    }

    public LoginPage enterUsername(String username) {
        fillByTestId(USERNAME_INPUT, username);
        return this;
    }
    
    public LoginPage enterPassword(String password) {
        fillByTestId(PASSWORD_INPUT, password);
        return this;
    }
    
    public LoginPage clearUsername() {
        clearByTestId(USERNAME_INPUT);
        return this;
    }
    
    public LoginPage clearPassword() {
        clearByTestId(PASSWORD_INPUT);
        return this;
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
    
    public LoginPage loginWithInvalidCredentials(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickSignIn();
        return this;
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
    
    public Locator getUsernameError() {
        return getByTestId(USERNAME_ERROR);
    }
    
    public Locator getPasswordError() {return getByTestId(PASSWORD_ERROR);}
    
    public Locator getLoginError() {
        return getByTestId(LOGIN_ERROR);
    }
    
    public String getPageTitle() {
        return getTitle();
    }
}