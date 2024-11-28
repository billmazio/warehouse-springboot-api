package gr.clothesmanager.repository;


import gr.clothesmanager.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {

    boolean existsByName(String name);
}