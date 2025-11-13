package gr.clothesmanager.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import gr.clothesmanager.components.ConfirmationDialog;


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

    public MaterialsPage clickAddMaterial() {
        clickByTestId(ADD_MATERIAL_BUTTON);
        waitForVisible(ADD_MATERIAL_MODAL);
        return this;
    }
    
    public MaterialsPage fillMaterialName(String name) {
        fillByTestId(ADD_MATERIAL_TEXT, name);
        return this;
    }
    
    public MaterialsPage selectSize(String size) {
        selectOptionByTestId(ADD_MATERIAL_SIZE, size);
        return this;
    }
    
    public MaterialsPage fillQuantity(String quantity) {
        fillByTestId(ADD_MATERIAL_QUANTITY, quantity);
        return this;
    }
    
    public MaterialsPage selectStore(String store) {
        selectOptionByTestId(ADD_MATERIAL_STORE, store);
        return this;
    }
    
    public MaterialsPage submitAddMaterial() {
        clickByTestId(ADD_MATERIAL_SUBMIT);
        waitForHidden(ADD_MATERIAL_MODAL);
        return this;
    }

    public MaterialsPage addMaterial(String name, String size, String quantity, String store) {
        clickAddMaterial();
        fillMaterialName(name);
        selectSize(size);
        fillQuantity(quantity);
        selectStore(store);
        submitAddMaterial();
        return this;
    }


    public MaterialsPage clickEditFirstMaterial() {
        page.locator("[data-test='" + EDIT_BUTTON + "']").first().click();
        waitForVisible(EDIT_MODAL);
        return this;
    }
    
    public MaterialsPage editMaterialName(String name) {
        fillByTestId(EDIT_TEXT, name);
        return this;
    }
    
    public MaterialsPage editSize(String size) {
        selectOptionByTestId(EDIT_SIZE, size);
        return this;
    }
    
    public MaterialsPage editQuantity(String quantity) {
        fillByTestId(EDIT_QUANTITY, quantity);
        return this;
    }
    
    public MaterialsPage confirmEdit() {
        clickByTestId(EDIT_CONFIRM);
        waitForHidden(EDIT_MODAL);
        return this;
    }

    public MaterialsPage editFirstMaterial(String name, String size, String quantity) {
        clickEditFirstMaterial();
        editMaterialName(name);
        editSize(size);
        editQuantity(quantity);
        confirmEdit();
        return this;
    }


    public MaterialsPage clickDeleteFirstMaterial() {
        page.locator("[data-test='" + DELETE_BUTTON + "']").first().click();
        return this;
    }

    public MaterialsPage confirmDelete() {
        confirmationDialog.confirmDelete();
        waitForNetworkIdle();
        return this;
    }

    public MaterialsPage deleteFirstMaterial() {
        int countBeforeDelete = getMaterialCount();
        clickDeleteFirstMaterial();
        confirmDelete();

        // Wait for count to decrease
        page.waitForCondition(() -> getMaterialCount() < countBeforeDelete);

        return this;
    }

    public MaterialsPage searchByProductName(String productName) {
        fillByTestId(FILTER_PRODUCT, productName);
        page.waitForLoadState(LoadState.NETWORKIDLE);
        return this;
    }
    
    public MaterialsPage filterBySize(String size) {
        selectOptionByTestId(FILTER_SIZE, size);
        page.waitForLoadState(LoadState.NETWORKIDLE);
        return this;
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
    
    public int getMaterialCount() {
        return getCount("[data-test='" + MATERIAL_ROW + "']");
    }
    
    public boolean hasMaterials() {
        return getMaterialCount() > 0;
    }
}