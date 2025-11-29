package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.pages.DashboardPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static gr.clothesmanager.helpers.AuthenticationHelper.loginAsAdmin;

@UsePlaywright(HeadlessChromeOptions.class)
public class DashboardTests {
    
    @Test
    @DisplayName("Should display all menu cards on dashboard")
    public void shouldDisplayAllMenuCards(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);
        dashboardPage.waitForLoad();
        List<String> cardHeadings = dashboardPage.getCardHeadings();
        Assertions.assertThat(cardHeadings).contains("Διαχείριση Χρηστών", "Διαχείριση Ενδυμάτων", "Παραγγελίες", "Διαχείριση Αποθηκών");
    }
}