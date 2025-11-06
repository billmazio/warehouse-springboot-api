package gr.clothesmanager;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        LoginTests.class,
        DashboardTests.class,
        StoreAndUserTests.class,
        MaterialAndOrderTests.class
})

public class ClothesManagerTestSuite {
}