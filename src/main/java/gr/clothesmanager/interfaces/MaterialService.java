package gr.clothesmanager.interfaces;

import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.service.exceptions.*;

import java.util.List;
import java.util.Optional;

public interface MaterialService {

    MaterialDTO save(MaterialDTO materialDTO) throws MaterialAlreadyExistsException, SizeNotFoundException, StoreNotFoundException;

    MaterialDTO findById(Long id) throws MaterialNotFoundException;

    List<MaterialDTO> findAll(Optional<String> text, Optional<Long> sizeId); // Updated to use Optional for filtering

    MaterialDTO edit(Long id, MaterialDTO materialDTO) throws MaterialNotFoundException, SizeNotFoundException;

    void delete(Long id) throws MaterialNotFoundException, UserNotFoundException;
}
