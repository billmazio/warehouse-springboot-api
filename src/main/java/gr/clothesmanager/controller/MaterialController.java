package gr.clothesmanager.controller;

import gr.clothesmanager.auth.AuthorizationService;
import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.dto.MaterialDistributionDTO;
import gr.clothesmanager.dto.PageResponse;
import gr.clothesmanager.service.MaterialService;
import gr.clothesmanager.service.exceptions.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;
    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<MaterialDTO> save(@Valid @RequestBody MaterialDTO materialDTO) throws StoreNotFoundException, MaterialAlreadyExistsException, SizeNotFoundException {
        MaterialDTO savedMaterial = materialService.save(materialDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMaterial);
    }

    @GetMapping("/{storeId}/materials")
    public ResponseEntity<List<MaterialDTO>> findMaterialsByStoreId(@PathVariable Long storeId) throws UserNotFoundException {
        List<MaterialDTO> materials = materialService.findMaterialsByStoreId(storeId);
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialDTO> findById(@PathVariable Long id) throws MaterialNotFoundException {
        MaterialDTO material = materialService.findById(id);
        return ResponseEntity.ok(material);
    }

    @GetMapping
    public ResponseEntity<List<MaterialDTO>> findAll(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Long sizeId
    ) {
        List<MaterialDTO> materials = materialService.findAll(
                Optional.ofNullable(text),
                Optional.ofNullable(sizeId)
        );
        return ResponseEntity.ok(materials);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialDTO> edit(@PathVariable Long id, @Valid @RequestBody MaterialDTO materialDTO) throws MaterialAlreadyExistsException, MaterialNotFoundException, SizeNotFoundException {
        MaterialDTO updatedMaterial = materialService.edit(id, materialDTO);
        return ResponseEntity.ok(updatedMaterial);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws UserNotFoundException, MaterialNotFoundException {
        materialService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/distribute")
    public ResponseEntity<Map<String, Object>> distributeMaterial(@Valid @RequestBody MaterialDistributionDTO distributionDTO) throws StoreNotFoundException, MaterialNotFoundException {
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        authorizationService.authorize(authenticatedUsername, "SUPER_ADMIN");

        MaterialDTO result = materialService.distributeMaterial(distributionDTO);
        return ResponseEntity.ok(Map.of(
                "message", "Material distributed successfully",
                "targetMaterial", result
        ));
    }

    @GetMapping("/paginated")
    public ResponseEntity<PageResponse<MaterialDTO>> findMaterialsPaginated(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Long sizeId,
            Pageable pageable) throws UserNotFoundException {
        Page<MaterialDTO> materialsPage = materialService.findAllPaginatedWithFilters(storeId, text, sizeId, pageable);
        return ResponseEntity.ok(PageResponse.from(materialsPage));
    }

    @GetMapping("/all/paginated")
    public ResponseEntity<PageResponse<MaterialDTO>> findAllMaterials(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Long sizeId,
            Pageable pageable) throws UserNotFoundException {
        Page<MaterialDTO> materialsPage = materialService.findAllPaginatedWithFilters(null, text, sizeId, pageable);
        return ResponseEntity.ok(PageResponse.from(materialsPage));
    }
}