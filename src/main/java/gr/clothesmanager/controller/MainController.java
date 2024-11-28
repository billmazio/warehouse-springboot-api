package gr.clothesmanager.controller;

import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.interfaces.MaterialService;
import gr.clothesmanager.interfaces.OrderService;
import gr.clothesmanager.interfaces.RoleService;
import gr.clothesmanager.interfaces.UserService;
import gr.clothesmanager.model.UserRole;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MainController {

    private final UserService userService;
    private final RoleService roleService;
    private final OrderService orderService;
    private final MaterialService materialService;

    public MainController(UserService userService, RoleService roleService, OrderService orderService, MaterialService materialService) {
        this.userService = userService;
        this.roleService = roleService;
        this.orderService = orderService;
        this.materialService = materialService;
    }

    @GetMapping("/index")
    public ResponseEntity<?> getDashboardData() {
        List<UserDTO> users = userService.findAllUsers().stream()
                .map((UserDTO user) -> UserDTO.fromModel(user.toModel())) // Convert each User to UserDTO
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("userCount", users.size());
        return ResponseEntity.ok(response);
    }


}
