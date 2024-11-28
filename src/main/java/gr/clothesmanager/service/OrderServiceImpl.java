package gr.clothesmanager.service;


import gr.clothesmanager.dto.OrderDTO;
import gr.clothesmanager.interfaces.OrderService;
import gr.clothesmanager.model.Material;
import gr.clothesmanager.model.Order;
import gr.clothesmanager.repository.MaterialRepository;
import gr.clothesmanager.repository.OrderRepository;
import gr.clothesmanager.service.exceptions.OrderNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final MaterialRepository materialRepository;

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

    @Transactional
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
