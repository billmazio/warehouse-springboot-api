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

    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    int countActiveUsersForDashboard(@Param("status") Status status);

    Optional<User> findByUsername(String username);

    List<User> findByStoreId(Long storeId);

    boolean existsByStoreId(Long storeId);
}
