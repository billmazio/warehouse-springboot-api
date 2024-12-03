package gr.clothesmanager.repository;


import gr.clothesmanager.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
   // @Query("SELECT o FROM Order o WHERE o.status = :status")

    @Query("SELECT COUNT(o) FROM Order o")
    int countOrders();

}