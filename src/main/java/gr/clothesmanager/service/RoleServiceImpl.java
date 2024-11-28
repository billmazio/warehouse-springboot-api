package gr.clothesmanager.service;

import gr.clothesmanager.interfaces.RoleService;
import gr.clothesmanager.model.UserRole;
import gr.clothesmanager.repository.AppViewRepository;
import gr.clothesmanager.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final UserRoleRepository roleRepository;
    private final AppViewRepository appViewRepository;

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

    @Transactional
    public UserRole getOrCreateRole(UserRole userRole) {
        var role = roleRepository.findByTag(userRole.getTag()).orElse(null);
        if (role != null) return role;
        return roleRepository.save(userRole);
    }


    @Transactional
    public void getRolesPerView(String name) {
        var roles = appViewRepository.findViewsRolesPermissionsPerView("teacherRegistrationList");
        System.out.println("roles" + roles);
    }
}
