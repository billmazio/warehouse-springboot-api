package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.base.BaseTest;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.pages.DashboardPage;
import gr.clothesmanager.pages.UsersPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UsePlaywright
@DisplayName("User Create Tests")
public class UserCreateTests extends BaseTest {
    
    @Test
    @DisplayName("Should create new user successfully")
    public void shouldCreateUserSuccessfully(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        UsersPage usersPage = dashboardPage.navigateToUsers();
        usersPage.waitForLoad();
        
        int initialCount = usersPage.getUserCount();
        
        String uniqueUsername = "testuser" + System.currentTimeMillis();
        System.out.println("Creating user with username: " + uniqueUsername);
        
        usersPage.createUser(
            uniqueUsername,
            "TestPassword123!",
            TestConstants.ROLE_LOCAL_ADMIN,
            TestConstants.STATUS_ACTIVE,
            TestConstants.STORE_DYTIKA
        );
        
        int finalCount = usersPage.getUserCount();
        assertEquals(initialCount + 1, finalCount,
            "User count should increase by 1 after creation");
        assertThat(usersPage.getTextLocator(uniqueUsername)).isVisible();
    }
}