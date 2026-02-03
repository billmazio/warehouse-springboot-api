package gr.clothesmanager.dto;

import gr.clothesmanager.core.enums.Status;
import gr.clothesmanager.model.Order;
import gr.clothesmanager.model.User;
import gr.clothesmanager.model.UserRole;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String password;
    private String username;
    private Status status;
    private Boolean isSystemEntity;
    private StoreDTO store;
    private List<UserRoleDTO> roles;
    private List<Long> orderIds;

    public static UserDTO fromModel(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .status(user.getStatus())
                .isSystemEntity(user.getIsSystemEntity())
                .store(user.getStore() != null ? StoreDTO.fromModel(user.getStore()) : null)
                .roles(user.getRoles() != null ? user.getRoles().stream().map(UserRoleDTO::fromModel).collect(Collectors.toList()) : null)
                .orderIds(user.getOrders() != null ? user.getOrders().stream().map(Order::getId).collect(Collectors.toList()) : null)
                .build();
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", status=" + status +
                ", isSystemEntity=" + isSystemEntity +
                ", store=" + store +
                ", roles=" + roles +
                ", orderIds=" + orderIds +
                '}';
    }
}