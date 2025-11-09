package gr.clothesmanager;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Execution(ExecutionMode.SAME_THREAD)
@Suite
@SelectClasses({
        LoginTests.class,
        DashboardTests.class,
        MaterialAndOrderTests.class,
        StoreAndUserTests.class
})

public class ClothesManagerTestSuite {
}