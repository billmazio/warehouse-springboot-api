package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import gr.clothesmanager.components.ConfirmationDialog;
import gr.clothesmanager.constants.TestConstants;


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

    public UsersPage fillUsername(String username) {
        fillByTestId(USER_CREATE_USERNAME, username);
        return this;
    }
    
    public UsersPage fillPassword(String password) {
        fillByTestId(USER_CREATE_PASSWORD, password);
        return this;
    }
    
    public UsersPage selectRole(String role) {
        selectOptionByTestId(USER_CREATE_ROLE, role);
        return this;
    }
    
    public UsersPage selectStatus(String status) {
        selectOptionByTestId(USER_CREATE_STATUS, status);
        return this;
    }
    
    public UsersPage selectStore(String store) {
        selectOptionByTestId(USER_CREATE_STORE, store);
        return this;
    }
    
    public UsersPage clickCreateUser() {
        click(CREATE_USER_BUTTON);
        waitForNetworkIdle();
        pause(TestConstants.WAIT_FOR_LOAD);
        return this;
    }

    public UsersPage createUser(String username, String password, String role, 
                                 String status, String store) {
        fillUsername(username);
        fillPassword(password);
        selectRole(role);
        selectStatus(status);
        selectStore(store);
        clickCreateUser();
        return this;
    }

    public UsersPage clickDeleteFirstEnabledUser() {
        Locator enabledDeleteButtons = page.locator("[data-test='" + DELETE_BUTTON + "']:not([disabled])");
        if (enabledDeleteButtons.count() > 0) {
            enabledDeleteButtons.first().click();
        }
        return this;
    }
    
    public UsersPage confirmDelete() {
        confirmationDialog.confirmDelete();
        waitForNetworkIdle();
        pause(2000);
        return this;
    }
    
    public UsersPage deleteFirstEnabledUser() {
        clickDeleteFirstEnabledUser();
        confirmDelete();
        return this;
    }

    public boolean isConfirmationDialogVisible() {
        return confirmationDialog.isVisible();
    }
    
    public int getUserCount() {
        return getCount("[data-test='" + USER_ROW + "']");
    }
    
    public boolean hasUsers() {
        return getUserCount() > 0;
    }
    
    public int getEnabledDeleteButtonCount() {return page.locator("[data-test='" + DELETE_BUTTON + "']:not([disabled])").count();}
    
    public Locator getTextLocator(String text) {
        return page.getByText(text);
    }
}