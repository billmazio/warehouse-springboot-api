package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.base.BaseTest;
import gr.clothesmanager.pages.DashboardPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

@UsePlaywright
@DisplayName("Dashboard Functionality Tests")
public class DashboardTests extends BaseTest {
    
    @Test
    @DisplayName("Should display all menu cards on dashboard")
    public void shouldDisplayAllMenuCards(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        
        List<String> cardHeadings = dashboardPage.getCardHeadings();
        Assertions.assertThat(cardHeadings).contains("Διαχείριση Χρηστών", "Διαχείριση Ενδυμάτων", "Παραγγελίες", "Διαχείριση Αποθηκών");
    }
}