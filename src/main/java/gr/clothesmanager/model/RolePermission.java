package gr.clothesmanager.model;


import gr.clothesmanager.core.enums.AppPermission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "role_permissions")
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private UserRole role;

    @ManyToOne
    @JoinColumn(name = "view_id")
    private AppView view;

    @Enumerated(EnumType.STRING)
    private AppPermission permission;

    @Override
    public String toString() {
        return "RolePermissionView{" +
                ", view=" + view.getName() +
                ", role=" + role.getName() +
                "permission=" + permission +
                '}';
    }
}

