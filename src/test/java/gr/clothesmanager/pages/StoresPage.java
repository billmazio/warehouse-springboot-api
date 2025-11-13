package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import gr.clothesmanager.components.ConfirmationDialog;
import gr.clothesmanager.constants.TestConstants;

public class StoresPage extends BasePage {

    private static final String STORES_TABLE = "stores-table";
    private static final String STORE_ROW = "store-row";

    private static final String STORE_CREATE_TITLE = "store-create-title";
    private static final String STORE_CREATE_ADDRESS = "store-create-address";
    private static final String STORE_CREATE_STATUS = "store-create-status";
    private static final String CREATE_STORE_BUTTON = "create-store";

    private static final String EDIT_BUTTON = "edit-button";
    private static final String EDIT_STORE_MODAL = "edit-store-modal";
    private static final String EDIT_STORE_ADDRESS = "edit-store-address";
    private static final String EDIT_STORE_SUBMIT = "edit-store-submit";

    private static final String DELETE_BUTTON = "delete-button";
    
    private final ConfirmationDialog confirmationDialog;
    
    public StoresPage(Page page) {
        super(page);
        this.confirmationDialog = new ConfirmationDialog(page);
    }

    public StoresPage waitForLoad() {
        waitForVisible(STORES_TABLE);
        waitForNetworkIdle();
        return this;
    }

    public StoresPage fillTitle(String title) {
        fillByTestId(STORE_CREATE_TITLE, title);
        return this;
    }
    
    public StoresPage fillAddress(String address) {
        fillByTestId(STORE_CREATE_ADDRESS, address);
        return this;
    }
    
    public StoresPage selectStatus(String status) {
        selectOptionByTestId(STORE_CREATE_STATUS, status);
        return this;
    }
    
    public StoresPage clickCreateStore() {
        clickByTestId(CREATE_STORE_BUTTON);
        waitForNetworkIdle();
        pause(TestConstants.WAIT_FOR_LOAD);
        return this;
    }

    public StoresPage createStore(String title, String address, String status) {
        fillTitle(title);
        fillAddress(address);
        selectStatus(status);
        clickCreateStore();
        return this;
    }

    public StoresPage clickEditFirstStore() {
        page.locator("[data-test='" + EDIT_BUTTON + "']").first().click();
        waitForVisible(EDIT_STORE_MODAL);
        return this;
    }
    
    public StoresPage editAddress(String address) {
        fillByTestId(EDIT_STORE_ADDRESS, address);
        return this;
    }
    
    public StoresPage confirmEditStore() {
        clickByTestId(EDIT_STORE_SUBMIT);
        waitForHidden(EDIT_STORE_MODAL);
        waitForNetworkIdle();
        return this;
    }
    
    public StoresPage editFirstStoreAddress(String address) {
        clickEditFirstStore();
        editAddress(address);
        confirmEditStore();
        return this;
    }

    public StoresPage clickDeleteFirstEnabledStore() {
        Locator enabledDeleteButtons = page.locator("[data-test='" + DELETE_BUTTON + "']:not([disabled])");
        if (enabledDeleteButtons.count() > 0) {
            enabledDeleteButtons.first().click();
        }
        return this;
    }
    
    public StoresPage confirmDelete() {
        confirmationDialog.confirmDelete();
        waitForNetworkIdle();
        pause(2000);
        return this;
    }
    
    public StoresPage deleteFirstEnabledStore() {
        clickDeleteFirstEnabledStore();
        confirmDelete();
        return this;
    }

    public boolean isEditStoreModalVisible() {
        return isVisible(EDIT_STORE_MODAL);
    }
    
    public boolean isConfirmationDialogVisible() {
        return confirmationDialog.isVisible();
    }
    
    public int getStoreCount() {
        return getCount("[data-test='" + STORE_ROW + "']");
    }
    
    public boolean hasStores() {
        return getStoreCount() > 0;
    }
    
    public int getEnabledDeleteButtonCount() {return page.locator("[data-test='" + DELETE_BUTTON + "']:not([disabled])").count();}
    
    public Locator getTextLocator(String text) {
        return page.getByText(text);
    }
}