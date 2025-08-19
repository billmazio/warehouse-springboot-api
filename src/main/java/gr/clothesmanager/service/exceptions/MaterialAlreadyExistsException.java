package gr.clothesmanager.service.exceptions;


import gr.clothesmanager.core.exceptions.AppException;

import java.io.Serial;

public class MaterialAlreadyExistsException extends AppException {

    private static final String DEFAULT_CODE = "materialAlreadyExists";

    @Serial
    private static final long serialVersionUID = 1L;

    public MaterialAlreadyExistsException(String message) {
        super(DEFAULT_CODE, message);
    }

    public MaterialAlreadyExistsException(String code, String message) {
        super(code, message);
    }
}
