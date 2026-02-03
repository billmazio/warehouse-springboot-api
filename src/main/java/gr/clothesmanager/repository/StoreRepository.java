package gr.clothesmanager.repository;



import gr.clothesmanager.model.Material;
import gr.clothesmanager.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT COUNT(s) FROM Store s")
    long countStores();

    @Modifying
    @Query("DELETE FROM Store s WHERE s.id = :id")
    int deleteDirectlyById(@Param("id") Long id);
}

