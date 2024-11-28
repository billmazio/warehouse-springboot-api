package gr.clothesmanager.interfaces;

import gr.clothesmanager.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);
    Optional<User> findUserById(Long id);
    Optional<User> findUserByUsername(String username);
    List<User> findAllUsers();
    void deleteUserById(Long id);
}
