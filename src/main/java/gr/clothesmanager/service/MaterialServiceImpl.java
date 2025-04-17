package gr.clothesmanager.service;

import gr.clothesmanager.auth.AuthorizationService;
import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.interfaces.MaterialService;
import gr.clothesmanager.model.Material;
import gr.clothesmanager.model.MaterialDistribution;
import gr.clothesmanager.model.Size;
import gr.clothesmanager.model.Store;
import gr.clothesmanager.repository.*;
import gr.clothesmanager.service.exceptions.MaterialAlreadyExistsException;
import gr.clothesmanager.service.exceptions.MaterialNotFoundException;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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
    private final OrderRepository orderRepository;
    private final UserServiceImpl userServiceImpl;
    private final AuthorizationService authorizationService;

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
    public List<MaterialDTO> findMaterialsByStoreId(Long storeId) throws UserNotFoundException {
        // Fetch the authenticated user's store and role
        UserDTO currentUser = userServiceImpl.getAuthenticatedUserDetails();

        // If user is a LOCAL_ADMIN, restrict access to their assigned store
        if (currentUser.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("LOCAL_ADMIN"))) {
            if (!storeId.equals(currentUser.getStore().getId())) {
                throw new AccessDeniedException("You do not have permission to access materials for this store.");
            }
        }

        // Fetch materials for the specified store
        List<Material> materials = materialRepository.findByStoreId(storeId);
        return materials.stream()
                .map(material -> new MaterialDTO(
                        material.getId(),
                        material.getText(),
                        material.getQuantity(),
                        material.getSize().getId(),
                        material.getSize().getName(),
                        material.getStore().getTitle(),
                        storeId
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void distributeMaterial(Long materialId, Long receiverStoreId, Integer quantity) {
        LOGGER.info("Starting distribution: materialId={}, receiverStoreId={}, quantity={}", materialId, receiverStoreId, quantity);

        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Material not found"));
        LOGGER.info("Fetched material: {}", material);

        if (material.getQuantity() < quantity) {
            throw new RuntimeException("Not enough material available for distribution");
        }

        Store receiverStore = storeRepository.findById(receiverStoreId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
        LOGGER.info("Fetched receiver store: {}", receiverStore);

        // Deduct quantity from central store
        material.setQuantity(material.getQuantity() - quantity);
        materialRepository.save(material);
        LOGGER.info("Updated central store material quantity to {}", material.getQuantity());

        // Check if material exists in receiver store with the same text and size
        Optional<Material> receiverMaterialOpt = materialRepository.findByTextAndSize_IdAndStore_Id(
                material.getText(), material.getSize().getId(), receiverStoreId);
        LOGGER.info("Receiver material exists: {}", receiverMaterialOpt.isPresent());

        Material receiverMaterial;
        if (receiverMaterialOpt.isPresent()) {
            // Update quantity in receiver store
            receiverMaterial = receiverMaterialOpt.get();
            receiverMaterial.setQuantity(receiverMaterial.getQuantity() + quantity);
            LOGGER.info("Updated receiver material quantity to {}", receiverMaterial.getQuantity());
        } else {
            // Create new material in receiver store
            receiverMaterial = new Material(
                    material.getText(),
                    quantity,
                    material.getSize(),
                    receiverStore
            );
            LOGGER.info("Created new material in receiver store: {}", receiverMaterial);
        }
        materialRepository.save(receiverMaterial);
        LOGGER.info("Saved receiver material");

        // Record the distribution
        MaterialDistribution distribution = new MaterialDistribution();
        distribution.setMaterial(material);
        distribution.setReceiverStore(receiverStore);
        distribution.setQuantity(quantity);
        distribution.setDistributionDate(LocalDate.now());
        materialDistributionRepository.save(distribution);
        LOGGER.info("Recorded material distribution: {}", distribution);
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
    public void delete(Long id) throws MaterialNotFoundException, UserNotFoundException {
        // Explicit authorization check
        authorizationService.authorize(userServiceImpl.getAuthenticatedUserDetails().getUsername(), "SUPER_ADMIN");

        LOGGER.info("Attempting to delete material with ID: {}", id);

        // Check if the material exists
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException("Το υλικό με ID " + id + " δεν βρέθηκε."));

        // Check if the material has associated orders
        boolean hasOrders = orderRepository.existsByMaterial_Id(id);

        if (hasOrders) {
            throw new IllegalStateException("Το υλικό έχει συνδεδεμένες παραγγελίες και δεν μπορεί να διαγραφεί.");
        }

        // Attempt to delete the material
        try {
            materialRepository.delete(material);
            LOGGER.info("Successfully deleted material with ID: {}", id);
        } catch (Exception ex) {
            LOGGER.error("Error deleting material: {}", ex.getMessage());
            throw new RuntimeException("Παρουσιάστηκε σφάλμα κατά τη διαγραφή του υλικού.", ex);
        }
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
    public Page<MaterialDTO> findAllPaginatedWithFilters(Long storeId, String text, Long sizeId, Pageable pageable) throws UserNotFoundException {
        UserDTO currentUser = userServiceImpl.getAuthenticatedUserDetails();

        // If the user is a LOCAL_ADMIN, restrict to their store
        if (currentUser.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("LOCAL_ADMIN"))) {
            storeId = currentUser.getStore().getId(); // Force storeId to the user's store
        }

        // Fetch materials based on storeId and filters
        Page<Material> materialsPage;
        if (storeId == null) {
            materialsPage = materialRepository.findAllByFilters(text, sizeId, pageable); // For SUPER_ADMIN
        } else {
            materialsPage = materialRepository.findByStoreIdAndFilters(storeId, text, sizeId, pageable); // For LOCAL_ADMIN
        }

        return materialsPage.map(this::convertToDTO);
    }
}
