package gr.clothesmanager.interfaces;


import gr.clothesmanager.dto.StoreDTO;
import gr.clothesmanager.service.exceptions.StoreAlreadyExistsException;
import gr.clothesmanager.service.exceptions.StoreNotFoundException;

import java.util.List;

public interface StoreService {

    StoreDTO save(StoreDTO storeDTO) throws StoreAlreadyExistsException;

    StoreDTO findById(Long id) throws StoreNotFoundException;

    List<StoreDTO> findAll();

    void edit (Long id , StoreDTO storeDTO)  throws StoreNotFoundException;
}
