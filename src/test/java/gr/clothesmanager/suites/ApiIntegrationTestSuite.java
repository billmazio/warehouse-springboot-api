package gr.clothesmanager.suites;

import gr.clothesmanager.tests.MaterialsApiTests;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Execution(ExecutionMode.SAME_THREAD)
@Suite
@SelectClasses(MaterialsApiTests.class)
public class ApiIntegrationTestSuite {
}
