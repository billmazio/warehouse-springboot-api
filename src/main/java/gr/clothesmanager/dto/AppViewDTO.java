package gr.clothesmanager.dto;

import gr.clothesmanager.core.enums.AppPermission;
import gr.clothesmanager.model.AppView;
import gr.clothesmanager.model.RolePermission;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppViewDTO {

    private Long id;
    private String name;
    private String description;
    private List<UserRoleDTO> roles;


    public static AppViewDTO fromModel(AppView model) {
        if (model == null) return null;
        AppViewDTO viewDTO = new AppViewDTO(model.getId(), model.getName(), model.getDescription(), null);
        List<UserRoleDTO> roleDTOs = model.getRolePermissions().stream()
                .collect(Collectors.groupingBy(RolePermission::getRole))
                .entrySet().stream()
                .map(entry -> {
                    UserRoleDTO roleDTO = new UserRoleDTO();
                    roleDTO.setId(entry.getKey().getId());
                    roleDTO.setName(entry.getKey().getName());
                    roleDTO.setTag(entry.getKey().getTag());
                    roleDTO.setPermissions(entry.getValue().stream()
                            .map(RolePermission::getPermission)
                            .collect(Collectors.toList()));
                    return roleDTO;
                }).collect(Collectors.toList());

        viewDTO.setRoles(roleDTOs);
        return viewDTO;
    }



    @Override
    public String toString() {
        return "AppViewDTO{" +
               // "roles=" + roles +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}

