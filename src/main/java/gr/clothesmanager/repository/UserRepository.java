package gr.clothesmanager.repository;

import gr.clothesmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.enable = :enable")
    Optional<User> findActiveUser(@Param("username") String username, @Param("enable") Integer enable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.enable = 1")
    int countActiveUsersForDashboard();

    Optional<User> findByUsername(String username);

    List<User> findByStoreId(Long storeId);
}
