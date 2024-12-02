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
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "address")
    private String address;

    @Column(name = "enable")
    private Integer enable;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private Set<Order> orders;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private Set<User> users;


    public Store(String title, String address, Integer enable) {
        this.title = title;
        this.address = address;
        this.enable = enable;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(id, store.id) &&
                Objects.equals(title, store.title) &&
                Objects.equals(address, store.address) &&
                Objects.equals(enable, store.enable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, address, enable);
    }


    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", enable=" + enable +
                ", orders=" + orders +
                ", users=" + users +
                '}';
    }
}
