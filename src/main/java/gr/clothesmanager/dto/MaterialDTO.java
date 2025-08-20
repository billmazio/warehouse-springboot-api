package gr.clothesmanager.dto;

import gr.clothesmanager.model.Material;
import gr.clothesmanager.model.Size;
import gr.clothesmanager.model.Store;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class MaterialDTO {
    private Long id;
    private String text;
    private Integer quantity;
    private Long sizeId;
    private String sizeName;
    private String storeTitle;
    private Long storeId;

    public Material toModel() { return new Material(id, text, quantity, null, null);}

    public static MaterialDTO fromModel(Material material) {
        if (material == null) return null;

        return MaterialDTO.builder()
                .id(material.getId())
                .text(material.getText())
                .quantity(material.getQuantity())
                .sizeId(material.getSize() != null ? material.getSize().getId() : null)
                .sizeName(material.getSize() != null ? material.getSize().getName() : null)
                .storeId(material.getStore() != null ? material.getStore().getId() : null)
                .storeTitle(material.getStore() != null ? material.getStore().getTitle() : null)
                .build();
    }

    @Override
    public String toString() {
        return "MaterialDTO{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", quantity=" + quantity +
                ", sizeId=" + sizeId +
                ", sizeName='" + sizeName + '\'' +
                ", storeId=" + storeId +
                ", storeTitle='" + storeTitle + '\'' +
                '}';
    }
}