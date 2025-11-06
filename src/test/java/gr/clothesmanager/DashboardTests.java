package gr.clothesmanager;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DashboardTests extends BasePlaywrightTest {

    @Test
    @DisplayName("Should display all menu cards on dashboard")
    public void shouldShowMenuCards(Page page) {
        loginAsAdmin(page);
        waitForDashboard(page);

        List<String> expectedCards = List.of(
                "card-users",
                "card-materials",
                "card-orders",
                "card-stores"
        );

        expectedCards.forEach(testId -> {
            assertThat(page.getByTestId(testId)).isVisible();
        });
    }
}