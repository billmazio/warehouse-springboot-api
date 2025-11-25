package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
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
        page.getByTestId(EDIT_BUTTON).first().click();
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

    public void editFirstOrder(String quantity, String status) {
        clickEditFirstOrder();
        updateOrderQuantity(quantity);
        updateOrderStatus(status);
        clickUpdateOrderButton();
    }

    public void deleteFirstOrder() {
        int countBeforeDelete = getOrderCount();

        page.getByTestId(DELETE_BUTTON).first().click();
        confirmationDialog.confirmDelete();
        waitForNetworkIdle();

        page.waitForCondition(() -> getOrderCount() < countBeforeDelete);
    }

    public boolean isUpdateOrderButtonVisible() {
        return isVisible(UPDATE_ORDER_BUTTON);
    }

    public int getOrderCount() {return getCountByTestId(ORDER_ROW);}

    public boolean orderExists(String material) {
        return page.getByTestId(ORDER_ROW)
                .filter(new Locator.FilterOptions().setHasText(material))
                .count() > 0;
    }
}