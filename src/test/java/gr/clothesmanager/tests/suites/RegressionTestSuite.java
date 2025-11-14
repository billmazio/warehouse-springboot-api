package gr.clothesmanager.tests.suites;

import gr.clothesmanager.tests.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Regression Test Suite
 * Comprehensive CRUD testing for all features
 * Run this for thorough testing before releases
 * Execution time: ~5-7 minutes
 * 
 * @author Bill Maziotis
 */
@Execution(ExecutionMode.SAME_THREAD)
@Suite
@SelectClasses({

    StoreCreateTests.class,
    StoreEditTests.class,
    StoreDeleteTests.class,
    UserCreateTests.class,
    UserDeleteTests.class,
    MaterialCreateTests.class,
    MaterialEditTests.class,
    MaterialSearchTests.class,
    MaterialDeleteTests.class,
    OrderCreateTests.class,
    OrderEditTests.class,
    OrderDeleteTests.class
})
public class RegressionTestSuite {
}