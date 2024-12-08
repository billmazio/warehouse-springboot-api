package gr.clothesmanager.controller;

import gr.clothesmanager.dto.SizeDTO;
import gr.clothesmanager.interfaces.SizeService;
import gr.clothesmanager.service.SizeServiceImpl;
import gr.clothesmanager.service.exceptions.SizeAlreadyExistsException;
import gr.clothesmanager.service.exceptions.SizeNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sizes")
@RequiredArgsConstructor
public class SizeController {

    private final SizeServiceImpl sizeService;

    @GetMapping("/{id}")
    public ResponseEntity<SizeDTO> findById(@PathVariable Long id) {
        try {
            SizeDTO size = sizeService.findById(id);
            return ResponseEntity.ok(size);
        } catch (SizeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<SizeDTO>> findAll() {
        return ResponseEntity.ok(sizeService.findAll());
    }

    @PostMapping
    public ResponseEntity<SizeDTO> save(@Valid @RequestBody SizeDTO sizeDTO) {
        try {
            SizeDTO savedSize = sizeService.save(sizeDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSize);
        } catch (SizeAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }
}
