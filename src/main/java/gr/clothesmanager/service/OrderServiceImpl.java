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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        // Fetch store by title
        Store store = storeRepository.findByTitle(orderDTO.getStoreTitle())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        // Fetch size by name
        Size size = sizeRepository.findByName(orderDTO.getSizeName())
                .orElseThrow(() -> new RuntimeException("Size not found"));

        // Fetch material by text, size ID, and store ID
        Optional<Material> materialOpt = materialRepository.findByTextAndSizeIdAndStoreId(
                orderDTO.getMaterialText(), size.getId(), store.getId());

        if (materialOpt.isEmpty()) {
            throw new RuntimeException("Material not found for the specified size and store");
        }

        Material material = materialOpt.get();
        int requestedQuantity = orderDTO.getQuantity();

        if (material.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException("Insufficient stock. Available quantity: " + material.getQuantity());
        }

        // Deduct stock from material
        material.setQuantity(material.getQuantity() - requestedQuantity);
        materialRepository.save(material);

        // Create and save the order
        Order order = orderDTO.toModel();
        order.setMaterial(material);
        order.setSize(size);
        order.setStore(store);
        order.setUser(userRepository.findByUsername(orderDTO.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found")));
        order.setStatus(orderDTO.getStatus());
        Order savedOrder = orderRepository.save(order);

        // Create response DTO with sold and remaining stock information
        OrderDTO responseDTO = OrderDTO.fromModel(savedOrder);
        responseDTO.setStock(material.getQuantity()); // Set remaining stock
        return responseDTO;
    }

    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) throws OrderNotFoundException {
        // Fetch existing order
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));

        // Fetch store by title
        Store store = storeRepository.findByTitle(orderDTO.getStoreTitle())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        // Fetch size by name
        Size size = sizeRepository.findByName(orderDTO.getSizeName())
                .orElseThrow(() -> new RuntimeException("Size not found"));

        // Fetch material by text, size ID, and store ID
        Optional<Material> materialOpt = materialRepository.findByTextAndSizeIdAndStoreId(
                orderDTO.getMaterialText(), size.getId(), store.getId());

        if (materialOpt.isEmpty()) {
            throw new RuntimeException("Material not found for the specified size and store");
        }

        Material material = materialOpt.get();

        int oldQuantity = order.getQuantity();
        int newQuantity = orderDTO.getQuantity();
        int quantityDifference = newQuantity - oldQuantity;

        // Check if the order is being canceled
        if (order.getStatus() != 3 && orderDTO.getStatus() == 3) {
            // Restore stock by adding the old quantity back to the material
            material.setQuantity(material.getQuantity() + oldQuantity);
            materialRepository.save(material);
        } else if (orderDTO.getStatus() != 3) {
            // Handle stock deduction if the status is not canceled and quantity changes
            if (material.getQuantity() < quantityDifference) {
                throw new InsufficientStockException("Insufficient stock. Available quantity: " + material.getQuantity());
            }

            material.setQuantity(material.getQuantity() - quantityDifference);
            materialRepository.save(material);
        }

        // Update order details
        order.setQuantity(newQuantity);
        order.setDateOfOrder(orderDTO.getDateOfOrder());
        order.setStatus(orderDTO.getStatus());
        order.setMaterial(material);
        order.setSize(size);
        order.setStore(store);
        order.setUser(userRepository.findByUsername(orderDTO.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found")));

        Order updatedOrder = orderRepository.save(order);
        LOGGER.info("Order updated with ID: {}", updatedOrder.getId());

        OrderDTO responseDTO = OrderDTO.fromModel(updatedOrder);
        responseDTO.setStock(material.getQuantity()); // Set remaining stock
        return responseDTO;
    }

    @Transactional
    public OrderDTO findById(Long id) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));
        LOGGER.info("Order retrieved with ID: {}", id);
        return OrderDTO.fromModel(order);
    }

    @Transactional
    public List<OrderDTO> findAll(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders;
        if (user.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("SUPER_ADMIN"))) {
            orders = orderRepository.findAll();
        } else {
            orders = orderRepository.findByStoreId(user.getStore().getId());
        }

        LOGGER.info("Retrieved orders for user: {}. Total count: {}", username, orders.size());
        return orders.stream()
                .map(OrderDTO::fromModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) throws OrderNotFoundException {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order with ID " + id + " not found.");
        }
        orderRepository.deleteById(id);
        LOGGER.info("Order deleted with ID: {}", id);
    }

    @Transactional
    public Page<OrderDTO> findOrdersPaginatedWithFilters(String username, Long storeId, String materialText, String sizeName, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<Order> ordersPage;
        if (user.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("SUPER_ADMIN"))) {
            // Super admin can filter orders across all stores
            ordersPage = orderRepository.findAllByFilters(storeId, null, materialText, sizeName, pageable);
        } else {
            // Local admin can only filter orders for their store
            Long localStoreId = user.getStore().getId();
            ordersPage = orderRepository.findAllByFilters(localStoreId, null, materialText, sizeName, pageable);
        }

        return ordersPage.map(OrderDTO::fromModel);
    }
}


