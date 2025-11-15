package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import gr.clothesmanager.components.ConfirmationDialog;
import gr.clothesmanager.constants.TestConstants;

/**
 * Page Object for Stores management page
 * Handles CRUD operations for stores
 * Users are associated with stores
 *
 * @author Bill Maziotis
 */
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

    private void fillStoreTitle(String title) {
        fillByTestId(STORE_CREATE_TITLE, title);
    }

    private void fillStoreAddress(String address) {
        fillByTestId(STORE_CREATE_ADDRESS, address);
    }

    private void selectStoreStatus(String status) {
        selectOptionByTestId(STORE_CREATE_STATUS, status);
    }

    private void clickCreateStoreButton() {
        clickByTestId(CREATE_STORE_BUTTON);
        waitForNetworkIdle();
        pause(TestConstants.WAIT_FOR_LOAD);
    }

    /**
     * Creates a new store with all required fields
     * @param title Store title/name
     * @param address Store address
     * @param status Store status (ACTIVE/INACTIVE)
     */
    public void createStore(String title, String address, String status) {
        fillStoreTitle(title);
        fillStoreAddress(address);
        selectStoreStatus(status);
        clickCreateStoreButton();
    }

    private void clickEditFirstStore() {
        page.locator("[data-test='" + EDIT_BUTTON + "']").first().click();
        waitForVisible(EDIT_STORE_MODAL);
    }

    private void fillEditStoreAddress(String address) {
        fillByTestId(EDIT_STORE_ADDRESS, address);
    }

    private void confirmEditStore() {
        clickByTestId(EDIT_STORE_SUBMIT);
        waitForHidden(EDIT_STORE_MODAL);
        waitForNetworkIdle();
    }

    /**
     * Edits the address of the first store in the list
     * @param address New address for the store
     */
    public void editFirstStoreAddress(String address) {
        clickEditFirstStore();
        fillEditStoreAddress(address);
        confirmEditStore();
    }

    /**
     * Deletes the first enabled store in the list
     * Only deletes stores that have enabled delete buttons (not system stores)
     * Waits for deletion to complete
     */
    public void deleteFirstEnabledStore() {
        Locator enabledDeleteButtons = page.locator("[data-test='" + DELETE_BUTTON + "']:not([disabled])");

        if (enabledDeleteButtons.count() == 0) {
            return;
        }

        int countBeforeDelete = getStoreCount();

        enabledDeleteButtons.first().click();
        confirmationDialog.confirmDelete();
        waitForNetworkIdle();

        page.waitForCondition(() -> getStoreCount() < countBeforeDelete);
    }

    /**
     * Gets the current count of stores displayed
     * @return Number of store rows
     */
    public int getStoreCount() {
        return getCount("[data-test='" + STORE_ROW + "']");
    }

    /**
     * Checks if any stores are present
     * @return true if at least one store exists
     */
    public boolean hasStores() {
        return getStoreCount() > 0;
    }

    /**
     * Gets count of stores with enabled delete buttons
     * System stores may have disabled delete buttons
     * @return Number of deletable stores
     */
    public int getEnabledDeleteButtonCount() {
        return page.locator("[data-test='" + DELETE_BUTTON + "']:not([disabled])").count();
    }

    /**
     * Gets a locator for text verification
     * Useful for checking if specific store name/address is visible
     * @param text Text to locate
     * @return Locator for the text
     */
    public Locator getTextLocator(String text) {
        return page.getByText(text);
    }
}