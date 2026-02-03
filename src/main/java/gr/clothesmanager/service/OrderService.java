package gr.clothesmanager.service;

import gr.clothesmanager.core.enums.OrderStatus;
import gr.clothesmanager.dto.OrderDTO;
import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.model.*;
import gr.clothesmanager.repository.*;
import gr.clothesmanager.service.exceptions.InsufficientStockException;
import gr.clothesmanager.service.exceptions.OrderNotFoundException;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
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
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final MaterialRepository materialRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final SizeRepository sizeRepository;

    @Transactional
    public OrderDTO save(OrderDTO dto) throws UserNotFoundException {
        if (dto.getMaterialId() == null) throw new IllegalArgumentException("MATERIAL_ID_REQUIRED");
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) throw new IllegalArgumentException("QUANTITY_REQUIRED");

        Material material = materialRepository.findByIdForUpdate(dto.getMaterialId())
                .orElseThrow(() -> new RuntimeException("MATERIAL_NOT_FOUND"));

        int requested = dto.getQuantity();
        if (material.getQuantity() < requested) {
            throw new InsufficientStockException("INSUFFICIENT_STOCK");
        }
        material.setQuantity(material.getQuantity() - requested);

        Order order = dto.toModel();
        order.setQuantity(requested);
        order.setDateOfOrder(dto.getDateOfOrder());
        order.setOrderStatus(dto.getOrderStatus() != null ? dto.getOrderStatus() : OrderStatus.PENDING);

        order.setMaterial(material);
        order.setStore(material.getStore());
        order.setSize(material.getSize());

        UserDTO currentUser = userService.getAuthenticatedUserDetails();
        order.setUser(userRepository.getReferenceById(currentUser.getId()));

        Order saved = orderRepository.save(order);

        OrderDTO out = OrderDTO.fromModel(saved);
        out.setStock(material.getQuantity());
        return out;
    }

    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO dto) throws OrderNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("ORDER_NOT_FOUND"));

        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("QUANTITY_REQUIRED");
        }

        Long materialId = order.getMaterial().getId();
        Material material = materialRepository.findByIdForUpdate(materialId)
                .orElseThrow(() -> new RuntimeException("MATERIAL_NOT_FOUND"));

        int oldQty = order.getQuantity();
        int newQty = dto.getQuantity();

        boolean wasCancelled = order.getOrderStatus() == OrderStatus.CANCELLED;
        boolean isNowCancelled = dto.getOrderStatus() == OrderStatus.CANCELLED;

        // A) Active -> Cancelled : return old qty to stock
        if (!wasCancelled && isNowCancelled) {
            material.setQuantity(material.getQuantity() + oldQty);
        }

        // B) Cancelled -> Active : subtract new qty from stock
        else if (wasCancelled && !isNowCancelled) {
            if (material.getQuantity() < newQty) {
                throw new InsufficientStockException("INSUFFICIENT_STOCK");
            }
            material.setQuantity(material.getQuantity() - newQty);
        }

        // C) Active -> Active : adjust by diff
        else if (!wasCancelled) {
            int diff = newQty - oldQty;  // + means need more stock, - means return stock

            if (diff > 0 && material.getQuantity() < diff) {
                throw new InsufficientStockException("INSUFFICIENT_STOCK");
            }
            material.setQuantity(material.getQuantity() - diff);
        }

        // D) Cancelled -> Cancelled : no stock change

        order.setQuantity(newQty);
        order.setDateOfOrder(dto.getDateOfOrder());
        order.setOrderStatus(dto.getOrderStatus());

        Order updated = orderRepository.save(order);

        OrderDTO response = OrderDTO.fromModel(updated);
        response.setStock(material.getQuantity());
        return response;
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