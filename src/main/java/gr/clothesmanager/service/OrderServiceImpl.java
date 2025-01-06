package gr.clothesmanager.service;


import gr.clothesmanager.dto.OrderDTO;
import gr.clothesmanager.interfaces.MaterialService;
import gr.clothesmanager.interfaces.OrderService;
import gr.clothesmanager.model.*;
import gr.clothesmanager.repository.*;
import gr.clothesmanager.service.exceptions.InsufficientStockException;
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
    private final UserRepository userRepository;
    private final SizeRepository sizeRepository;


    @Transactional
    public OrderDTO save(OrderDTO orderDTO) {
        Optional<Material> materialOpt = materialRepository.findByTextAndSizeNameAndStoreTitle(
                orderDTO.getMaterialText(), orderDTO.getSizeName(), orderDTO.getStoreTitle());

        if (materialOpt.isEmpty()) {
            throw new RuntimeException("Material not found for the specified size and store");
        }

        Material material = materialOpt.get();
        int requestedQuantity = orderDTO.getQuantity();

        if (material.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException("Insufficient stock. Available quantity: " + material.getQuantity());
        }

        Size size = material.getSize();
        Store store = material.getStore();
        User user = userRepository.findByUsername(orderDTO.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create and set order
        Order order = orderDTO.toModel();
        order.setMaterial(material);
        order.setSize(size);
        order.setStore(store);
        order.setUser(user);

        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Deduct the stock and save the material
        material.setQuantity(material.getQuantity() - requestedQuantity);
        materialRepository.save(material);

        LOGGER.info("Order saved with ID: {} and updated material stock. Remaining quantity: {}",
                savedOrder.getId(), material.getQuantity());

        return OrderDTO.fromModel(savedOrder);
    }



    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));

        // You can map the fields from orderDTO to the order object.
        order.setQuantity(orderDTO.getQuantity());
        order.setDateOfOrder(orderDTO.getDateOfOrder());
        order.setStatus(orderDTO.getStatus());
        order.setStock(orderDTO.getStock());
        order.setSold(orderDTO.getSold());

        // Update the associated entities like material, size, store, user
        Material material = materialRepository.findByText(orderDTO.getMaterialText()).get(0); // Assuming this fetches the correct material
        Size size = sizeRepository.findByName(orderDTO.getSizeName())
                .orElseThrow(() -> new RuntimeException("Size not found"));
        Store store = storeRepository.findByTitle(orderDTO.getStoreTitle())
                .orElseThrow(() -> new RuntimeException("Store not found"));
        User user = userRepository.findByUsername(orderDTO.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        order.setMaterial(material);
        order.setSize(size);
        order.setStore(store);
        order.setUser(user);

        // Save updated order
        Order updatedOrder = orderRepository.save(order);
        LOGGER.info("Order updated with ID: {}", updatedOrder.getId());

        return OrderDTO.fromModel(updatedOrder);
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


