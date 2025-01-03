package gr.clothesmanager.service;


import gr.clothesmanager.dto.OrderDTO;
import gr.clothesmanager.interfaces.OrderService;
import gr.clothesmanager.model.Material;
import gr.clothesmanager.model.MaterialDistribution;
import gr.clothesmanager.model.Order;
import gr.clothesmanager.model.Store;
import gr.clothesmanager.repository.MaterialRepository;
import gr.clothesmanager.repository.OrderRepository;
import gr.clothesmanager.repository.StoreRepository;
import gr.clothesmanager.service.exceptions.OrderNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final MaterialRepository materialRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public OrderDTO save(OrderDTO orderDTO) {
        Order order = orderDTO.toModel();
        Order savedOrder = orderRepository.save(order);
        LOGGER.info("Order saved with ID: {}", savedOrder.getId());
        return OrderDTO.fromModel(savedOrder);
    }

    @Transactional
    public OrderDTO findById(Long id) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));
        LOGGER.info("Order retrieved with ID: {}", id);
        return OrderDTO.fromModel(order);
    }

    @Transactional
    public List<OrderDTO> findAll() {
        List<Order> orders = orderRepository.findAll();
        LOGGER.info("Retrieved all orders. Total count: {}", orders.size());
        return orders.stream()
                .map(OrderDTO::fromModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deny(Long id) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));
        order.setStatus(1);  // Order denied
        orderRepository.save(order);
        LOGGER.info("Order denied with ID: {}", id);
    }

/*    @Transactional
    public void accept(Long id) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));
        order.setStatus(2); // Order accepted
        orderRepository.save(order);

        Material material = order.getMaterial();
        if (material != null) {
            material.setQuantity(material.getQuantity() - order.getQuantity());
            materialRepository.save(material);
            LOGGER.info("Order accepted with ID: {}. Material stock updated.", id);
        } else {
            LOGGER.warn("Order with ID {} has no associated material.", id);
        }
    }*/

    @Transactional
    public void accept(Long id) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));
        order.setStatus(2); // Order accepted
        orderRepository.save(order);

        Material material = order.getMaterial();
        if (material != null) {
            distributeMaterial(material.getId(), order.getStore().getId(), order.getQuantity());
            LOGGER.info("Order accepted with ID: {}. Material stock updated and transferred.", id);
        } else {
            LOGGER.warn("Order with ID {} has no associated material.", id);
        }
    }

    @Transactional
    public void distributeMaterial(Long materialId, Long receiverStoreId, Integer quantity) {
        // Fetch material from the central store
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        if (material.getQuantity() < quantity) {
            throw new RuntimeException("Not enough material available for distribution");
        }

        // Deduct the quantity from the central store
        material.setQuantity(material.getQuantity() - quantity);
        materialRepository.save(material);

        // Fetch the receiver store
        Store receiverStore = storeRepository.findById(receiverStoreId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        // Add the quantity to the receiver store
        Optional<Material> receiverMaterialOpt = materialRepository.findByStoreIdAndMaterialId(receiverStoreId, materialId);
        if (receiverMaterialOpt.isPresent()) {
            Material receiverMaterial = receiverMaterialOpt.get();
            receiverMaterial.setQuantity(receiverMaterial.getQuantity() + quantity);
            materialRepository.save(receiverMaterial);
        } else {
            Material newMaterial = new Material();
            newMaterial.setStore(receiverStore);
            newMaterial.setText(material.getText());
            newMaterial.setQuantity(quantity);
            newMaterial.setSize(material.getSize());
            materialRepository.save(newMaterial);
        }

        // Log the distribution (you can extend this part to store the distribution in a database if needed)
        LOGGER.info("Distributed {} units of Material ID {} from central store to Store ID {} on {}",
                quantity, materialId, receiverStoreId, LocalDate.now());
    }



    @Transactional
    public void delete(Long id) throws OrderNotFoundException {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order with ID " + id + " not found.");
        }
        orderRepository.deleteById(id);
        LOGGER.info("Order deleted with ID: {}", id);
    }
}


