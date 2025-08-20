package gr.clothesmanager.common;

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
                "message", "Σφάλμα επικύρωσης.",
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
                "message", "Σφάλμα επικύρωσης.",
                "errors", fieldErrors
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
                "message", "Δεν έχετε δικαίωμα για αυτή την ενέργεια."
        ));
    }

    // 500 – fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", true,
                "message", "Κάτι πήγε στραβά. Δοκιμάστε ξανά."
        ));
    }
}

