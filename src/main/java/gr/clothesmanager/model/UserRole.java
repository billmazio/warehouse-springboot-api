package gr.clothesmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gr.clothesmanager.dto.UserRoleDTO;
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
@Table(name = "roles")
public class UserRole extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String tag;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<User> users;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleDTO roleDTO = (UserRoleDTO) o;
        return Objects.equals(tag, roleDTO.getTag());
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "tag='" + tag + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
