package gr.clothesmanager.controller;

import gr.clothesmanager.interfaces.RoleService;
import gr.clothesmanager.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
//
//    @GetMapping
//    public ResponseEntity<List<String>> findAllRoles() {
//        return ResponseEntity.ok(roleService.getRoleByTag());
//    }

    @GetMapping("/{tag}")
    public ResponseEntity<UserRole> getRoleByTag(@PathVariable String tag) {
        UserRole role = roleService.getRoleByTag(tag);
        if (role == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(role);
    }

    @PostMapping
    public ResponseEntity<UserRole> createRole(@RequestBody UserRole userRole) {
        UserRole createdRole = roleService.getOrCreateRole(userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }
}
