package gr.clothesmanager.pages;

import com.microsoft.playwright.Page;
import gr.clothesmanager.components.ConfirmationDialog;
import gr.clothesmanager.constants.TestConstants;

/**
 * Page Object for Orders management page
 * Handles CRUD operations for orders
 * Orders depend on materials and are associated with stores and users
 *
 * @author Bill Maziotis
 */
public class OrdersPage extends BasePage {

    private static final String ORDERS_TABLE = "orders-table";
    private static final String ORDER_ROW = "order-row";

    private static final String ORDER_QUANTITY = "order-quantity";
    private static final String ORDER_DATE = "order-date";
    private static final String ORDER_STORE = "order-store";
    private static final String ORDER_MATERIAL = "order-material";
    private static final String ORDER_SIZE = "order-size";
    private static final String ORDER_USER = "order-user";
    private static final String ORDER_STATUS = "order-status";
    private static final String CREATE_ORDER_BUTTON = "create-order-button";

    private static final String EDIT_BUTTON = "edit-button";
    private static final String UPDATE_ORDER_BUTTON = "update-order-button";

    private static final String DELETE_BUTTON = "delete-button";
    
    private final ConfirmationDialog confirmationDialog;
    
    public OrdersPage(Page page) {
        super(page);
        this.confirmationDialog = new ConfirmationDialog(page);
    }

    public OrdersPage waitForLoad() {
        waitForVisible(ORDERS_TABLE);
        waitForNetworkIdle();
        return this;
    }

    private void fillOrderQuantity(String quantity) {
        fillByTestId(ORDER_QUANTITY, quantity);
    }

    private void fillOrderDate(String date) {
        fillByTestId(ORDER_DATE, date);
    }

    private void selectOrderStore(String store) {
        selectOptionByTestId(ORDER_STORE, store);
        pause(500);
    }

    private void selectOrderMaterial(String material) {
        selectOptionByTestId(ORDER_MATERIAL, material);
        pause(500); // Wait for size dropdown to populate
    }

    private void selectOrderSize(String size) {
        selectOptionByTestId(ORDER_SIZE, size);
    }

    private void selectOrderUser(String user) {
        selectOptionByTestId(ORDER_USER, user);
    }

    private void selectOrderStatus(String status) {
        selectOptionByTestId(ORDER_STATUS, status);
    }

    private void clickCreateOrderButton() {
        clickByTestId(CREATE_ORDER_BUTTON);
        waitForNetworkIdle();
        pause(TestConstants.WAIT_FOR_LOAD);
    }

    /**
     * Create a new order with all required fields
     * @param quantity Order quantity
     * @param date Order date (format: yyyy-MM-dd)
     * @param store Store name
     * @param material Material name
     * @param size Material size
     * @param user Username
     * @param status Order status
     */
    public void createOrder(String quantity, String date, String store,
                            String material, String size, String user, String status) {
        fillOrderQuantity(quantity);
        fillOrderDate(date);
        selectOrderStore(store);
        selectOrderMaterial(material);
        selectOrderSize(size);
        selectOrderUser(user);
        selectOrderStatus(status);
        clickCreateOrderButton();
    }

    private void clickEditFirstOrder() {
        page.locator("[data-test='" + EDIT_BUTTON + "']").first().click();
        pause(TestConstants.WAIT_FOR_LOAD);
    }

    private void updateOrderQuantity(String quantity) {
        fillByTestId(ORDER_QUANTITY, quantity);
    }

    private void updateOrderStatus(String status) {
        selectOptionByTestId(ORDER_STATUS, status);
    }

    private void clickUpdateOrderButton() {
        clickByTestId(UPDATE_ORDER_BUTTON);
    }

    /**
     * Edits the first order in the list
     * @param quantity New quantity
     * @param status New status
     */
    public void editFirstOrder(String quantity, String status) {
        clickEditFirstOrder();
        updateOrderQuantity(quantity);
        updateOrderStatus(status);
        clickUpdateOrderButton();
    }

    /**
     * Deletes the first order in the list
     * Waits for deletion to complete
     */
    public void deleteFirstOrder() {
        int countBeforeDelete = getOrderCount();

        page.locator("[data-test='" + DELETE_BUTTON + "']").first().click();
        confirmationDialog.confirmDelete();
        waitForNetworkIdle();

        page.waitForCondition(() -> getOrderCount() < countBeforeDelete);
    }

    public boolean isUpdateOrderButtonVisible() {
        return isVisible(UPDATE_ORDER_BUTTON);
    }

    /**
     * Gets the current count of orders displayed
     * @return Number of order rows
     */
    public int getOrderCount() {
        return getCount("[data-test='" + ORDER_ROW + "']");
    }

    /**
     * Verifies that an order exists for the given material
     * @param material The material name to search for
     * @return true if order exists, false otherwise
     */
    public boolean orderExists(String material) {
        return getCount("[data-test='" + ORDER_ROW + "']" +
                ":has-text('" + material + "')") > 0;
    }
}