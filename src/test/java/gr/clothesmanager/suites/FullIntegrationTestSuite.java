package gr.clothesmanager.suites;


import gr.clothesmanager.tests.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Full Integration Test Suite
 * Complete test coverage including login, dashboard, and all CRUD operations
 * Respects database dependencies (create stores before users, etc.)
 * Run this for complete validation
 * Execution time: ~1-2 minutes
 *
 * @author Bill Maziotis
 */
@Execution(ExecutionMode.SAME_THREAD)
@Suite
@SelectClasses({
        LoginTests.class,
        DashboardTests.class,
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