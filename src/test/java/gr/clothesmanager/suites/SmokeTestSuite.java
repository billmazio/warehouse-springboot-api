package gr.clothesmanager.suites;


import gr.clothesmanager.tests.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Smoke Test Suite
 * Quick validation of critical functionality
 * Run this before every deployment or major change
 * Execution time: ~40-50 sec
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
    OrderCreateTests.class
})

public class SmokeTestSuite {
}