package gr.clothesmanager.dto;

import gr.clothesmanager.model.Material;
import gr.clothesmanager.model.Size;
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


    public static MaterialDTO fromModel(Material material) {
        if (material == null) return null;

        return MaterialDTO.builder()
                .id(material.getId())
                .text(material.getText())
                .quantity(material.getQuantity())
                .sizeId(material.getSize() != null ? material.getSize().getId() : null)
                .build();
    }

    public Material toModel() {
        Material material = new Material();
        material.setId(this.id);
        material.setText(this.text);
        material.setQuantity(this.quantity);
        return material;
    }

    @Override
    public String toString() {
        return "MaterialDTO{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", quantity=" + quantity +
                ", sizeId=" + sizeId +
                '}';
    }
}
