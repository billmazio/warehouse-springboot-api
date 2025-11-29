package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.StoresPage;
import gr.clothesmanager.pages.UsersPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static gr.clothesmanager.helpers.AuthenticationHelper.loginAsAdmin;


@UsePlaywright(HeadlessChromeOptions.class)
public class UserCreateTests {

    @Test
    @DisplayName("Should create new user successfully")
    public void shouldCreateNewUserSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        dashboardPage.waitForLoad();
        StoresPage storesPage = dashboardPage.navigateToStores();
        storesPage.waitForLoad();

        String uniqueStore = TestConstants.uniqueStoreName("TESTSTORE");

        storesPage.createStore(uniqueStore, "Test Address", TestConstants.STATUS_ACTIVE);

        dashboardPage.goToDashboard();

        UsersPage usersPage = dashboardPage.navigateToUsers();
        usersPage.waitForLoad();

        String uniqueUser = TestConstants.uniqueUserName("testuser");

        usersPage.createUser(
                uniqueUser,
                "Test123!",
                uniqueStore,
                TestConstants.ROLE_LOCAL_ADMIN
        );


        Assertions.assertThat(usersPage.userExists(uniqueUser)).isTrue();
    }
}