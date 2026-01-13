package gr.clothesmanager.repository;


import gr.clothesmanager.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT COUNT(o) FROM Order o")
    int countOrders();

    @Query("SELECT o FROM Order o " +
            "WHERE (:storeId IS NULL OR o.store.id = :storeId) " +
            "AND (:userId IS NULL OR o.user.id = :userId) " +
            "AND (:materialText IS NULL OR LOWER(o.material.text) LIKE LOWER(CONCAT('%', :materialText, '%'))) " +
            "AND (:sizeName IS NULL OR LOWER(o.size.name) LIKE LOWER(CONCAT('%', :sizeName, '%')))")
    Page<Order> findAllByFilters(@Param("storeId") Long storeId,
                                 @Param("userId") Long userId,
                                 @Param("materialText") String materialText,
                                 @Param("sizeName") String sizeName,
                                 Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.store.id = :storeId")
    List<Order> findByStoreId(@Param("storeId") Long storeId);

    boolean existsByStoreId(Long storeId);

    boolean existsByMaterial_Id(Long materialId);
}