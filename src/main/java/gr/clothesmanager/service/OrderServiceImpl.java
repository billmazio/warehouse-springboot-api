package gr.clothesmanager.service;

import gr.clothesmanager.core.enums.OrderStatus;
import gr.clothesmanager.dto.OrderDTO;
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
        if (orderDTO.getStore() == null || orderDTO.getStore().getTitle() == null) {
            throw new IllegalArgumentException("Store information is required");
        }

        if (orderDTO.getSize() == null || orderDTO.getSize().getName() == null) {
            throw new IllegalArgumentException("Size information is required");
        }

        if (orderDTO.getMaterial() == null || orderDTO.getMaterial().getText() == null) {
            throw new IllegalArgumentException("Material information is required");
        }

        if (orderDTO.getUser() == null || orderDTO.getUser().getUsername() == null) {
            throw new IllegalArgumentException("User information is required");
        }

        Store store = storeRepository.findByTitle(orderDTO.getStore().getTitle())
                .orElseThrow(() -> new RuntimeException("STORE_NOT_FOUND"));

        Size size = sizeRepository.findByName(orderDTO.getSize().getName())
                .orElseThrow(() -> new RuntimeException("SIZE_NOT_FOUND"));

        Optional<Material> materialOpt = materialRepository.findByTextAndSizeIdAndStoreId(
                orderDTO.getMaterial().getText(), size.getId(), store.getId());

        if (materialOpt.isEmpty()) {
            throw new RuntimeException("Material not found for the specified size and store");
        }

        Material material = materialOpt.get();
        int requestedQuantity = orderDTO.getQuantity();

        if (material.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException("INSUFFICIENT_STOCK");
        }

        material.setQuantity(material.getQuantity() - requestedQuantity);
        materialRepository.save(material);

        Order order = orderDTO.toModel();
        order.setMaterial(material);
        order.setSize(size);
        order.setStore(store);
        order.setUser(userRepository.findByUsername(orderDTO.getUser().getUsername())
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND")));

        order.setOrderStatus(orderDTO.getOrderStatus() != null ?
                orderDTO.getOrderStatus() : OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        OrderDTO responseDTO = OrderDTO.fromModel(savedOrder);
        responseDTO.setStock(material.getQuantity());
        return responseDTO;
    }

    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + id + " not found."));

        Store store = storeRepository.findByTitle(orderDTO.getStore().getTitle())
                .orElseThrow(() -> new RuntimeException("STORE_NOT_FOUND"));

        Size size = sizeRepository.findByName(orderDTO.getSize().getName())
                .orElseThrow(() -> new RuntimeException("SIZE_NOT_FOUND"));

        Optional<Material> materialOpt = materialRepository.findByTextAndSizeIdAndStoreId(
                orderDTO.getMaterial().getText(), size.getId(), store.getId());

        if (materialOpt.isEmpty()) {
            throw new RuntimeException("Material not found for the specified size and store");
        }

        Material material = materialOpt.get();

        int oldQuantity = order.getQuantity();
        int newQuantity = orderDTO.getQuantity();
        int quantityDifference = newQuantity - oldQuantity;


        boolean wasOrderCancelled = order.getOrderStatus() != OrderStatus.CANCELLED;
        boolean isOrderNowCancelled = orderDTO.getOrderStatus() == OrderStatus.CANCELLED;

        if (wasOrderCancelled && isOrderNowCancelled) {
            // If the order is being cancelled, add the quantity back to stock
            material.setQuantity(material.getQuantity() + oldQuantity);
            materialRepository.save(material);
        } else if (!isOrderNowCancelled) {
            // If the order is not cancelled and quantity changed
            if (material.getQuantity() < quantityDifference) {
                throw new InsufficientStockException("INSUFFICIENT_STOCK");
            }

            material.setQuantity(material.getQuantity() - quantityDifference);
            materialRepository.save(material);
        }

        order.setQuantity(newQuantity);
        order.setDateOfOrder(orderDTO.getDateOfOrder());
        order.setOrderStatus(orderDTO.getOrderStatus());
        order.setMaterial(material);
        order.setSize(size);
        order.setStore(store);
        order.setUser(userRepository.findByUsername(orderDTO.getUser().getUsername())
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND")));

        Order updatedOrder = orderRepository.save(order);
        LOGGER.info("Order updated with ID: {}", updatedOrder.getId());

        OrderDTO responseDTO = OrderDTO.fromModel(updatedOrder);
        responseDTO.setStock(material.getQuantity());
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
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

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
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        Page<Order> ordersPage;
        if (user.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("SUPER_ADMIN"))) {
            ordersPage = orderRepository.findAllByFilters(storeId, null, materialText, sizeName, pageable);
        } else {
            Long localStoreId = user.getStore().getId();
            ordersPage = orderRepository.findAllByFilters(localStoreId, null, materialText, sizeName, pageable);
        }

        return ordersPage.map(OrderDTO::fromModel);
    }
}