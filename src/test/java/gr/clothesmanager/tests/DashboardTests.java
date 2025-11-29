package gr.clothesmanager.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.pages.DashboardPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static gr.clothesmanager.helpers.AuthenticationHelper.loginAsAdmin;

@UsePlaywright(HeadlessChromeOptions.class)
public class DashboardTests {

    @Test
    @DisplayName("Should display all menu cards on dashboard")
    public void shouldDisplayAllMenuCards(Page page) {
        DashboardPage dashboardPage = loginAsAdmin(page);

        System.out.println("Card users visible: " + page.getByTestId("card-users").isVisible());
        System.out.println("Page URL: " + page.url());
        
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("dashboard-debug.png")));

        dashboardPage.waitForLoad();
        List<String> cardHeadings = dashboardPage.getCardHeadings();
        Assertions.assertThat(cardHeadings).contains("Διαχείριση Χρηστών", "Διαχείριση Ενδυμάτων", "Παραγγελίες", "Διαχείριση Αποθηκών");
    }
}