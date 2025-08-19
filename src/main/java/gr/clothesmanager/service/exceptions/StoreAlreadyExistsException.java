package gr.clothesmanager.service.exceptions;

import gr.clothesmanager.core.exceptions.AppException;

import java.io.Serial;

    public class StoreAlreadyExistsException extends AppException {

        private static final String DEFAULT_CODE = "storeAlreadyExists";

        @Serial
        private static final long serialVersionUID = 1L;

        public StoreAlreadyExistsException(String message) {
            super(DEFAULT_CODE, message);
        }

        public StoreAlreadyExistsException(String code, String message) {
            super(code, message);
        }
    }


