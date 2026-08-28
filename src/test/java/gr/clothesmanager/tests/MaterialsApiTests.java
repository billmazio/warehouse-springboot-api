package gr.clothesmanager.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.junit.UsePlaywright;
import gr.clothesmanager.builders.MaterialRequestBuilder;
import gr.clothesmanager.config.HeadlessChromeOptions;
import gr.clothesmanager.dto.MaterialDistributionDTO;
import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.pages.ApiAuthenticationPage;
import gr.clothesmanager.pages.MaterialsApiPage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@UsePlaywright(HeadlessChromeOptions.class)
public class MaterialsApiTests {

    private final List<Long> createdMaterialIds = new ArrayList<>();
    private MaterialsApiPage materialsApi;

    @AfterEach
    void deleteCreatedMaterials() {
        if (materialsApi == null || createdMaterialIds.isEmpty()) {
            return;
        }

        for (Long id : createdMaterialIds) {
            materialsApi.delete(id);
        }
        createdMaterialIds.clear();
    }

    @Test
    @DisplayName("Should create and retrieve a material")
    void shouldCreateAndRetrieveMaterial(APIRequestContext request) throws IOException {
        MaterialsApiPage materials = authenticatedMaterials(request);
        MaterialDTO material = MaterialRequestBuilder.aMaterial().build();

        APIResponse created = materials.create(material);

        Assertions.assertThat(created.status()).isEqualTo(201);
        JsonNode createdBody = materials.body(created);
        Long id = createdBody.get("id").asLong();
        createdMaterialIds.add(id);
        Assertions.assertThat(createdBody.get("text").asText()).isEqualTo(material.getText());
        Assertions.assertThat(materials.findById(id).status()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should filter materials by text and store")
    void shouldFilterMaterials(APIRequestContext request) throws IOException {
        MaterialsApiPage materials = authenticatedMaterials(request);
        MaterialDTO material = MaterialRequestBuilder.aMaterial().build();
        Long id = createAndTrack(materials, material);

        JsonNode byText = materials.body(materials.findAllByText(material.getText()));
        JsonNode byStore = materials.body(materials.findByStoreId(material.getStoreId()));

        Assertions.assertThat(byText).anyMatch(item -> item.get("id").asLong() == id);
        Assertions.assertThat(byStore).anyMatch(item -> item.get("id").asLong() == id);
    }

    @Test
    @DisplayName("Should update and delete a material")
    void shouldUpdateAndDeleteMaterial(APIRequestContext request) throws IOException {
        MaterialsApiPage materials = authenticatedMaterials(request);
        Long id = createAndTrack(materials, MaterialRequestBuilder.aMaterial().build());
        MaterialDTO update = MaterialRequestBuilder.aMaterial()
                .withText("API_Updated_" + System.currentTimeMillis())
                .withQuantity(25)
                .withSizeId(2L)
                .build();

        APIResponse updated = materials.update(id, update);
        APIResponse deleted = materials.delete(id);
        createdMaterialIds.remove(id);

        Assertions.assertThat(updated.status()).isEqualTo(200);
        Assertions.assertThat(materials.body(updated).get("quantity").asInt()).isEqualTo(25);
        Assertions.assertThat(deleted.status()).isEqualTo(204);
        Assertions.assertThat(materials.findById(id).status()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should return paginated materials")
    void shouldReturnPaginatedMaterials(APIRequestContext request) throws IOException {
        MaterialsApiPage materials = authenticatedMaterials(request);

        APIResponse response = materials.findPaginated(0, 10);
        JsonNode body = materials.body(response);

        Assertions.assertThat(response.status()).isEqualTo(200);
        Assertions.assertThat(body.get("content").isArray()).isTrue();
        Assertions.assertThat(body.get("pageNumber").asInt()).isZero();
        Assertions.assertThat(body.get("pageSize").asInt()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should distribute material successfully")
    void shouldDistributeMaterial(APIRequestContext request) throws IOException {
        MaterialsApiPage materials = authenticatedMaterials(request);
        MaterialDTO material = MaterialRequestBuilder.aMaterial()
                .withQuantity(10)
                .build();
        Long id = createAndTrack(materials, material);

        APIResponse response = materials.distribute(MaterialDistributionDTO.builder()
                .materialId(id)
                .receiverStoreId(1L)
                .quantity(2)
                .build());

        Assertions.assertThat(response.status()).isEqualTo(200);
        Assertions.assertThat(materials.body(response).get("message").asText())
                .isEqualTo("Material distributed successfully");
    }

    private MaterialsApiPage authenticatedMaterials(APIRequestContext request) throws IOException {
        String token = new ApiAuthenticationPage(request).loginAsAdmin();
        materialsApi = new MaterialsApiPage(request, token);
        return materialsApi;
    }

    private Long createAndTrack(MaterialsApiPage materials, MaterialDTO material) throws IOException {
        JsonNode body = materials.body(materials.create(material));
        Assertions.assertThat(body.get("id")).isNotNull();
        Long id = body.get("id").asLong();
        createdMaterialIds.add(id);
        return id;
    }
}
