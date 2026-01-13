package gr.clothesmanager.common;

import gr.clothesmanager.service.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 – JSON body validation (@Valid @RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String,String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> fieldErrors.put(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of(
                "error", true,
                "message", "Validation error.",
                "errors", fieldErrors
        ));
    }

    // 400 – Binding/validation for non-body inputs (@ModelAttribute, query params, etc.)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String,Object>> handleBind(BindException ex) {
        Map<String,String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> fieldErrors.put(fe.getField(), fe.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of(
                "error", true,
                "message", "Validation error.",
                "errors", fieldErrors
        ));
    }

    // 400 – Insufficient stock exception
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String,Object>> handleInsufficientStock(InsufficientStockException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", true,
                "code", "INSUFFICIENT_STOCK",
                "message", "Insufficient stock."
        ));
    }

    // 400 – Insufficient quantity exception (for material distribution)
    @ExceptionHandler(InsufficientQuantityException.class)
    public ResponseEntity<Map<String,Object>> handleInsufficientQuantity(InsufficientQuantityException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", true,
                "code", "INSUFFICIENT_QUANTITY",
                "message", "Insufficient quantity."
        ));
    }

    // 409 – Size already exists
    @ExceptionHandler(SizeAlreadyExistsException.class)
    public ResponseEntity<Map<String,Object>> handleSizeAlreadyExists(SizeAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", true,
                "code", "SIZE_ALREADY_EXISTS",
                "message", "Size already exists with this name."
        ));
    }

    // 409 – User already exists
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String,Object>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", true,
                "code", "USER_ALREADY_EXISTS",
                "message", "Username already exists. Please choose a different one."
        ));
    }

    // 409 – Store already exists
    @ExceptionHandler(StoreAlreadyExistsException.class)
    public ResponseEntity<Map<String,Object>> handleStoreAlreadyExists(StoreAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", true,
                "code", "STORE_ALREADY_EXISTS",
                "message", "Store already exists with this title."
        ));
    }

    // 409 – Material already exists
    @ExceptionHandler(MaterialAlreadyExistsException.class)
    public ResponseEntity<Map<String,Object>> handleMaterialAlreadyExists(MaterialAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", true,
                "code", "MATERIAL_ALREADY_EXISTS",
                "message", "Material with same description and size already exists in this store."
        ));
    }

    // 404 – Material not found
    @ExceptionHandler(MaterialNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleMaterialNotFound(MaterialNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", true,
                "code", "MATERIAL_NOT_FOUND",
                "message", "Material not found."
        ));
    }

    // 404 – Order not found
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleOrderNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", true,
                "code", "ORDER_NOT_FOUND",
                "message", "Order not found."
        ));
    }

    // 404 – User not found
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", true,
                "code", "USER_NOT_FOUND",
                "message", "User not found."
        ));
    }

    // 404 – Size not found
    @ExceptionHandler(SizeNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleSizeNotFound(SizeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", true,
                "code", "SIZE_NOT_FOUND",
                "message", "Size not found."
        ));
    }

    // 404 – Store not found
    @ExceptionHandler(StoreNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleStoreNotFound(StoreNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", true,
                "code", "STORE_NOT_FOUND",
                "message", "Store not found."
        ));
    }

    // 409 – Material has orders (cannot delete)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalState(IllegalStateException ex) {
        if ("MATERIAL_HAS_ORDERS".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", true,
                    "code", "MATERIAL_HAS_ORDERS",
                    "message", "Material has associated orders and cannot be deleted."
            ));
        } else if ("STORE_DELETE_HAS_MATERIALS".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", true,
                    "code", "STORE_DELETE_HAS_MATERIALS",
                    "message", "Store has associated materials and cannot be deleted."
            ));
        } else if ("STORE_DELETE_HAS_ORDERS".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", true,
                    "code", "STORE_DELETE_HAS_ORDERS",
                    "message", "Store has associated orders and cannot be deleted."
            ));
        } else if ("STORE_DELETE_HAS_USERS".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", true,
                    "code", "STORE_DELETE_HAS_USERS",
                    "message", "Store has associated users and cannot be deleted."
            ));
        } else if ("CANNOT_DISABLE_OWN_ACCOUNT".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", true,
                    "code", "CANNOT_DISABLE_OWN_ACCOUNT",
                    "message", "You cannot disable your own account."
            ));
        } else if ("CANNOT_MODIFY_SUPER_ADMIN".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", true,
                    "code", "CANNOT_MODIFY_SUPER_ADMIN",
                    "message", "You cannot modify a SUPER_ADMIN user."
            ));
        } else if ("CANNOT_MODIFY_OTHER_STORE_USERS".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", true,
                    "code", "CANNOT_MODIFY_OTHER_STORE_USERS",
                    "message", "You can only modify users from your own store."
            ));
        } else if ("CANNOT_DELETE_SELF".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", true,
                    "code", "CANNOT_DELETE_SELF",
                    "message", "You cannot delete your own account."
            ));
        } else if ("CANNOT_DELETE_SUPER_ADMIN".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", true,
                    "code", "CANNOT_DELETE_SUPER_ADMIN",
                    "message", "You cannot delete a SUPER_ADMIN user."
            ));
        } else if ("SYSTEM_USER_PROTECTED".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", true,
                    "code", "SYSTEM_USER_PROTECTED",
                    "message", "This user is system protected and cannot be deleted."
            ));
        } else if ("SYSTEM_STORE_PROTECTED".equals(ex.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", true,
                    "code", "SYSTEM_STORE_PROTECTED",
                    "message", "This store is system protected and cannot be deleted."
            ));
        } else if (ex.getMessage().contains("related orders") || ex.getMessage().contains("cannot be deleted")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", true,
                    "code", "INTEGRITY_VIOLATION",
                    "message", "Cannot be deleted due to related data."
            ));
        }
        // For other IllegalStateExceptions, return a generic conflict message
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", true,
                "code", "CONFLICT_GENERIC",
                "message", "Related data conflict."
        ));
    }

    // 400 – Runtime exceptions with specific error codes
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,Object>> handleRuntimeException(RuntimeException ex) {
        String errorCode;
        String message;

        switch (ex.getMessage()) {
            case "STORE_NOT_FOUND":
                errorCode = "STORE_NOT_FOUND";
                message = "Store not found.";
                break;
            case "SIZE_NOT_FOUND":
                errorCode = "SIZE_NOT_FOUND";
                message = "Size not found.";
                break;
            case "USER_NOT_FOUND":
                errorCode = "USER_NOT_FOUND";
                message = "User not found.";
                break;
            default:
                if (ex.getMessage().contains("Material not found")) {
                    errorCode = "MATERIAL_NOT_FOUND";
                    message = "Material not found for the specified size/store.";
                } else {
                    // Let the general Exception handler deal with other RuntimeExceptions
                    throw ex;
                }
                break;
        }

        return ResponseEntity.badRequest().body(Map.of(
                "error", true,
                "code", errorCode,
                "message", message
        ));
    }

    // 409 – Data integrity violation (for deletes with related data)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", true,
                "code", "INTEGRITY_VIOLATION",
                "message", "Cannot be deleted due to related data in the store."
        ));
    }

    // 400 – other bad requests
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", true,
                "message", ex.getMessage()
        ));
    }

    // 403 – access denied
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String,Object>> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "error", true,
                "code", "ACCESS_DENIED",
                "message", "You do not have permission for this action."
        ));
    }

    // 500 – fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", true,
                "message", "Something went wrong. Please try again."
        ));
    }
}