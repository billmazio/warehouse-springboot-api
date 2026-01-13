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
    private Set<UserRole> roles;
    private List<Long> orderIds;

    public User toModel() {return new User(id, password, username, status, null , null, roles, null);}

    public static UserDTO fromModel(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .status(user.getStatus())
                .isSystemEntity(user.getIsSystemEntity())
                .store(user.getStore() != null ? StoreDTO.fromModel(user.getStore()) : null)
                .roles(user.getRoles())
                .orderIds(user.getOrders() != null ? user.getOrders().stream().map(Order::getId).collect(Collectors.toList()) : null)
                .build();
    }

    public boolean hasRole(UserRole userRole) {
        return roles != null && roles.contains(userRole.getName());
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