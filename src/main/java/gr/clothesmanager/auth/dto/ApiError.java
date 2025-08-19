package gr.clothesmanager.auth.dto;

import java.util.Map;


public record ApiError(String errorCode, String message, Map<String, Object> data) {}
