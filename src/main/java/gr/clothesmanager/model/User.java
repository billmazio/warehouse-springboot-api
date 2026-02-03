package gr.clothesmanager.model;

import gr.clothesmanager.core.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "is_system_entity", nullable = false)
    private Boolean isSystemEntity;

    @OneToMany(mappedBy = "user")
    private Set<Order> orders;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRole> roles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    private Store store;

    public User(String password, String username, Status status, Boolean isSystemEntity, Set<Order> orders, Set<UserRole> roles, Store store) {
        this.password = password;
        this.username = username;
        this.status = status;
        this.isSystemEntity = isSystemEntity;
        this.orders = orders;
        this.roles = roles;
        this.store = store;
    }
}