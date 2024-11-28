package gr.clothesmanager.interfaces;

import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.service.exceptions.MaterialAlreadyExistsException;
import gr.clothesmanager.service.exceptions.MaterialNotFoundException;

import java.util.List;

public interface MaterialService {


    MaterialDTO save(MaterialDTO materialDTO) throws MaterialAlreadyExistsException;


    MaterialDTO findById(Long id) throws  MaterialNotFoundException;


    List<MaterialDTO> findAll();


    MaterialDTO edit(Long id, MaterialDTO materialDTO) throws  MaterialNotFoundException;


    void delete(Long id) throws  MaterialNotFoundException;
}
