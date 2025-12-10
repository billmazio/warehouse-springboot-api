package gr.clothesmanager.suites;

import gr.clothesmanager.tests.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Regression Test Suite
 * Comprehensive CRUD testing for all features
 * Run this for thorough testing before releases
 * Execution time: ~50-60 sec
 * 
 * @author Bill Maziotis
 */
@Execution(ExecutionMode.SAME_THREAD)
@Suite
@SelectClasses({
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

public class RegressionTestSuite {
}