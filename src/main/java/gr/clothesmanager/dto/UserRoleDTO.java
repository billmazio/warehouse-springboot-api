package gr.clothesmanager.dto;

import gr.clothesmanager.model.UserRole;
import lombok.*;

import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UserRoleDTO {
    private Long id;
    private String name;
    private String tag;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleDTO that = (UserRoleDTO) o;
        return Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tag);
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
