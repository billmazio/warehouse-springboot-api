package gr.clothesmanager.repository;


import gr.clothesmanager.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findByText(String text);
}