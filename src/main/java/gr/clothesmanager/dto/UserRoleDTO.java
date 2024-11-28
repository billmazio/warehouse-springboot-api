package gr.clothesmanager.dto;

import gr.clothesmanager.core.enums.AppPermission;
import gr.clothesmanager.model.RolePermission;
import gr.clothesmanager.model.UserRole;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UserRoleDTO {

    private Long id;
    private String name;
    private String tag;
    private List<AppPermission> permissions;
    private List<RolePermission> rolePermissions;
    private Set<Long> userIds;

    public UserRoleDTO(Long id, String name, String tag) {
        this.id = id;
        this.name = name;
        this.tag = tag;
    }

    public static UserRoleDTO fromModel(UserRole model){
        if (model == null) return null;
        return UserRoleDTO.builder()
                .id(model.getId())
                .name(model.getName())
                .tag(model.getTag())
                .build();
    }

    public UserRoleDTO copy() {
        //return new RoleDTO(id, name, tag, new ArrayList<>(permissions));
        return UserRoleDTO.builder()
                .id(id)
                .name(name)
                .tag(tag)
                .permissions(new ArrayList<>(permissions))
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleDTO roleDTO = (UserRoleDTO) o;
        return Objects.equals(tag, roleDTO.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }

    @Override
    public String toString() {
        return "RoleDTO{" +
                "id=" + id +
                ",tag='" + tag +
                ", name='" + name +
                '}';
    }
}
