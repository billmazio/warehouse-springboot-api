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
    private Integer quantity;      // Quantity ordered
    private Integer sold;          // Quantity sold (derived from order quantity)
    private Integer stock;         // Remaining stock (derived from material)
    private Integer status;
    private String materialText;
    private String sizeName;
    private String storeTitle;
    private String userName;

    public static OrderDTO fromModel(Order order) {
        if (order == null) return null;

        return OrderDTO.builder()
                .id(order.getId())
                .dateOfOrder(order.getDateOfOrder())
                .quantity(order.getQuantity())
                .sold(order.getQuantity())  // Sold is the same as the order quantity
                .stock(order.getMaterial().getQuantity())  // Remaining stock from material
                .status(order.getStatus())
                .materialText(order.getMaterial() != null ? order.getMaterial().getText() : null)
                .sizeName(order.getSize() != null ? order.getSize().getName() : null)
                .storeTitle(order.getStore() != null ? order.getStore().getTitle() : null)
                .userName(order.getUser() != null ? order.getUser().getUsername() : null)
                .build();
    }

    public Order toModel() {
        Order order = new Order();
        order.setId(id);
        order.setDateOfOrder(dateOfOrder);
        order.setQuantity(quantity);
        order.setStatus(status);
        // Relationships (material, size, store, user) should be set separately in the service layer
        return order;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", dateOfOrder=" + dateOfOrder +
                ", quantity=" + quantity +
                ", sold=" + sold +
                ", stock=" + stock +
                ", status=" + status +
                ", materialText='" + materialText + '\'' +
                ", sizeName='" + sizeName + '\'' +
                ", storeTitle='" + storeTitle + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
