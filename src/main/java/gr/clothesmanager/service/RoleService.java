package gr.clothesmanager.service;

import gr.clothesmanager.model.UserRole;
import gr.clothesmanager.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class RoleService {

    private final UserRoleRepository roleRepository;

    @Transactional
    public UserRole getRoleByTag(String tag) {
        return roleRepository.findByTag(tag).orElse(null);
    }

    @Transactional
    public UserRole getOrCreateRole(String name, String tag) {
        var role = roleRepository.findByTag(tag).orElse(null);
        if (role != null) return role;
        return roleRepository.save(new UserRole(null, name, tag, null));
    }
}
