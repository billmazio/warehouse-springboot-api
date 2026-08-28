package gr.clothesmanager.pages;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import gr.clothesmanager.constants.TestConstants;

import java.io.IOException;

public class ApiAuthenticationPage {

    private final APIRequestContext request;
    private final ObjectMapper objectMapper;

    public ApiAuthenticationPage(APIRequestContext request) {
        this.request = request;
        this.objectMapper = new ObjectMapper();
    }

    public String loginAsAdmin() throws IOException {
        APIResponse response = request.post(
                TestConstants.API_BASE_URL + "/auth/login",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData("""
                                {"username":"%s","password":"%s"}
                                """.formatted(
                                TestConstants.ADMIN_USERNAME,
                                TestConstants.ADMIN_PASSWORD
                        ))
        );

        if (response.status() != 200) {
            throw new IllegalStateException("API login failed with status " + response.status());
        }

        JsonNode body = objectMapper.readTree(response.text());
        JsonNode token = body.get("token");
        if (token == null || token.asText().isBlank()) {
            throw new IllegalStateException("API login response did not contain a token");
        }
        return token.asText();
    }
}
