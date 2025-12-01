package gr.clothesmanager.constants;

public class TestConstants {

    public static final String BASE_URL = "http://localhost:3000";
    public static final String LOGIN_URL = BASE_URL + "/login";
    public static final String DASHBOARD_URL = BASE_URL + "/dashboard";

    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "Admin!1234";

    public static final int DEFAULT_TIMEOUT = 10000;
    public static final int SHORT_TIMEOUT = 5000;
    public static final int WAIT_FOR_LOAD = 1000;

    public static final String STORE_KENTRIKA = "ΚΕΝΤΡΙΚΑ";
    public static final String STORE_DYTIKA = "ΔΥΤΙΚΑ";

    public static final String SIZE_EXTRA_SMALL = "EXTRA SMALL";
    public static final String SIZE_SMALL = "SMALL";
    public static final String SIZE_MEDIUM = "MEDIUM";
    public static final String SIZE_LARGE = "LARGE";
    public static final String SIZE_EXTRA_LARGE = "EXTRA LARGE";

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";

    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_LOCAL_ADMIN = "LOCAL_ADMIN";

    // ========== UNIQUE NAME GENERATORS ==========

    /**
     * Generate unique store name for testing
     * @param baseName Base store name (e.g., "ΔΥΤΙΚΑ")
     * @return Unique store name with timestamp
     */
    public static String uniqueStoreName(String baseName) {
        return baseName + "_" + System.currentTimeMillis();
    }

    /**
     * Generate unique material name for testing
     * @param baseName Base material name (e.g., "Μπλούζα")
     * @return Unique material name with timestamp
     */
    public static String uniqueMaterialName(String baseName) {
        return baseName + "_" + System.currentTimeMillis();
    }

    /**
     * Generate unique username for testing
     * @param baseName Base username (e.g., "testuser")
     * @return Unique username with timestamp
     */
    public static String uniqueUserName(String baseName) {
        return baseName + System.currentTimeMillis();
    }
}