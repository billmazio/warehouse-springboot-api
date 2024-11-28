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
    private Integer quantity;
    private Date dateOfOrder;
    private Long userId;
    private Long storeId;
    private Long materialId;
    private Integer status;
    private Integer sold;
    private Integer stock;
    private Long sizeId;


    public static OrderDTO fromModel(Order order) {
        if (order == null) return null;

        return OrderDTO.builder()
                .id(order.getId())
                .quantity(order.getQuantity())
                .dateOfOrder(order.getDateOfOrder())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .storeId(order.getStore() != null ? order.getStore().getId() : null)
                .materialId(order.getMaterial() != null ? order.getMaterial().getId() : null)
                .status(order.getStatus())
                .sold(order.getSold())
                .stock(order.getStock())
                .sizeId(order.getSize() != null ? order.getSize().getId() : null)
                .build();
    }


    public Order toModel() {
        Order order = new Order();
        order.setId(this.id);
        order.setQuantity(this.quantity);
        order.setDateOfOrder(this.dateOfOrder);
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
