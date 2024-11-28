package gr.clothesmanager.service;

import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.interfaces.MaterialService;
import gr.clothesmanager.model.Material;
import gr.clothesmanager.model.Size;
import gr.clothesmanager.repository.MaterialRepository;
import gr.clothesmanager.repository.SizeRepository;
import gr.clothesmanager.service.exceptions.MaterialAlreadyExistsException;
import gr.clothesmanager.service.exceptions.MaterialNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MaterialServiceImpl.class);

    private final MaterialRepository materialRepository;
    private final SizeRepository sizeRepository;

    @Transactional
    public MaterialDTO save(MaterialDTO materialDTO) throws MaterialAlreadyExistsException {
        LOGGER.info("Saving new material with text: {}", materialDTO.getText());

        Optional<Material> existingMaterial = materialRepository.findByText(materialDTO.getText());
        if (existingMaterial.isPresent()) {
            LOGGER.error("Material already exists with text: {}", materialDTO.getText());
            throw new MaterialAlreadyExistsException("Material with the same text already exists.");
        }

        Size size = sizeRepository.findById(materialDTO.getSizeId())
                .orElseThrow(() -> new MaterialAlreadyExistsException("Size not found with ID: " + materialDTO.getSizeId()));

        Material material = new Material(materialDTO.getText(), materialDTO.getQuantity(), size);
        material = materialRepository.save(material);
        return MaterialDTO.fromModel(material);
    }

    @Transactional
    public MaterialDTO findById(Long id) throws MaterialNotFoundException {
        LOGGER.info("Finding material with ID: {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException("Material not found with ID: " + id));

        return MaterialDTO.fromModel(material);
    }

    @Transactional
    public List<MaterialDTO> findAll() {
        LOGGER.info("Fetching all materials.");
        List<Material> materials = materialRepository.findAll();
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
    @Override
    public void delete(Long id) throws MaterialNotFoundException {
        LOGGER.info("Deleting material with ID: {}", id);
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new MaterialNotFoundException("Material not found with ID: " + id));

        materialRepository.delete(material);
    }
}
