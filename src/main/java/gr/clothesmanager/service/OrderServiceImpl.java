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
        order.setStatus(1); // Example status for 'Completed'
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

        if (material.getQuantity() < quantityDifference) {
            throw new InsufficientStockException("Insufficient stock. Available quantity: " + material.getQuantity());
        }

        // Update material stock
        material.setQuantity(material.getQuantity() - quantityDifference);
        materialRepository.save(material);

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
    public List<OrderDTO> findAll() {
        List<Order> orders = orderRepository.findAll();
        LOGGER.info("Retrieved all orders. Total count: {}", orders.size());
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
    public Page<OrderDTO> findOrdersPaginatedWithFilters(Long storeId, Long userId, String materialText, String sizeName, Pageable pageable) {
        LOGGER.info("Fetching orders with optional filters. Store ID: {}, User ID: {}, Material Text: {}, Size Name: {}", storeId, userId, materialText, sizeName);

        Page<Order> ordersPage;
        if (storeId == null && userId == null && (materialText == null || materialText.isEmpty()) && (sizeName == null || sizeName.isEmpty())) {
            ordersPage = orderRepository.findAll(pageable);
        } else {

            ordersPage = orderRepository.findAllByFilters(storeId, userId, materialText, sizeName, pageable);
        }
        return ordersPage.map(OrderDTO::fromModel);
    }


}


