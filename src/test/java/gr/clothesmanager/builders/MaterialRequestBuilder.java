package gr.clothesmanager.builders;

import gr.clothesmanager.constants.TestConstants;
import gr.clothesmanager.dto.MaterialDTO;

public class MaterialRequestBuilder {

    private String text = TestConstants.uniqueMaterialName("API_Material");
    private Integer quantity = 10;
    private Long sizeId = 3L;
    private Long storeId = 1L;

    private MaterialRequestBuilder() {
    }

    public static MaterialRequestBuilder aMaterial() {
        return new MaterialRequestBuilder();
    }

    public MaterialRequestBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public MaterialRequestBuilder withQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }

    public MaterialRequestBuilder withSizeId(Long sizeId) {
        this.sizeId = sizeId;
        return this;
    }

    public MaterialRequestBuilder withStoreId(Long storeId) {
        this.storeId = storeId;
        return this;
    }

    public MaterialDTO build() {
        return MaterialDTO.builder()
                .text(text)
                .quantity(quantity)
                .sizeId(sizeId)
                .storeId(storeId)
                .build();
    }
}
