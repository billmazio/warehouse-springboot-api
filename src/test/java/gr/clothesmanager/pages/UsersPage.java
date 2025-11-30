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

    public void createUser(String username, String password,
                           String store, String role) {
        fillUserUsername(username);
        fillUserPassword(password);
        selectUserStore(store);
        selectUserRole(role);
        selectUserStatus(TestConstants.STATUS_ACTIVE);  // Default to ACTIVE
        clickCreateUserButton();
    }

    public void deleteUser() {
        Locator enabledDeleteButtons = page.locator("[data-test='" + DELETE_BUTTON + "']:not([disabled])");

        if (enabledDeleteButtons.count() < 2) {
            return;
        }

        int countBeforeDelete = getUserCount();

        enabledDeleteButtons.nth(1).click();
        confirmationDialog.confirmDelete();
        waitForNetworkIdle();

        page.waitForCondition(() -> getUserCount() < countBeforeDelete);
    }

    public int getUserCount() {
        return getCountByTestId(USER_ROW);
    }

    public int getEnabledDeleteButtonCount() {
        return page.locator("[data-test='" + DELETE_BUTTON + "']:not([disabled])").count();
    }

    public boolean userExists(String username) {return page.getByText(username).count() > 0;}
}