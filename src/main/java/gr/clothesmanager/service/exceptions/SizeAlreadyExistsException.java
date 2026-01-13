package gr.clothesmanager.service.exceptions;

import gr.clothesmanager.core.exceptions.AppException;

import java.io.Serial;

public class SizeAlreadyExistsException extends AppException {

    private static final String DEFAULT_CODE = "sizeAlreadyExists";

    @Serial
    private static final long serialVersionUID = 1L;

    public SizeAlreadyExistsException(String message) {
        super(DEFAULT_CODE, message);
    }

    public SizeAlreadyExistsException(String code, String message) {
        super(code, message);
    }
}
