package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import gr.clothesmanager.components.ConfirmationDialog;

import java.util.List;

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
    private static final String CREATE_USER_BUTTON = "user-create-submit";

    private static final String DELETE_BUTTON = "delete-button";

    private final ConfirmationDialog confirmationDialog;
    private final Locator userRows;
    private final Locator userNameInput;
    private final Locator passwordInput;
    private final Locator roleInput;
    private final Locator statusInput;
    private final Locator storeInput;
    private final Locator createUserButton;

    public UsersPage(Page page) {
        super(page);
        this.confirmationDialog = new ConfirmationDialog(page);
        this.userRows = page.getByTestId(USER_ROW);
        this.userNameInput = page.getByTestId(USER_CREATE_USERNAME);
        this.passwordInput = page.getByTestId(USER_CREATE_PASSWORD);
        this.roleInput = page.getByTestId(USER_CREATE_ROLE);
        this.statusInput = page.getByTestId(USER_CREATE_STATUS);
        this.storeInput = page.getByTestId(USER_CREATE_STORE);
        this.createUserButton = page.getByTestId(CREATE_USER_BUTTON);
    }

    public UsersPage waitForLoad() {
        waitForVisible(USER_TABLE);
        waitForNetworkIdle();
        return this;
    }

    public void createUser(String username, String password,String role,String status,String store) {
        userNameInput.fill(username);
        passwordInput.fill(password);
        roleInput.selectOption(role);
        statusInput.selectOption(status);
        storeInput.selectOption(store);
        createUserButton.click();
    }

    public void deleteUser(String username) {
        Locator itemRow = itemRow(username);
        Locator deleteButton = itemRow.getByTestId(DELETE_BUTTON);

        deleteButton.click();
        confirmationDialog.confirmDelete();
        waitForNetworkIdle();
    }

    private Locator itemRow(String username) {
        return page.getByTestId(USER_ROW)
                .filter(new Locator.FilterOptions().setHasText(username));
    }

    public Locator userExists(String username) {
        return page.getByTestId(USER_ROW)
                .filter(new Locator.FilterOptions().setHasText(username));
    }

    public List<String> usersList() { return userRows.allTextContents(); }
}