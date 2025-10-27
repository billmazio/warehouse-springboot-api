package gr.clothesmanager;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@UsePlaywright
class ClothesManagerApplicationTests {

    @Test
    void contextLoads(Page page) {
        page.navigate("http://localhost:3000/login");
        String title = page.title();

        Assertions.assertTrue(title.contains("Warehouse Management System"));
    }

    @Test
    void shouldSearchByKeyWord(Page page) {
        // Login
        page.navigate("http://localhost:3000/login");
        page.waitForSelector("input[placeholder='Username']");
        page.locator("input[placeholder='Username']").fill("admin");
        page.locator("input[placeholder='Password']").fill("Admin!1234");
        page.locator("button:has-text('Sign In')").click();
        page.waitForURL("**/dashboard**", new Page.WaitForURLOptions().setTimeout(10000));

        // Navigate to dashboard
        page.navigate("http://localhost:3000/dashboard");

        // Wait for specific card to be visible
        page.locator("text=Διαχείριση Ενδυμάτων").waitFor(new Locator.WaitForOptions().setTimeout(10000));

        // Count cards
        int cardCount = page.locator(".card").count();
        System.out.println("Found " + cardCount + " cards");
        Assertions.assertEquals(4, cardCount, "Expected 4 cards on dashboard!");

        // Click materials card
        page.locator("div.card:has-text('Διαχείριση Ενδυμάτων')").click();
        page.waitForURL("**/manage-materials**");
        page.locator("input[placeholder*='Φίλτρο']").fill("Μπλούζα");
        page.waitForTimeout(3000);
        page.locator("button:has-text('Επεξεργασία')").first().click();
    }

}
