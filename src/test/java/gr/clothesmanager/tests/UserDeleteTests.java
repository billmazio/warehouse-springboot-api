package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.base.BaseTest;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.UsersPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("User Delete Tests")
public class UserDeleteTests extends BaseTest {
    
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
        assertEquals(initialCount - 1, finalCount,
            "User count should decrease by 1 after deletion");
    }
}