package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import gr.clothesmanager.components.ConfirmationDialog;

import java.util.List;

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
    private final Locator storeRows;
    private final Locator storeTitleInput;
    private final Locator storeAddressInput;
    private final Locator storeStatusSelect;
    private final Locator createButton;
    private final Locator editButton;
    private final Locator editAddressInput;
    private final Locator editSubmitButton;


    public StoresPage(Page page) {
        super(page);
        this.confirmationDialog = new ConfirmationDialog(page);
        this.storeRows = page.getByTestId(STORE_ROW);
        this.storeTitleInput = page.getByTestId(STORE_CREATE_TITLE);
        this.storeAddressInput = page.getByTestId(STORE_CREATE_ADDRESS);
        this.storeStatusSelect = page.getByTestId(STORE_CREATE_STATUS);
        this.createButton = page.getByTestId(CREATE_STORE_BUTTON);
        this.editButton = page.getByTestId(EDIT_BUTTON);
        this.editAddressInput = page.getByTestId(EDIT_STORE_ADDRESS);
        this.editSubmitButton = page.getByTestId(EDIT_STORE_SUBMIT);
    }

    public StoresPage waitForLoad() {
        waitForVisible(STORES_TABLE);
        waitForNetworkIdle();
        return this;
    }

    public void createStore(String title, String address, String status) {
        storeTitleInput.fill(title);
        storeAddressInput.fill(address);
        storeStatusSelect.selectOption(status);
        createButton.click();
    }

    public void editStore(String address) {
        editButton.first().click();
        waitForVisible(EDIT_STORE_MODAL);
        editAddressInput.fill(address);
        editSubmitButton.click();
        waitForHidden(EDIT_STORE_MODAL);
        waitForNetworkIdle();
    }

    public void deleteStore(String store) {
        Locator itemRow = itemRow(store);
        Locator deleteButton = itemRow.getByTestId(DELETE_BUTTON);

        deleteButton.click();
        confirmationDialog.confirmDelete();
        waitForNetworkIdle();
    }

    private Locator itemRow(String store) {
        return page.getByTestId(STORE_ROW)
                .filter(new Locator.FilterOptions().setHasText(store));
    }

    public Locator storeExists(String store) {
        return page.getByTestId(STORE_ROW)
                .filter(new Locator.FilterOptions().setHasText(store));
    }

    public List<String> storesList() { return storeRows.allTextContents();}
}