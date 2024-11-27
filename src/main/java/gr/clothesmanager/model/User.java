package gr.clothesmanager.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;
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

    @Column(name = "comments")
    private String comments;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "enable")
    private Integer enable;

    @OneToMany(mappedBy = "user")
    private Set<Order> orders;

    @Getter(AccessLevel.PRIVATE)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRole> roles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    private Store store;

    public User(Long id, String comments, String lastname, String firstname, String password, String username, Integer enable, Store store) {
        this.id = id;
        this.comments = comments;
        this.lastname = lastname;
        this.firstname = firstname;
        this.password = password;
        this.username = username;
        this.enable = enable;
        this.store = store;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(comments, user.comments) &&
                Objects.equals(lastname, user.lastname) &&
                Objects.equals(firstname, user.firstname) &&
                Objects.equals(password, user.password) &&
                Objects.equals(username, user.username) &&
                Objects.equals(enable, user.enable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, comments, lastname, firstname, password, username, enable);
    }

}




