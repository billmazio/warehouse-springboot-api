package gr.clothesmanager.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import gr.clothesmanager.components.ConfirmationDialog;

/**
 * Page Object for Materials management page
 * Handles CRUD operations for materials (clothing items)
 * Supports search and filtering by product name and size
 *
 * @author Bill Maziotis
 */
public class MaterialsPage extends BasePage {

    private static final String MATERIALS_TABLE = "materials-table";
    private static final String MATERIAL_ROW = "material-row";

    private static final String ADD_MATERIAL_BUTTON = "add-material-button";
    private static final String ADD_MATERIAL_MODAL = "add-material-modal";
    private static final String ADD_MATERIAL_TEXT = "add-material-text";
    private static final String ADD_MATERIAL_SIZE = "add-material-size";
    private static final String ADD_MATERIAL_QUANTITY = "add-material-quantity";
    private static final String ADD_MATERIAL_STORE = "add-material-store";
    private static final String ADD_MATERIAL_SUBMIT = "add-material-submit";

    private static final String EDIT_BUTTON = "edit-button";
    private static final String EDIT_MODAL = "edit-modal";
    private static final String EDIT_TEXT = "edit-text";
    private static final String EDIT_SIZE = "edit-size";
    private static final String EDIT_QUANTITY = "edit-quantity";
    private static final String EDIT_CONFIRM = "edit-confirm";

    private static final String DELETE_BUTTON = "delete-button";

    private static final String FILTER_PRODUCT = "filter-product";
    private static final String FILTER_SIZE = "filter-size";
    
    private final ConfirmationDialog confirmationDialog;
    
    public MaterialsPage(Page page) {
        super(page);
        this.confirmationDialog = new ConfirmationDialog(page);
    }

    public MaterialsPage waitForLoad() {
        waitForVisible(MATERIALS_TABLE);
        waitForNetworkIdle();
        return this;
    }

    private void clickAddMaterial() {
        clickByTestId(ADD_MATERIAL_BUTTON);
        waitForVisible(ADD_MATERIAL_MODAL);
    }

    private void fillMaterialName(String name) {
        fillByTestId(ADD_MATERIAL_TEXT, name);
    }

    private void selectMaterialSize(String size) {
        selectOptionByTestId(ADD_MATERIAL_SIZE, size);
    }

    private void fillMaterialQuantity(String quantity) {
        fillByTestId(ADD_MATERIAL_QUANTITY, quantity);
    }

    private void selectMaterialStore(String store) {
        selectOptionByTestId(ADD_MATERIAL_STORE, store);
    }

    private void submitAddMaterial() {
        clickByTestId(ADD_MATERIAL_SUBMIT);
        waitForHidden(ADD_MATERIAL_MODAL);
    }

    /**
     * Adds a new material with all required fields
     * @param name Material name (e.g., "Μπλούζα Polo")
     * @param size Material size (SMALL, MEDIUM, etc.)
     * @param quantity Initial quantity
     * @param store Store location
     */
    public void addMaterial(String name, String size, String quantity, String store) {
        clickAddMaterial();
        fillMaterialName(name);
        selectMaterialSize(size);
        fillMaterialQuantity(quantity);
        selectMaterialStore(store);
        submitAddMaterial();
    }

    private void clickEditFirstMaterial() {
        page.locator("[data-test='" + EDIT_BUTTON + "']").first().click();
        waitForVisible(EDIT_MODAL);
    }

    private void fillEditMaterialName(String name) {
        fillByTestId(EDIT_TEXT, name);
    }

    private void selectEditSize(String size) {
        selectOptionByTestId(EDIT_SIZE, size);
    }

    private void fillEditQuantity(String quantity) {
        fillByTestId(EDIT_QUANTITY, quantity);
    }

    private void confirmEdit() {
        clickByTestId(EDIT_CONFIRM);
        waitForHidden(EDIT_MODAL);
    }

    /**
     * Edits the first material in the list
     * @param name New material name
     * @param size New size
     * @param quantity New quantity
     */
    public void editFirstMaterial(String name, String size, String quantity) {
        clickEditFirstMaterial();
        fillEditMaterialName(name);
        selectEditSize(size);
        fillEditQuantity(quantity);
        confirmEdit();
    }


    /**
     * Deletes the first material in the list
     * Waits for deletion to complete by monitoring count change
     */
    public void deleteFirstMaterial() {
        int countBeforeDelete = getMaterialCount();

        page.locator("[data-test='" + DELETE_BUTTON + "']").first().click();
        confirmationDialog.confirmDelete();
        waitForNetworkIdle();

        page.waitForCondition(() -> getMaterialCount() < countBeforeDelete);
    }


    /**
     * Searches for materials by product name
     * @param productName Product name to search for
     */
    public void searchByProductName(String productName) {
        fillByTestId(FILTER_PRODUCT, productName);
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * Filters materials by size
     * @param size Size to filter by
     */
    public void filterBySize(String size) {
        selectOptionByTestId(FILTER_SIZE, size);
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    public boolean isAddMaterialModalVisible() {
        return isVisible(ADD_MATERIAL_MODAL);
    }

    public boolean isEditModalVisible() {
        return isVisible(EDIT_MODAL);
    }

    public boolean isConfirmationDialogVisible() {
        return confirmationDialog.isVisible();
    }

    /**
     * Gets the current count of materials displayed
     * @return Number of material rows
     */
    public int getMaterialCount() {
        return getCount("[data-test='" + MATERIAL_ROW + "']");
    }
}