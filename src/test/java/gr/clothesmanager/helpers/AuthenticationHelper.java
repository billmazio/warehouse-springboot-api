package gr.clothesmanager.helpers;

import com.microsoft.playwright.Page;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.LoginPage;

public class AuthenticationHelper {

    private AuthenticationHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static DashboardPage loginAsAdmin(Page page) {
        LoginPage loginPage = new LoginPage(page);
        return loginPage.open()
                .loginAsAdmin()
                .waitForLoad();
    }
}