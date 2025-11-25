package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.UsersPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static gr.clothesmanager.helpers.AuthenticationHelper.loginAsAdmin;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UsePlaywright(HeadlessChromeOptions.class)
public class UserDeleteTests  {
    
    @Test
    @DisplayName("Should delete user successfully")
    public void shouldDeleteUserSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        UsersPage usersPage = dashboardPage.navigateToUsers();
        usersPage.waitForLoad();
        
        int enabledDeleteCount = usersPage.getEnabledDeleteButtonCount();
        
        if (enabledDeleteCount == 0) {
            System.out.println("No deletable users, skipping test");
            return;
        }
        
        int initialCount = usersPage.getUserCount();
        
        usersPage.deleteFirstEnabledUser();
        
        int finalCount = usersPage.getUserCount();
        Assertions.assertThat(finalCount).isEqualTo(initialCount - 1);
    }
}