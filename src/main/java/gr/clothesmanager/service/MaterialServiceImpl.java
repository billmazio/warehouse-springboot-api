package gr.clothesmanager.service;

import gr.clothesmanager.auth.AuthorizationService;
import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.interfaces.MaterialService;
import gr.clothesmanager.model.Material;
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

        Size size = sizeRepository.findById(materialDTO.getSizeId())
                .orElseThrow(() -> new MaterialAlreadyExistsException("Size not found with ID: " + materialDTO.getSizeId()));

        Store store = storeRepository.findById(materialDTO.getStoreId())
                .orElseThrow(() -> new MaterialAlreadyExistsException("Store not found with ID: " + materialDTO.getStoreId()));

        Material material = new Material(materialDTO.getText(), materialDTO.getQuantity(), size, store);
        material = materialRepository.save(material);

        return MaterialDTO.fromModel(material);
    }

    @Transactional
    public List<MaterialDTO> findMaterialsByStoreId(Long storeId) throws UserNotFoundException {
        UserDTO currentUser = userServiceImpl.getAuthenticatedUserDetails();

        if (currentUser.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("LOCAL_ADMIN"))) {
            if (!storeId.equals(currentUser.getStore().getId())) {
                throw new AccessDeniedException("You do not have permission to access materials for this store.");
            }
        }

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
                sizeId.orElse(null)
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
        authorizationService.authorize(userServiceImpl.getAuthenticatedUserDetails().getUsername(), "SUPER_ADMIN");
        LOGGER.info("Authorization passed for deleting material ID: {}", id);

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException("Το υλικό με ID " + id + " δεν βρέθηκε."));
        LOGGER.info("Found material with ID: {} for deletion", id);

        // Check if the material has associated orders
        boolean hasOrders = orderRepository.existsByMaterial_Id(id);
        LOGGER.info("Material has associated orders: {}", hasOrders);

        if (hasOrders) {
            throw new IllegalStateException("Το υλικό έχει συνδεδεμένες παραγγελίες και δεν μπορεί να διαγραφεί.");
        }

        try {
            LOGGER.info("Executing direct delete for material ID: {}", id);
            materialRepository.deleteDirectlyById(id);
            materialRepository.flush();
            LOGGER.info("Material deletion completed for ID: {}", id);

            // Verify deletion
            boolean stillExists = materialRepository.existsById(id);
            LOGGER.info("Verification check - Material still exists: {}", stillExists);

            if (stillExists) {
                throw new RuntimeException("Material was not deleted despite successful operation");
            }
        } catch (Exception ex) {
            LOGGER.error("Error deleting material: {}", ex.getMessage(), ex);
            throw new RuntimeException("Παρουσιάστηκε σφάλμα κατά τη διαγραφή του υλικού: " + ex.getMessage(), ex);
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

        if (currentUser.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("LOCAL_ADMIN"))) {
            storeId = currentUser.getStore().getId(); // Force storeId to the user's store
        }

        Page<Material> materialsPage;
        if (storeId == null) {
            materialsPage = materialRepository.findAllByFilters(text, sizeId, pageable); // For SUPER_ADMIN
        } else {
            materialsPage = materialRepository.findByStoreIdAndFilters(storeId, text, sizeId, pageable); // For LOCAL_ADMIN
        }

        return materialsPage.map(this::convertToDTO);
    }
}
