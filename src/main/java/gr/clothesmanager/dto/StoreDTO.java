package gr.clothesmanager.dto;

import gr.clothesmanager.model.Order;
import gr.clothesmanager.model.Store;
import gr.clothesmanager.model.User;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class StoreDTO {

    private Long id;
    private String title;
    private String address;
    private Integer enable;
    private List<Long> orderIds;  // Simplified list of order IDs
    private List<Long> userIds;   // Simplified list of user IDs

    // **Mapping Methods**

    // Convert `StoreDTO` to `Store` (Model)
    public Store toModel() {
        return new Store(id,title,address,enable,null,null);

    }

    // Populate an existing `Store` model from `StoreDTO`
    public void copyToModel(Store store) {
        store.setTitle(title);
        store.setAddress(address);
        store.setEnable(enable);
    }

    // Convert `Store` (Model) to Basic `StoreDTO`
    public static StoreDTO fromModelBasic(Store store) {
        if (store == null) return null;

        return StoreDTO.builder()
                .id(store.getId())
                .title(store.getTitle())
                .address(store.getAddress())
                .enable(store.getEnable())
                .build();
    }

    // Convert `Store` (Model) to Full `StoreDTO`
    public static StoreDTO fromModel(Store store) {
        if (store == null) return null;

        return StoreDTO.builder()
                .id(store.getId())
                .title(store.getTitle())
                .address(store.getAddress())
                .enable(store.getEnable())
                .orderIds(store.getOrders() != null ? store.getOrders().stream().map(Order::getId).collect(Collectors.toList()) : null)
                .userIds(store.getUsers() != null ? store.getUsers().stream().map(User::getId).collect(Collectors.toList()) : null)
                .build();
    }

    // Copy an instance of `StoreDTO`
    public StoreDTO copy() {
        return StoreDTO.builder()
                .id(this.id)
                .title(this.title)
                .address(this.address)
                .enable(this.enable)
                .orderIds(this.orderIds != null ? List.copyOf(this.orderIds) : null)
                .userIds(this.userIds != null ? List.copyOf(this.userIds) : null)
                .build();
    }

    // Utility: Check if a `StoreDTO` list contains a specific `StoreDTO` by title
    public static boolean customListContains(List<StoreDTO> list, StoreDTO dto) {
        if (list == null || list.isEmpty() || dto == null) return false;
        return list.stream().anyMatch(storeDTO ->
                storeDTO.getTitle().equals(dto.getTitle()));
    }

    @Override
    public String toString() {
        return "StoreDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", enable=" + enable +
                ", orderIds=" + orderIds +
                ", userIds=" + userIds +
                '}';
    }
}
