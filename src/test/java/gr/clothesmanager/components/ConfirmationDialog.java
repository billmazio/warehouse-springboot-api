package gr.clothesmanager.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class ConfirmationDialog {
    private final Page page;
    
    private static final String CONFIRMATION_DIALOG = "confirmation-dialog";
    private static final String CONFIRM_DELETE_BUTTON = "confirm-delete";
    
    public ConfirmationDialog(Page page) {
        this.page = page;
    }
    
    public boolean isVisible() {
        return page.getByTestId(CONFIRMATION_DIALOG).isVisible();
    }

    public void confirmDelete() {
        page.getByTestId(CONFIRM_DELETE_BUTTON).click();
        waitForDialogToClose();
    }
    
    public void waitForDialogToClose() {
        page.getByTestId(CONFIRMATION_DIALOG).waitFor(
            new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN)
        );
    }
}