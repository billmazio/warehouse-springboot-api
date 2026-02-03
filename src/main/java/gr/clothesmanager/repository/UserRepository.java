package gr.clothesmanager.repository;

import gr.clothesmanager.core.enums.Status;
import gr.clothesmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.store.id = :storeId")
    List<User> findByStoreIdWithRoles(@Param("storeId") Long storeId);

    Optional<User> findByUsername(String username);

    boolean existsByStoreId(Long storeId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    long countActiveUsersForDashboard(@Param("status") Status status);
}
