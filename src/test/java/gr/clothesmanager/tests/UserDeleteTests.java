package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.helpers.AuthenticationHelper;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.StoresPage;
import gr.clothesmanager.pages.UsersPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@UsePlaywright(HeadlessChromeOptions.class)
public class UserDeleteTests  {

    @Test
    @DisplayName("Should delete user successfully")
    public void shouldDeleteUserSuccessfully(Page page) {
        DashboardPage dashboardPage = AuthenticationHelper.loginAsAdmin(page);

        StoresPage storesPage = dashboardPage.navigateToStores();
        storesPage.waitForLoad();

        String uniqueStore = TestConstants.uniqueStoreName("ΑΝΑΤΟΛΙΚΑ");
        storesPage.createStore(uniqueStore, "ΑΝΑΤΟΛΙΚΑ", TestConstants.STATUS_ACTIVE);

        dashboardPage.goToDashboard();

        UsersPage usersPage = dashboardPage.navigateToUsers();
        usersPage.waitForLoad();

        String uniqueUser = TestConstants.uniqueUserName("User95");
        usersPage.createUser(uniqueUser, "Basil3263@", TestConstants.ROLE_LOCAL_ADMIN, TestConstants.STATUS_INACTIVE, uniqueStore);

        usersPage.deleteUser(uniqueUser);

        Assertions.assertThat(usersPage.usersList()).doesNotContain(uniqueUser);
    }
}