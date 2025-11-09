package gr.clothesmanager;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DashboardTests extends BasePlaywrightTest {

    @Test
    @DisplayName("Should display all menu cards on dashboard")
    public void shouldShowMenuCards(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);

        List<String> cardHeadings = page.getByTestId("card-name").locator("h3").allInnerTexts();

        assertThat(cardHeadings).contains(
                "Διαχείριση Χρηστών",
                "Διαχείριση Ενδυμάτων",
                "Παραγγελίες",
                "Διαχείριση Αποθηκών"
        );
    }
}