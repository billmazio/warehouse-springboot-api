package gr.clothesmanager.repository;


import gr.clothesmanager.model.Material;
import gr.clothesmanager.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {

    boolean existsByName(String name);

    Optional<Size> findByName(String name);

    @Query("SELECT COUNT(s) FROM Size s")
    long countSizes();
}