package gr.clothesmanager.interfaces;

import gr.clothesmanager.model.UserRole;

public interface RoleService {


    UserRole getRoleByTag(String tag);


    UserRole getOrCreateRole(String name, String tag);


    UserRole getOrCreateRole(UserRole userRole);


    void getRolesPerView(String name);
}
