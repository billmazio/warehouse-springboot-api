package gr.clothesmanager.dto;

import gr.clothesmanager.core.enums.Status;
import gr.clothesmanager.model.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreDTO {
    private Long id;
    private String title;
    private String address;
    private Status status;
    private List<Long> orderIds;
    private List<Long> userIds;
    private List<Long> materialIds;
    private List<Long> materialDescriptionIds;

    public Store toModel() {return new Store(id, title, address, status, null, null,null);}

    public static StoreDTO fromModel(Store store) {
        if (store == null) return null;

        return StoreDTO.builder()
                .id(store.getId())
                .title(store.getTitle())
                .address(store.getAddress())
                .status(store.getStatus())
                .orderIds(store.getOrders() != null ? store.getOrders().stream().map(Order::getId).collect(Collectors.toList()) : null)
                .userIds(store.getUsers() != null ? store.getUsers().stream().map(User::getId).collect(Collectors.toList()) : null)
                .materialIds(store.getMaterials() != null ? store.getMaterials().stream().map(Material::getId).collect(Collectors.toList()) : null)
                .build();
    }

    @Override
    public String toString() {
        return "StoreDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", status=" + status +
                ", orderIds=" + orderIds +
                ", userIds=" + userIds +
                ", materialIds=" + materialIds +
                ", materialDescriptionIds=" + materialDescriptionIds +
                '}';
    }
}