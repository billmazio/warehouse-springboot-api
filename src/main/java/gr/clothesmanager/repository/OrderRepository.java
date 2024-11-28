package gr.clothesmanager.repository;


import gr.clothesmanager.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
   // @Query("SELECT o FROM Orders o WHERE o.status = :status")

    Order findById(int id);

    List<Order> findAll();


}