package gr.clothesmanager.model;

import gr.clothesmanager.core.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.sql.Date;

@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfOrder;
    private Integer quantity;

    public Order(Long id,Date dateOfOrder, OrderStatus orderStatus, Material material, Size size, Store store, User user) {
        this.id = id;
        this.dateOfOrder = dateOfOrder;
        this.orderStatus = orderStatus;
        this.material = material;
        this.size = size;
        this.store = store;
        this.user = user;
    }

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", referencedColumnName = "id", nullable = false)
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id", referencedColumnName = "id", nullable = false)
    private Size size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", dateOfOrder=" + dateOfOrder +
                ", quantity=" + quantity +
                ", orderStatus=" + orderStatus +
                ", material=" + material +
                ", size=" + size +
                ", store=" + store +
                ", user=" + user +
                '}';
    }
}


