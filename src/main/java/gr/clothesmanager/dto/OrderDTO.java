package gr.clothesmanager.dto;
import gr.clothesmanager.core.enums.OrderStatus;
import gr.clothesmanager.model.Order;
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
    private OrderStatus orderStatus;
    private MaterialDTO material;
    private SizeDTO size;
    private StoreDTO store;
    private UserDTO user;
    private Integer stock;

    public Order toModel() { return new Order(id, dateOfOrder, quantity, orderStatus, null, null, null ,null); }

    public static OrderDTO fromModel(Order order) {
        if (order == null) return null;
        return OrderDTO.builder()
                .id(order.getId())
                .dateOfOrder(order.getDateOfOrder())
                .quantity(order.getQuantity())
                .orderStatus(order.getOrderStatus())
                .material(order.getMaterial() != null ? MaterialDTO.fromModel(order.getMaterial()) : null)
                .size(order.getSize() != null ? SizeDTO.fromModel(order.getSize()) : null)
                .store(order.getStore() != null ? StoreDTO.fromModel(order.getStore()) : null)
                .user(order.getUser() != null ? UserDTO.fromModel(order.getUser()) : null)
                .stock(order.getMaterial().getQuantity())
                .build();
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", dateOfOrder=" + dateOfOrder +
                ", quantity=" + quantity +
                ", orderStatus=" + orderStatus +
                ", material=" + material +
                ", size=" + size +
                ", store=" + store +
                ", user=" + user +
                ", stock=" + stock +
                '}';
    }
}
