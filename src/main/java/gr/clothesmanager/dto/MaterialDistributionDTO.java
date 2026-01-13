package gr.clothesmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class MaterialDistributionDTO {
    @NotNull(message = "Material ID is required")
    private Long materialId;
    
    @NotNull(message = "Receiver store ID is required")
    private Long receiverStoreId;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Override
    public String toString() {
        return "MaterialDistributionDTO{" +
                "materialId=" + materialId +
                ", receiverStoreId=" + receiverStoreId +
                ", quantity=" + quantity +
                '}';
    }
}