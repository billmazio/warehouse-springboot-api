package gr.clothesmanager;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ClothesManagerApplicationTests {

    @Test
    void contextLoads() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        Page page = browser.newPage();

        page.navigate("http://localhost:3000/login");
        String title = page.title();

        Assertions.assertTrue(title.contains("Warehouse Management System"));

        browser.close();
        playwright.close();
    }

}
