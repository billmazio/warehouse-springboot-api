package gr.clothesmanager.service.exceptions;

import gr.clothesmanager.core.exceptions.AppException;

import java.io.Serial;

public class StoreNotFoundException extends AppException {

    private static final String DEFAULT_CODE = "storeNotFound";

    @Serial
    private static final long serialVersionUID = 1L;

    public StoreNotFoundException(String message) {
        super(DEFAULT_CODE, message);
    }

    public StoreNotFoundException(String code, String message) {
        super(code, message);
    }

}
