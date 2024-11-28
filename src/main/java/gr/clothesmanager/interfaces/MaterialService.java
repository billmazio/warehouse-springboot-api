package gr.clothesmanager.interfaces;

import gr.clothesmanager.dto.MaterialDTO;
import gr.clothesmanager.service.exceptions.MaterialAlreadyExistsException;

import java.util.List;

public interface MaterialService {


    MaterialDTO save(MaterialDTO materialDTO) throws MaterialAlreadyExistsException;


    MaterialDTO findById(Long id) throws MaterialAlreadyExistsException;


    List<MaterialDTO> findAll();


    MaterialDTO edit(Long id, MaterialDTO materialDTO) throws MaterialAlreadyExistsException;


    void delete(Long id) throws MaterialAlreadyExistsException;
}
