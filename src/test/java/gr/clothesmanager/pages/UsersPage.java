package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import gr.clothesmanager.components.ConfirmationDialog;
import gr.clothesmanager.constants.TestConstants;

/**
 * Page Object for Users management page
 * Handles user creation and deletion
 * Users must be associated with a store
 *
 * @author Bill Maziotis
 */
public class UsersPage extends BasePage {

    private static final String USER_TABLE = "user-table";
    private static final String USER_ROW = "user-row";

    private static final String USER_CREATE_USERNAME = "user-create-username";
    private static final String USER_CREATE_PASSWORD = "user-create-password";
    private static final String USER_CREATE_ROLE = "user-create-role";
    private static final String USER_CREATE_STATUS = "user-create-status";
    private static final String USER_CREATE_STORE = "user-create-store";
    private static final String CREATE_USER_BUTTON = ".user-create-form .create-button";

    private static final String DELETE_BUTTON = "delete-button";

    private final ConfirmationDialog confirmationDialog;

    public UsersPage(Page page) {
        super(page);
        this.confirmationDialog = new ConfirmationDialog(page);
    }

    public UsersPage waitForLoad() {
        waitForVisible(USER_TABLE);
        waitForNetworkIdle();
        return this;
    }

    private void fillUserUsername(String username) {
        fillByTestId(USER_CREATE_USERNAME, username);
    }

    private void fillUserPassword(String password) {
        fillByTestId(USER_CREATE_PASSWORD, password);
    }

    private void selectUserRole(String role) {
        selectOptionByTestId(USER_CREATE_ROLE, role);
    }

    private void selectUserStatus(String status) {
        selectOptionByTestId(USER_CREATE_STATUS, status);
    }

    private void selectUserStore(String store) {
        selectOptionByTestId(USER_CREATE_STORE, store);
    }

    private void clickCreateUserButton() {
        click(CREATE_USER_BUTTON);
        waitForNetworkIdle();
        pause(TestConstants.WAIT_FOR_LOAD);
    }

    /**
     * Creates a new user with all required fields
     * User must be associated with a store
     *
     * @param username Unique username for the user
     * @param password User password
     * @param role User role (LOCAL_ADMIN, etc.)
     * @param store Store to associate user with
     */
    public void createUser(String username, String password,
                           String store, String role) {
        fillUserUsername(username);
        fillUserPassword(password);
        selectUserStore(store);
        selectUserRole(role);
        selectUserStatus(TestConstants.STATUS_ACTIVE);  // Default to ACTIVE
        clickCreateUserButton();
    }

    /**
     * Deletes the first enabled user in the list
     * Only deletes users with enabled delete buttons (not system users)
     * Waits for deletion to complete
     */
    public void deleteFirstEnabledUser() {
        Locator enabledDeleteButtons = page.locator("[data-test='" + DELETE_BUTTON + "']:not([disabled])");

        if (enabledDeleteButtons.count() == 0) {
            return;
        }

        int countBeforeDelete = getUserCount();

        enabledDeleteButtons.first().click();
        confirmationDialog.confirmDelete();
        waitForNetworkIdle();

        page.waitForCondition(() -> getUserCount() < countBeforeDelete);
    }

    /**
     * Gets the current count of users displayed
     * @return Number of user rows
     */
    public int getUserCount() {
        return getCount("[data-test='" + USER_ROW + "']");
    }

    /**
     * Checks if any users are present
     * @return true if at least one user exists
     */
    public boolean hasUsers() {
        return getUserCount() > 0;
    }

    /**
     * Gets count of users with enabled delete buttons
     * System/admin users may have disabled delete buttons
     * @return Number of deletable users
     */
    public int getEnabledDeleteButtonCount() {
        return page.locator("[data-test='" + DELETE_BUTTON + "']:not([disabled])").count();
    }

    /**
     * Gets a locator for text verification
     * Useful for checking if specific username is visible
     * @param text Text to locate
     * @return Locator for the text
     */
    public Locator getTextLocator(String text) {
        return page.getByText(text);
    }

    /**
     * Check if a user exists in the list
     * @param username Username to check
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String username) {
        try {
            return page.locator("text=" + username).isVisible();
        } catch (Exception e) {
            return false;
        }
    }
}