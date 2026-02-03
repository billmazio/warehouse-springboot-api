package gr.clothesmanager.model;

import gr.clothesmanager.core.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private Set<Order> orders;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private Set<User> users;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private Set<Material> materials;

    public Store(Long id, String title, String address, Status status, Set<Order> orders, Set<User> users, Set<Material> materials) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.status = status;
        this.orders = orders;
        this.users = users;
        this.materials = materials;
    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", status=" + status +
                '}';
    }
}