package gr.clothesmanager;

import com.microsoft.playwright.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;


import java.util.List;

public class DashboardTests extends BasePlaywrightTest{


    @Test
    @DisplayName("Should display all menu cards on dashboard")
    public void shouldShowMenuCards(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);

        List<String> cardHeadings = page.getByTestId("card-name").locator("h3").allInnerTexts();
        Assertions.assertThat(cardHeadings).contains("Διαχείριση Χρηστών", "Διαχείριση Ενδυμάτων", "Παραγγελίες", "Διαχείριση Αποθηκών");
    }
}