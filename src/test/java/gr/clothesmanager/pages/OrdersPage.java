package gr.clothesmanager.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import gr.clothesmanager.components.ConfirmationDialog;
import gr.clothesmanager.constants.TestConstants;

import java.util.List;


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
    private final Locator orderRows;
    private final Locator orderQuantityInput;
    private final Locator orderDateInput;
    private final Locator selectOrderStore;
    private final Locator selectOrderMaterial;
    private final Locator selectOrderSize;
    private final Locator selectOrderUser;
    private final Locator selectOrderStatus;
    private final Locator createOrderButton;
    private final Locator editOrderButton;
    private final Locator editQuantityInput;
    private final Locator editStatus;
    private final Locator updateOrderButton;
    
    public OrdersPage(Page page) {
        super(page);
        this.confirmationDialog = new ConfirmationDialog(page);

        this.orderQuantityInput = page.getByTestId(ORDER_QUANTITY);
        this.orderRows = page.getByTestId(ORDER_ROW);
        this.orderDateInput = page.getByTestId(ORDER_DATE);
        this.selectOrderStore = page.getByTestId(ORDER_STORE);
        this.selectOrderMaterial = page.getByTestId(ORDER_MATERIAL);
        this.selectOrderSize = page.getByTestId(ORDER_SIZE);
        this.selectOrderUser = page.getByTestId(ORDER_USER);
        this.selectOrderStatus = page.getByTestId(ORDER_STATUS);
        this.createOrderButton = page.getByTestId(CREATE_ORDER_BUTTON);
        this.editOrderButton = page.getByTestId(EDIT_BUTTON);
        this.editQuantityInput = page.getByTestId(ORDER_QUANTITY);
        this.editStatus = page.getByTestId(ORDER_STATUS);
        this.updateOrderButton = page.getByTestId(UPDATE_ORDER_BUTTON);
    }

    public OrdersPage waitForLoad() {
        waitForVisible(ORDERS_TABLE);
        waitForNetworkIdle();
        return this;
    }

    public void createOrder(String quantity, String date, String store, String material, String size, String user,String status) {
        orderQuantityInput.fill(quantity);
        orderDateInput.fill(date);
        selectOrderStore.selectOption(store);
        selectOrderMaterial.selectOption(material);
        selectOrderSize.selectOption(size);
        selectOrderUser.selectOption(user);
        selectOrderStatus.selectOption(status);
        createOrderButton.click();
    }

    public void editOrder(String quantity, String status) {
        editOrderButton.first().click();
        editQuantityInput.fill(quantity);
        editStatus.selectOption(status);
        updateOrderButton.click();
    }

    public void deleteOrder(String order) {
        Locator itemRow = itemRow(order);
        Locator deleteButton = itemRow.getByTestId(DELETE_BUTTON);

        deleteButton.click();
        confirmationDialog.confirmDelete();
        waitForNetworkIdle();
    }

    private Locator itemRow(String order) {
        return page.getByTestId(ORDER_ROW)
                .filter(new Locator.FilterOptions().setHasText(order));
    }

    public Locator orderExists(String material) {
        return page.getByTestId(ORDER_ROW)
                .filter(new Locator.FilterOptions().setHasText(material));
    }

    public List<String> ordersList() { return orderRows.allTextContents(); }
}