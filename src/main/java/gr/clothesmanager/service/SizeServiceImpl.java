package gr.clothesmanager.service;

import gr.clothesmanager.dto.SizeDTO;
import gr.clothesmanager.model.Size;
import gr.clothesmanager.repository.SizeRepository;
import gr.clothesmanager.service.exceptions.SizeAlreadyExistsException;
import gr.clothesmanager.interfaces.SizeService;
import gr.clothesmanager.service.exceptions.SizeNotFoundException;
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
public class SizeServiceImpl implements SizeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SizeServiceImpl.class);
    private final SizeRepository sizeRepository;

    @Transactional
    public SizeDTO findById(Long id) throws SizeNotFoundException {
        if (id == null) {
            LOGGER.error("[findById] Size ID is null");
            throw new SizeNotFoundException("Size ID cannot be null");
        }

        Size size = sizeRepository.findById(id).orElseThrow(() -> {
            LOGGER.error("[findById] Size not found with ID: {}", id);
            return new SizeNotFoundException("Size with ID " + id + " not found.");
        });

        return SizeDTO.fromModel(size);
    }

    @Transactional
    public List<SizeDTO> findAll() {
        var sizes = sizeRepository.findAll();
        return sizes.stream()
                .map(SizeDTO::fromModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public SizeDTO save(SizeDTO sizeDTO) throws SizeAlreadyExistsException {
        if (sizeDTO == null) {
            LOGGER.error("[save] SizeDTO is null");
            throw new IllegalArgumentException("SizeDTO cannot be null");
        }
        String name = sizeDTO.getName();
        if (name == null) {
            LOGGER.error("[save] Size name is blank");
            throw new IllegalArgumentException("Size name cannot be blank");
        }

        Size saved = sizeRepository.save(new Size(name));
        LOGGER.info("Successfully saved size with ID: {}", saved.getId());
        return SizeDTO.fromModel(saved);
    }
}
