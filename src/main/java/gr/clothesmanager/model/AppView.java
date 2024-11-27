package gr.clothesmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "views")
public class AppView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String path;
    private String description;

    @OneToMany(mappedBy = "view", cascade = CascadeType.ALL)
    private Set<RolePermission> rolePermissions;

   /* public Set<RolePermission> getAllRolePermissions() {
        if (rolePermissions == null) rolePermissions = new HashSet<>();
        return Collections.unmodifiableSet(rolePermissions);
    }*/

    @Override
    public String toString() {
        return "AppView{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", description='" + description + '\'' +
                //", rolePermissions=" + rolePermissions +
                '}';
    }
}

