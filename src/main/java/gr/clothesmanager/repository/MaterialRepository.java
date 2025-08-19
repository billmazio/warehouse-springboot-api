package gr.clothesmanager.repository;


import gr.clothesmanager.model.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    @Query("SELECT m FROM Material m WHERE m.store.id = :storeId")
    List<Material> findByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT m FROM Material m WHERE m.text = :text AND m.store.title = :storeTitle")
    Optional<Material> findByTextAndStoreTitle(@Param("text") String text, @Param("storeTitle") String storeTitle);

    @Query("SELECT m FROM Material m WHERE (:text IS NULL OR m.text = :text) AND (:sizeId IS NULL OR m.size.id = :sizeId)")
    List<Material> findByOptionalFilters(@org.springframework.lang.Nullable String text, @org.springframework.lang.Nullable Long sizeId);

    @Query("SELECT COUNT(m) FROM Material m")
    int countMaterials();

    @Query("SELECT m FROM Material m " +
            "WHERE (:text IS NULL OR LOWER(m.text) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:sizeId IS NULL OR m.size.id = :sizeId) " +
            "AND (:storeId IS NULL OR m.store.id = :storeId)")
    Page<Material> findByStoreIdAndFilters(@Param("storeId") Long storeId,
                                           @Param("text") String text,
                                           @Param("sizeId") Long sizeId,
                                           Pageable pageable);

    @Query("SELECT m FROM Material m " +
            "WHERE LOWER(m.text) = LOWER(:text) " +
            "AND m.size.id = :sizeId " +
            "AND m.store.id = :storeId")
    Optional<Material> findByTextAndSizeIdAndStoreId(@Param("text") String text,
                                                     @Param("sizeId") Long sizeId,
                                                     @Param("storeId") Long storeId);

    @Query("SELECT m FROM Material m " +
            "WHERE (:text IS NULL OR LOWER(m.text) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:sizeId IS NULL OR m.size.id = :sizeId)")
    Page<Material> findAllByFilters(@Param("text") String text,
                                    @Param("sizeId") Long sizeId,
                                    Pageable pageable);

    boolean existsByTextAndStoreIdAndSize_Id(String text, Long storeId, Long sizeId);

    boolean existsByStoreId(Long storeId);

    @Modifying
    @Query("DELETE FROM Material m WHERE m.id = :id")
    void deleteDirectlyById(@Param("id") Long id);
}






