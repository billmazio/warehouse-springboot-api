package gr.clothesmanager.service;

import gr.clothesmanager.dto.SizeDTO;
import gr.clothesmanager.model.Size;
import gr.clothesmanager.repository.SizeRepository;
import gr.clothesmanager.service.exceptions.SizeAlreadyExistsException;
import gr.clothesmanager.service.exceptions.SizeNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SizeService {

    private final SizeRepository sizeRepository;

    @Transactional
    public SizeDTO findById(Long id) throws SizeNotFoundException {
        Optional<Size> size = sizeRepository.findById(id);
        if (size.isPresent()) {
            return SizeDTO.fromModel(size.get());
        }
        throw new SizeNotFoundException("Size with ID " + id + " not found.");
    }

    @Transactional
    public List<SizeDTO> findAll() {
        List<Size> sizes = sizeRepository.findAll();
        return sizes.stream()
                .map(SizeDTO::fromModel)
                .collect(Collectors.toList());
    }

    @Transactional
    public SizeDTO save(SizeDTO dto) throws SizeAlreadyExistsException {
        if (sizeRepository.existsByName(dto.getName())) {
            throw new SizeAlreadyExistsException("Size with name " + dto.getName() + " already exists.");
        }
        Size size = new Size(dto.getName());
        size = sizeRepository.save(size);

        return SizeDTO.fromModel(size);
    }
}
