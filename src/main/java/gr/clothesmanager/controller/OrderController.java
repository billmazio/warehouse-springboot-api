package gr.clothesmanager.controller;

import gr.clothesmanager.dto.OrderDTO;
import gr.clothesmanager.dto.PageResponse;
import gr.clothesmanager.interfaces.OrderService;
import gr.clothesmanager.service.OrderServiceImpl;
import gr.clothesmanager.service.exceptions.InsufficientStockException;
import gr.clothesmanager.service.exceptions.OrderAlreadyExistsException;
import gr.clothesmanager.service.exceptions.OrderNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<?> save(@Valid @RequestBody OrderDTO orderDTO) {
        try {
            OrderDTO savedOrder = orderService.save(orderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + e.getMessage());
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
        try {
            OrderDTO order = orderService.findById(id);
            return ResponseEntity.ok(order);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> findAll() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            List<OrderDTO> orders = orderService.findAll(username);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        try {
            OrderDTO updatedOrder = orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(updatedOrder);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            orderService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/paginated")
    public ResponseEntity<PageResponse<OrderDTO>> getOrdersPaginated(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String materialText,
            @RequestParam(required = false) String sizeName,
            @RequestParam int page,
            @RequestParam int size) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Page<OrderDTO> ordersPage = orderService.findOrdersPaginatedWithFilters(
                    username, storeId, materialText, sizeName, PageRequest.of(page, size));
            return ResponseEntity.ok(PageResponse.from(ordersPage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}




