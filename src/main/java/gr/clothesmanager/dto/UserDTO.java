package gr.clothesmanager.dto;

import gr.clothesmanager.model.User;
import gr.clothesmanager.model.UserRole;
import gr.clothesmanager.model.Store;
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
    private String username;
    private String password;
    private Integer enable;
    private StoreDTO store; // Assuming a StoreDTO exists
    private Set<UserRole> roles;
    private List<Long> orderIds; // List of Order IDs

    public User toModel() {
        return new User(id, password, username, enable, store != null ? store.toModel() : null);
    }

    public static UserDTO fromModel(User user) {
        if (user == null) return null;

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .enable(user.getEnable())
                .store(user.getStore() != null ? StoreDTO.fromModel(user.getStore()) : null)
                .roles(user.getRoles()) // This expects `user.getRoles()` to return a Set<UserRole>
                .orderIds(user.getOrders() != null ? user.getOrders().stream().map(order -> order.getId()).collect(Collectors.toList()) : null)
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
                ", enable=" + enable +
                ", store=" + store +
                ", roles=" + roles +
                ", orderIds=" + orderIds +
                '}';
    }
}
