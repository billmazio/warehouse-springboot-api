package gr.clothesmanager.controller;

import gr.clothesmanager.auth.AuthorizationService;
import gr.clothesmanager.dto.DistributionRequestDTO;
import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.repository.MaterialRepository;
import gr.clothesmanager.service.MaterialServiceImpl;
import gr.clothesmanager.service.exceptions.MaterialAlreadyExistsException;
import gr.clothesmanager.service.exceptions.MaterialNotFoundException;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialServiceImpl materialService;
    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<MaterialDTO> save(@Valid @RequestBody MaterialDTO materialDTO) {
        try {
            MaterialDTO savedMaterial = materialService.save(materialDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMaterial);
        } catch (MaterialAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @GetMapping("/{storeId}/materials")
    public ResponseEntity<List<MaterialDTO>> findMaterialsByStoreId(@PathVariable Long storeId) {
        try {
            // Fetch materials for the specified store
            List<MaterialDTO> materials = materialService.findMaterialsByStoreId(storeId);
            return ResponseEntity.ok(materials);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialDTO> findById(@PathVariable Long id) {
        try {
            MaterialDTO material = materialService.findById(id);
            return ResponseEntity.ok(material);
        } catch (MaterialNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/{materialId}/distribute")
    public ResponseEntity<Void> distributeMaterial(
            @PathVariable Long materialId,
            @RequestBody DistributionRequestDTO distributionRequest) {

        materialService.distributeMaterial(
                materialId,
                distributionRequest.getReceiverStoreId(),
                distributionRequest.getQuantity()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<MaterialDTO>> findAll(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Long sizeId
    ) {
        // Pass the query parameters as Optional to the service
        List<MaterialDTO> materials = materialService.findAll(
                Optional.ofNullable(text),
                Optional.ofNullable(sizeId)
        );
        return ResponseEntity.ok(materials);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialDTO> edit(@PathVariable Long id, @Valid @RequestBody MaterialDTO materialDTO) {
        try {
            MaterialDTO updatedMaterial = materialService.edit(id, materialDTO);
            return ResponseEntity.ok(updatedMaterial);
        } catch (MaterialNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            // Perform authorization check
            String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            authorizationService.authorize(authenticatedUsername, "SUPER_ADMIN");

            // Call the service to delete the material
            materialService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Δεν έχετε δικαίωμα να διαγράψετε προϊόντα."));
        } catch (MaterialNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Το προϊόν δεν βρέθηκε."));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Δεν είναι δυνατή η διαγραφή του προϊόντος, καθώς υπάρχουν σχετικές παραγγελίες."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Παρουσιάστηκε σφάλμα κατά τη διαγραφή του προϊόντος."));
        }
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<MaterialDTO>> findMaterialsPaginated(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Long sizeId,
            Pageable pageable) {
        try {
            Page<MaterialDTO> materials = materialService.findAllPaginatedWithFilters(storeId, text, sizeId, pageable);
            return ResponseEntity.ok(materials);
        } catch (AccessDeniedException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @GetMapping("/all/paginated")
    public ResponseEntity<Page<MaterialDTO>> findAllMaterials(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Long sizeId,
            Pageable pageable) throws UserNotFoundException {
        Page<MaterialDTO> materials = materialService.findAllPaginatedWithFilters(null, text, sizeId, pageable);
        return ResponseEntity.ok(materials);
    }
}
