package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import gr.clothesmanager.components.ConfirmationDialog;

import java.util.List;

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
    private final Locator addMaterialButton;
    private final Locator materialRows;
    private final Locator materialTextInput;
    private final Locator materialSizeInput;
    private final Locator materialQuantityInput;
    private final Locator materialStoreInput;
    private final Locator materialSubmitInput;
    private final Locator editButton;
    private final Locator materialEditTextInput;
    private final Locator materialEditSizeInput;
    private final Locator materialEditQuantityInput;
    private final Locator editMaterialSubmit;

    
    public MaterialsPage(Page page) {
        super(page);
        this.confirmationDialog = new ConfirmationDialog(page);
        this.materialRows = page.getByTestId(MATERIAL_ROW);
        this.addMaterialButton = page.getByTestId(ADD_MATERIAL_BUTTON);
        this.materialTextInput = page.getByTestId(ADD_MATERIAL_TEXT);
        this.materialSizeInput = page.getByTestId(ADD_MATERIAL_SIZE);
        this.materialQuantityInput = page.getByTestId(ADD_MATERIAL_QUANTITY);
        this.materialStoreInput = page.getByTestId(ADD_MATERIAL_STORE);
        this.materialSubmitInput = page.getByTestId(ADD_MATERIAL_SUBMIT);
        this.editButton = page.getByTestId(EDIT_BUTTON);
        this.materialEditTextInput = page.getByTestId(EDIT_TEXT);
        this.materialEditSizeInput = page.getByTestId(EDIT_SIZE);
        this.materialEditQuantityInput = page.getByTestId(EDIT_QUANTITY);
        this.editMaterialSubmit = page.getByTestId(EDIT_CONFIRM);
    }

    public MaterialsPage waitForLoad() {
        waitForVisible(MATERIALS_TABLE);
        waitForNetworkIdle();
        return this;
    }

    public void addMaterial(String text, String size, String quantity,String store) {
        addMaterialButton.click();
        materialTextInput.fill(text);
        materialSizeInput.selectOption(size);
        materialQuantityInput.fill(quantity);
        materialStoreInput.selectOption(store);
        materialSubmitInput.click();
        waitForHidden(ADD_MATERIAL_MODAL);
    }

    public void editMaterial(String text, String size, String quantity) {
        editButton.first().click();
        waitForVisible(EDIT_MODAL);
        materialEditTextInput.fill(text);
        materialEditSizeInput.selectOption(size);
        materialEditQuantityInput.fill(quantity);
        editMaterialSubmit.click();
        waitForHidden(EDIT_MODAL);
        waitForNetworkIdle();
    }

    public void deleteMaterial(String material) {
        Locator itemRow = itemRow(material);
        Locator deleteButton = itemRow.getByTestId(DELETE_BUTTON);

        deleteButton.click();
        confirmationDialog.confirmDelete();
        waitForNetworkIdle();
    }

    private Locator itemRow(String material) {
        return page.getByTestId(MATERIAL_ROW)
                .filter(new Locator.FilterOptions().setHasText(material));
    }

    public void searchByProductName(String productName) {
        fillByTestId(FILTER_PRODUCT, productName);
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    public void filterBySize(String size) {
        selectOptionByTestId(FILTER_SIZE, size);
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    public boolean isAddMaterialModalVisible() { return isVisible(ADD_MATERIAL_MODAL); }

    public boolean isEditModalVisible() { return isVisible(EDIT_MODAL);}

    public List<String> materialsList() { return materialRows.allTextContents();}

    public String currentFilter() {
        return page.getByTestId("filter-size")
                .locator("option:checked")
                .textContent();
    }

    public String currentProduct() {
        return page.getByTestId("filter-product")
                .getAttribute("value");
    }
}