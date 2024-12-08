package gr.clothesmanager.controller;

import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.interfaces.MaterialService;
import gr.clothesmanager.service.MaterialServiceImpl;
import gr.clothesmanager.service.exceptions.MaterialAlreadyExistsException;
import gr.clothesmanager.service.exceptions.MaterialNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialServiceImpl materialService;

    @PostMapping
    public ResponseEntity<MaterialDTO> save(@Valid @RequestBody MaterialDTO materialDTO) {
        try {
            MaterialDTO savedMaterial = materialService.save(materialDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMaterial);
        } catch (MaterialAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
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
            @RequestParam Long receiverStoreId,
            @RequestParam Integer quantity) {
        materialService.distributeMaterial(materialId, receiverStoreId, quantity);
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
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            materialService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (MaterialNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
