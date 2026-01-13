package gr.clothesmanager.controller;

import gr.clothesmanager.dto.OrderDTO;
import gr.clothesmanager.dto.PageResponse;
import gr.clothesmanager.service.OrderServiceImpl;
import gr.clothesmanager.service.exceptions.OrderNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> save(@Valid @RequestBody OrderDTO orderDTO) {
        OrderDTO savedOrder = orderService.save(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id) throws OrderNotFoundException {
        OrderDTO order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> findAll() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<OrderDTO> orders = orderService.findAll(username);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) throws OrderNotFoundException {
        OrderDTO updatedOrder = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws OrderNotFoundException {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paginated")
    public ResponseEntity<PageResponse<OrderDTO>> getOrdersPaginated(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String materialText,
            @RequestParam(required = false) String sizeName,
            @RequestParam int page,
            @RequestParam int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<OrderDTO> ordersPage = orderService.findOrdersPaginatedWithFilters(
                username, storeId, materialText, sizeName, PageRequest.of(page, size));
        return ResponseEntity.ok(PageResponse.from(ordersPage));
    }
}