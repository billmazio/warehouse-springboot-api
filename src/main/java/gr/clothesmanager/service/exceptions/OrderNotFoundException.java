package gr.clothesmanager.service.exceptions;

import gr.clothesmanager.core.exceptions.AppException;

import java.io.Serial;

public class OrderNotFoundException extends AppException {

    private static final String DEFAULT_CODE = "orderNotFound";

    @Serial
    private static final long serialVersionUID = 1L;

    public OrderNotFoundException(String message) {
        super(DEFAULT_CODE, message);
    }

    public OrderNotFoundException(String code, String message) {
        super(code, message);
    }
}
