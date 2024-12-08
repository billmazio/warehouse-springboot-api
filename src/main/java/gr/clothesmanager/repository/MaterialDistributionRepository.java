package gr.clothesmanager.repository;

import gr.clothesmanager.model.MaterialDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialDistributionRepository extends JpaRepository<MaterialDistribution, Long> {
    List<MaterialDistribution> findByReceiverStoreId(Long storeId); // Find materials distributed to a store
}
