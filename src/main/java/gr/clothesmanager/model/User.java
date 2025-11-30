package gr.clothesmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    //private Integer enable;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "is_system_entity", nullable = false)
    private Boolean isSystemEntity = false;

    @OneToMany(mappedBy = "user")
    private Set<Order> orders;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRole> roles;

    @ManyToOne(fetch = FetchType.EAGER)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id != null && id.equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}