package gr.clothesmanager;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.LoadState;

import java.util.Arrays;

@UsePlaywright(BasePlaywrightTest.MyOptions.class)
public abstract class BasePlaywrightTest {
    public static class MyOptions implements OptionsFactory {
        @Override
        public Options getOptions() {
            return new Options()
                    .setLaunchOptions(
                            new BrowserType.LaunchOptions()
                                    .setHeadless(false)
                                    .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
                    )
                    .setTestIdAttribute("data-test");
        }
    }

    protected void loginAsAdmin(Page page) {
        page.navigate("http://localhost:3000/login");
        page.getByTestId("username-input").fill("admin");
        page.getByTestId("password-input").fill("Admin!1234");
        page.getByTestId("sign-in-button").click();
    }

    protected void waitForDashboard(Page page) {
        page.waitForURL("**/dashboard**", new Page.WaitForURLOptions().setTimeout(10000));
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    protected void navigateToDashboardSection(Page page, String cardTestId, String expectedUrl) {
        page.getByTestId(cardTestId).waitFor(new Locator.WaitForOptions().setTimeout(10000));
        page.getByTestId(cardTestId).click();
        page.waitForURL(expectedUrl, new Page.WaitForURLOptions().setTimeout(10000));
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(1000);
    }
}