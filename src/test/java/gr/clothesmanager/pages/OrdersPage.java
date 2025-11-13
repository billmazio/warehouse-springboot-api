package gr.clothesmanager.pages;

import com.microsoft.playwright.Page;
import gr.clothesmanager.components.ConfirmationDialog;
import gr.clothesmanager.constants.TestConstants;


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

    public OrdersPage fillQuantity(String quantity) {
        fillByTestId(ORDER_QUANTITY, quantity);
        return this;
    }
    
    public OrdersPage fillDate(String date) {
        fillByTestId(ORDER_DATE, date);
        return this;
    }
    
    public OrdersPage selectStore(String store) {
        selectOptionByTestId(ORDER_STORE, store);
        pause(500);
        return this;
    }
    
    public OrdersPage selectMaterial(String material) {
        selectOptionByTestId(ORDER_MATERIAL, material);
        pause(500);
        return this;
    }
    
    public OrdersPage selectSize(String size) {
        selectOptionByTestId(ORDER_SIZE, size);
        return this;
    }
    
    public OrdersPage selectUser(String user) {
        selectOptionByTestId(ORDER_USER, user);
        return this;
    }
    
    public OrdersPage selectStatus(String status) {
        selectOptionByTestId(ORDER_STATUS, status);
        return this;
    }
    
    public OrdersPage clickCreateOrder() {
        clickByTestId(CREATE_ORDER_BUTTON);
        waitForNetworkIdle();
        pause(TestConstants.WAIT_FOR_LOAD);
        return this;
    }

    public OrdersPage createOrder(String quantity, String date, String store, 
                                   String material, String size, String user, String status) {
        fillQuantity(quantity);
        fillDate(date);
        selectStore(store);
        selectMaterial(material);
        selectSize(size);
        selectUser(user);
        selectStatus(status);
        clickCreateOrder();
        return this;
    }

    public OrdersPage clickEditFirstOrder() {
        page.locator("[data-test='" + EDIT_BUTTON + "']").first().click();
        pause(TestConstants.WAIT_FOR_LOAD);
        return this;
    }
    
    public OrdersPage updateQuantity(String quantity) {
        fillByTestId(ORDER_QUANTITY, quantity);
        return this;
    }
    
    public OrdersPage updateStatus(String status) {
        selectOptionByTestId(ORDER_STATUS, status);
        return this;
    }
    
    public OrdersPage clickUpdateOrder() {
        clickByTestId(UPDATE_ORDER_BUTTON);
        return this;
    }
    
    public OrdersPage editFirstOrder(String quantity, String status) {
        clickEditFirstOrder();
        updateQuantity(quantity);
        updateStatus(status);
        clickUpdateOrder();
        return this;
    }

    public OrdersPage clickDeleteFirstOrder() {
        page.locator("[data-test='" + DELETE_BUTTON + "']").first().click();
        return this;
    }

    public OrdersPage confirmDelete() {
        confirmationDialog.confirmDelete();
        waitForNetworkIdle();
        pause(2000);
        return this;
    }
    
    public OrdersPage deleteFirstOrder() {
        clickDeleteFirstOrder();
        confirmDelete();
        return this;
    }

    public boolean isUpdateOrderButtonVisible() {
        return isVisible(UPDATE_ORDER_BUTTON);
    }
    
    public boolean isConfirmationDialogVisible() {
        return confirmationDialog.isVisible();
    }
    
    public int getOrderCount() {return getCount("[data-test='" + ORDER_ROW + "']");}
    
    public boolean hasOrders() {
        return getOrderCount() > 0;
    }
}