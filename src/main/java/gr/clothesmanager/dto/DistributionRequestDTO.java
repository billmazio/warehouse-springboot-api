package gr.clothesmanager.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistributionRequestDTO {
    private Long receiverStoreId;
    private Integer quantity;


}
