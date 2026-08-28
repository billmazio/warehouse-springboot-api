package gr.clothesmanager.pages;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.dto.MaterialDistributionDTO;
import gr.clothesmanager.dto.MaterialDTO;

import java.io.IOException;

public class MaterialsApiPage {

    private final APIRequestContext request;
    private final String authorizationHeader;
    private final ObjectMapper objectMapper;

    public MaterialsApiPage(APIRequestContext request, String token) {
        this.request = request;
        this.authorizationHeader = "Bearer " + token;
        this.objectMapper = new ObjectMapper();
    }

    public APIResponse create(MaterialDTO material) throws IOException {
        return request.post(
                TestConstants.API_BASE_URL + "/materials",
                jsonOptions(material)
        );
    }

    public APIResponse findById(Long id) {
        return request.get(url("/" + id), authenticatedOptions());
    }

    public APIResponse findAll() {
        return request.get(url(""), authenticatedOptions());
    }

    public APIResponse findAllByText(String text) {
        return request.get(
                url(""),
                authenticatedOptions().setQueryParam("text", text)
        );
    }

    public APIResponse findByStoreId(Long storeId) {
        return request.get(url("/" + storeId + "/materials"), authenticatedOptions());
    }

    public APIResponse findPaginated(Integer page, Integer size) {
        return request.get(
                url("/paginated"),
                authenticatedOptions()
                        .setQueryParam("page", page)
                        .setQueryParam("size", size)
        );
    }

    public APIResponse update(Long id, MaterialDTO material) throws IOException {
        return request.put(
                url("/" + id),
                jsonOptions(material)
        );
    }

    public APIResponse delete(Long id) {
        return request.delete(url("/" + id), authenticatedOptions());
    }

    public APIResponse distribute(MaterialDistributionDTO distribution) throws IOException {
        return request.post(
                url("/distribute"),
                jsonOptions(distribution)
        );
    }

    public JsonNode body(APIResponse response) throws IOException {
        return objectMapper.readTree(response.text());
    }

    private RequestOptions jsonOptions(Object body) throws IOException {
        return authenticatedOptions()
                .setHeader("Content-Type", "application/json")
                .setData(objectMapper.writeValueAsString(body));
    }

    private RequestOptions authenticatedOptions() {
        return RequestOptions.create().setHeader("Authorization", authorizationHeader);
    }

    private String url(String path) {
        return TestConstants.API_BASE_URL + "/materials" + path;
    }
}
