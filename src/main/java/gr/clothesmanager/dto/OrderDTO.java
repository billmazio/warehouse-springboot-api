package gr.clothesmanager.dto;

import gr.clothesmanager.model.Material;
import gr.clothesmanager.model.Order;
import gr.clothesmanager.model.Size;
import gr.clothesmanager.model.Store;
import gr.clothesmanager.model.User;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private Date dateOfOrder;
    private Integer quantity;
    private Integer sold;
    private Integer status;
    private Integer stock;
    private Long materialId;
    private Long sizeId;
    private Long storeId;
    private Long userId;

    public static OrderDTO fromModel(Order order) {
        if (order == null) return null;

        return OrderDTO.builder()
                .id(order.getId())
                .dateOfOrder(order.getDateOfOrder())
                .quantity(order.getQuantity())
                .sold(order.getSold())
                .status(order.getStatus())
                .stock(order.getStock())
                .materialId(order.getMaterial() != null ? order.getMaterial().getId() : null)
                .sizeId(order.getSize() != null ? order.getSize().getId() : null)
                .storeId(order.getStore() != null ? order.getStore().getId() : null)
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .build();
    }

    public Order toModel() {
        Order order = new Order();
        order.setDateOfOrder(this.dateOfOrder);
        order.setQuantity(this.quantity);
        order.setSold(this.sold);
        order.setStatus(this.status);
        order.setStock(this.stock);
        order.setMaterial(new Material(this.materialId));
        order.setSize(new Size(this.sizeId));
        order.setStore(new Store(this.storeId));
        order.setUser(new User(this.userId));


        return order;
    }



    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", dateOfOrder=" + dateOfOrder +
                ", userId=" + userId +
                ", storeId=" + storeId +
                ", materialId=" + materialId +
                ", status=" + status +
                ", sold=" + sold +
                ", stock=" + stock +
                ", sizeId=" + sizeId +
                '}';
    }
}
