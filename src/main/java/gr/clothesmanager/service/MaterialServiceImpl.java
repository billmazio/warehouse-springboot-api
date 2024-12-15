package gr.clothesmanager.service;

import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.interfaces.MaterialService;
import gr.clothesmanager.model.Material;
import gr.clothesmanager.model.MaterialDistribution;
import gr.clothesmanager.model.Size;
import gr.clothesmanager.model.Store;
import gr.clothesmanager.repository.MaterialDistributionRepository;
import gr.clothesmanager.repository.MaterialRepository;
import gr.clothesmanager.repository.SizeRepository;
import gr.clothesmanager.repository.StoreRepository;
import gr.clothesmanager.service.exceptions.MaterialAlreadyExistsException;
import gr.clothesmanager.service.exceptions.MaterialNotFoundException;
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
public class MaterialServiceImpl implements MaterialService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialServiceImpl.class);

    private final MaterialRepository materialRepository;
    private final StoreRepository storeRepository;
    private final SizeRepository sizeRepository;
    private final MaterialDistributionRepository materialDistributionRepository;

    @Transactional
    public MaterialDTO save(MaterialDTO materialDTO) throws MaterialAlreadyExistsException {
        LOGGER.info("Saving new material with text: {}", materialDTO.getText());

        // Check if the material with the same text already exists in the specified store
        Optional<Material> existingMaterial = materialRepository.findByTextAndStoreId(
                materialDTO.getText(), materialDTO.getStoreId());
        if (existingMaterial.isPresent()) {
            LOGGER.error("Material already exists with text: {} in store ID: {}", materialDTO.getText(), materialDTO.getStoreId());
            throw new MaterialAlreadyExistsException("Material with the same text already exists in this store.");
        }

        // Fetch the associated size
        Size size = sizeRepository.findById(materialDTO.getSizeId())
                .orElseThrow(() -> new MaterialAlreadyExistsException("Size not found with ID: " + materialDTO.getSizeId()));

        // Fetch the associated store
        Store store = storeRepository.findById(materialDTO.getStoreId())
                .orElseThrow(() -> new MaterialAlreadyExistsException("Store not found with ID: " + materialDTO.getStoreId()));

        // Create and save the new material
        Material material = new Material(materialDTO.getText(), materialDTO.getQuantity(), size, store);
        material = materialRepository.save(material);

        return MaterialDTO.fromModel(material);
    }



    @Transactional
    public List<MaterialDTO> findMaterialsByStoreId(Long storeId) {
        // Fetch materials for the specified store
        List<Material> materials = materialRepository.findByStoreId(storeId);
        return materials.stream()
                .map(material -> new MaterialDTO(
                        material.getId(),
                        material.getText(),
                        material.getQuantity(),
                        material.getSize().getId(),  // Use size ID
                        material.getSize().getName(), // Fetch size name
                        material.getStore().getTitle(),
                        storeId

                ))
                .collect(Collectors.toList());
    }



    @Transactional
    public void distributeMaterial(Long materialId, Long receiverStoreId, Integer quantity) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        if (material.getQuantity() < quantity) {
            throw new RuntimeException("Not enough material available for distribution");
        }

        Store receiverStore = storeRepository.findById(receiverStoreId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        // Update the material's quantity in the central store
        material.setQuantity(material.getQuantity() - quantity);
        materialRepository.save(material);

        // Record the distribution
        MaterialDistribution distribution = new MaterialDistribution();
        distribution.setMaterial(material);
        distribution.setReceiverStore(receiverStore);
        distribution.setQuantity(quantity);
        distribution.setDistributionDate(LocalDate.now());
        materialDistributionRepository.save(distribution);
    }

    @Transactional
    public MaterialDTO findById(Long id) throws MaterialNotFoundException {
        LOGGER.info("Finding material with ID: {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException("Material not found with ID: " + id));

        return MaterialDTO.fromModel(material);
    }

    @Transactional
    public List<MaterialDTO> findAll(Optional<String> text, Optional<Long> sizeId) {
        LOGGER.info("Fetching all materials with optional filters.");

        List<Material> materials = materialRepository.findByOptionalFilters(
                text.orElse(null),
                sizeId.orElse(null) // Pass null if Optional is empty
        );

        return materials.stream()
                .map(MaterialDTO::fromModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public MaterialDTO edit(Long id, MaterialDTO materialDTO) throws MaterialNotFoundException {
        LOGGER.info("Editing material with ID: {}", id);

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException("Material not found with ID: " + id));

        material.setText(materialDTO.getText());
        material.setQuantity(materialDTO.getQuantity());

        if (materialDTO.getQuantity() == null) {
            material.setQuantity(0);
        }

        Size size = sizeRepository.findById(materialDTO.getSizeId())
                .orElseThrow(() -> new MaterialNotFoundException("Size not found with ID: " + materialDTO.getSizeId()));
        material.setSize(size);

        material = materialRepository.save(material);
        return MaterialDTO.fromModel(material);
    }

    @Transactional
    public void delete(Long id) throws MaterialNotFoundException {
        LOGGER.info("Deleting material with ID: {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException("Material not found with ID: " + id));

        materialRepository.delete(material);
    }

    private MaterialDTO convertToDTO(Material material) {
        MaterialDTO dto = new MaterialDTO();
        dto.setId(material.getId());
        dto.setText(material.getText());
        dto.setSizeName(material.getSize().getName());
        dto.setQuantity(material.getQuantity());
        dto.setStoreId(material.getStore().getId());
        return dto;
    }

    @Transactional
    public Page<MaterialDTO> findAllPaginated(Long storeId, Pageable pageable) {
        Page<Material> materials = materialRepository.findByStoreId(storeId, pageable);
        return materials.map(this::convertToDTO);
    }



}
