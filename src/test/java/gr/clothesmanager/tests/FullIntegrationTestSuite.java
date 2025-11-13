package gr.clothesmanager.tests;


import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Execution(ExecutionMode.SAME_THREAD)
@Suite
@SelectClasses({

    LoginTests.class,
    DashboardTests.class,
    StoreCreateTests.class,
    UserCreateTests.class,
    MaterialCreateTests.class,
    OrderCreateTests.class,
    StoreEditTests.class,
    MaterialEditTests.class,
    OrderEditTests.class,
    MaterialSearchTests.class,
    OrderDeleteTests.class,
    MaterialDeleteTests.class,
    UserDeleteTests.class,
    StoreDeleteTests.class
})
public class FullIntegrationTestSuite {
}