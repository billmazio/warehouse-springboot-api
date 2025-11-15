package gr.clothesmanager.base;

import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.Options;
import com.microsoft.playwright.junit.OptionsFactory;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.pages.*;


import java.util.Arrays;

@UsePlaywright(BaseTest.MyOptions.class)
public abstract class BaseTest {
    
    public static class MyOptions implements OptionsFactory {
        @Override
        public Options getOptions() {
            return new Options().setLaunchOptions(
                            new BrowserType.LaunchOptions()
                                    .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
                    ).setHeadless(false)
                    .setTestIdAttribute("data-test");
        }
    }



    protected LoginPage getLoginPage(Page page) {
        return new LoginPage(page);
    }
    
    protected DashboardPage getDashboardPage(Page page) {
        return new DashboardPage(page);
    }
    
    protected MaterialsPage getMaterialsPage(Page page) {
        return new MaterialsPage(page);
    }
    
    protected OrdersPage getOrdersPage(Page page) {
        return new OrdersPage(page);
    }
    
    protected StoresPage getStoresPage(Page page) {
        return new StoresPage(page);
    }
    
    protected UsersPage getUsersPage(Page page) {
        return new UsersPage(page);
    }

    protected DashboardPage loginAsAdmin(Page page) {
        LoginPage loginPage = getLoginPage(page);
        return loginPage.open()
                        .loginAsAdmin()
                        .waitForLoad();
    }
}