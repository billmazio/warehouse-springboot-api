package gr.clothesmanager.service;

import gr.clothesmanager.auth.AuthorizationService;
import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.dto.MaterialDistributionDTO;
import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.model.Material;
import gr.clothesmanager.model.Size;
import gr.clothesmanager.model.Store;
import gr.clothesmanager.repository.MaterialRepository;
import gr.clothesmanager.repository.OrderRepository;
import gr.clothesmanager.repository.SizeRepository;
import gr.clothesmanager.repository.StoreRepository;
import gr.clothesmanager.service.exceptions.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialService.class);

    private final MaterialRepository materialRepository;
    private final StoreRepository storeRepository;
    private final SizeRepository sizeRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final AuthorizationService authorizationService;

    @Transactional
    public MaterialDTO save(MaterialDTO materialDTO) throws MaterialAlreadyExistsException, SizeNotFoundException, StoreNotFoundException {
        LOGGER.info("Saving new material with text: {}", materialDTO.getText());

        // Duplicate (text + store + size) -> 409
        if (materialRepository.existsByTextAndStoreIdAndSizeId(
                materialDTO.getText(), materialDTO.getStoreId(), materialDTO.getSizeId())) {
            throw new MaterialAlreadyExistsException("MATERIAL_ALREADY_EXISTS");
        }

        Material material = materialDTO.toModel();
        material.setText(materialDTO.getText());
        material.setQuantity(materialDTO.getQuantity());

        material.setSize(sizeRepository.getReferenceById(materialDTO.getSizeId()));
        material.setStore(storeRepository.getReferenceById(materialDTO.getStoreId()));

        material = materialRepository.save(material);
        return MaterialDTO.fromModel(material);
    }

    @Transactional
    public List<MaterialDTO> findMaterialsByStoreId(Long storeId) throws UserNotFoundException {
        UserDTO currentUser = userService.getAuthenticatedUserDetails();

        // LOCAL_ADMIN can only access their own store
        if (currentUser.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("LOCAL_ADMIN"))) {
            if (!storeId.equals(currentUser.getStore().getId())) {
                throw new AccessDeniedException("ACCESS_DENIED");
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
                .orElseThrow(() -> new MaterialNotFoundException("MATERIAL_NOT_FOUND"));
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
    public MaterialDTO edit(Long id, MaterialDTO materialDTO)
            throws MaterialNotFoundException, SizeNotFoundException, MaterialAlreadyExistsException {

        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException("MATERIAL_NOT_FOUND"));

        String newText = materialDTO.getText();
        int newQty = materialDTO.getQuantity() == null ? 0 : materialDTO.getQuantity();
        Long newSizeId = materialDTO.getSizeId();
        Long storeId = material.getStore().getId();

        if (materialRepository.existsByTextAndStoreIdAndSizeIdAndIdNot(newText, storeId, newSizeId, id)) {
            throw new MaterialAlreadyExistsException("MATERIAL_ALREADY_EXISTS");
        }

        material.setText(newText);
        material.setQuantity(newQty);

        material.setSize(sizeRepository.getReferenceById(newSizeId));

        return MaterialDTO.fromModel(material);
    }

    @Transactional
    public void delete(Long id) throws MaterialNotFoundException, UserNotFoundException {
        authorizationService.authorize(userService.getAuthenticatedUserDetails().getUsername(), "SUPER_ADMIN");
        LOGGER.info("Authorization passed for deleting material ID: {}", id);

        if (!materialRepository.existsById(id)) {
            throw new MaterialNotFoundException("MATERIAL_NOT_FOUND");
        }

        boolean hasOrders = orderRepository.existsByMaterialId(id);
        if (hasOrders) {
            throw new IllegalStateException("MATERIAL_HAS_ORDERS");
        }

        materialRepository.deleteDirectlyById(id);
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
    public MaterialDTO distributeMaterial(MaterialDistributionDTO dto)
            throws MaterialNotFoundException, StoreNotFoundException, InsufficientQuantityException {

        Material source = materialRepository.findByIdForUpdate(dto.getMaterialId())
                .orElseThrow(() -> new MaterialNotFoundException("MATERIAL_NOT_FOUND"));

        Integer qtyObj = dto.getQuantity();
        if (qtyObj == null || qtyObj <= 0) {
            throw new IllegalArgumentException("QUANTITY_REQUIRED");
        }
        int qty = qtyObj;

        if (source.getQuantity() < qty) {
            throw new InsufficientQuantityException("INSUFFICIENT_QUANTITY");
        }

        // receiver store: reference is enough if you only need id
        Store receiverStore;
        try {
            receiverStore = storeRepository.getReferenceById(dto.getReceiverStoreId());
        } catch (EntityNotFoundException ex) {
            throw new StoreNotFoundException("STORE_NOT_FOUND");
        }

        // Find target material (same text + size + receiver store)
        Optional<Material> targetOpt = materialRepository.findByTextAndSizeIdAndStoreId(
                source.getText(),
                source.getSize().getId(),
                dto.getReceiverStoreId()
        );

        Material target = targetOpt.orElseGet(() -> {
            Material m = new Material();
            m.setText(source.getText());
            m.setSize(source.getSize());   // ok (same size entity)
            m.setStore(receiverStore);
            m.setQuantity(0);
            return m;
        });

        // Update quantities (managed entities inside TX)
        source.setQuantity(source.getQuantity() - qty);
        target.setQuantity((target.getQuantity() == null ? 0 : target.getQuantity()) + qty);

        // Save only target if new; source will be auto-flushed
        // (save(source) not required, but ok if you keep it)
        Material savedTarget = materialRepository.save(target);

        return MaterialDTO.fromModel(savedTarget);
    }

    @Transactional
    public Page<MaterialDTO> findAllPaginatedWithFilters(Long storeId, String text, Long sizeId, Pageable pageable) throws UserNotFoundException {
        UserDTO currentUser = userService.getAuthenticatedUserDetails();

        if (currentUser.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("LOCAL_ADMIN"))) {
            storeId = currentUser.getStore().getId(); // force στο store του χρήστη
        }

        Page<Material> materialsPage = (storeId == null)
                ? materialRepository.findAllByFilters(text, sizeId, pageable) // SUPER_ADMIN
                : materialRepository.findByStoreIdAndFilters(storeId, text, sizeId, pageable); // LOCAL_ADMIN

        return materialsPage.map(this::convertToDTO);
    }
}
