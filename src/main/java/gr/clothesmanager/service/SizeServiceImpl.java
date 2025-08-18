package gr.clothesmanager.service;

import gr.clothesmanager.dto.SizeDTO;
import gr.clothesmanager.model.Size;
import gr.clothesmanager.repository.SizeRepository;
import gr.clothesmanager.service.exceptions.SizeAlreadyExistsException;
import gr.clothesmanager.interfaces.SizeService;
import gr.clothesmanager.service.exceptions.SizeNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {

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
    public SizeDTO save(SizeDTO sizeDTO) throws SizeAlreadyExistsException {
        if (sizeRepository.existsByName(sizeDTO.getName())) {
            throw new SizeAlreadyExistsException("Size with name " + sizeDTO.getName() + " already exists.");
        }
        Size size = new Size(sizeDTO.getName());
        size = sizeRepository.save(size);

        return SizeDTO.fromModel(size);
    }
}
