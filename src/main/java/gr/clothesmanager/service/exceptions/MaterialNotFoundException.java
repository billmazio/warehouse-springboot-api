package gr.clothesmanager.service.exceptions;

import gr.clothesmanager.core.exceptions.AppException;

import java.io.Serial;

public class MaterialNotFoundException extends AppException {

    private static final String DEFAULT_CODE = "materialNotFound";

    @Serial
    private static final long serialVersionUID = 1L;

    public MaterialNotFoundException(String message) {
        super(DEFAULT_CODE, message);
    }

    public MaterialNotFoundException(String code, String message) {
        super(code, message);
    }
}