package gr.clothesmanager.interfaces;

import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.service.exceptions.MaterialAlreadyExistsException;
import gr.clothesmanager.service.exceptions.MaterialNotFoundException;
import gr.clothesmanager.service.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface MaterialService {

    MaterialDTO save(MaterialDTO materialDTO) throws MaterialAlreadyExistsException;

    MaterialDTO findById(Long id) throws MaterialNotFoundException;

    List<MaterialDTO> findAll(Optional<String> text, Optional<Long> sizeId); // Updated to use Optional for filtering

    MaterialDTO edit(Long id, MaterialDTO materialDTO) throws MaterialNotFoundException;

    void delete(Long id) throws MaterialNotFoundException, UserNotFoundException;
}
