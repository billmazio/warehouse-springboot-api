package gr.clothesmanager.interfaces;


import gr.clothesmanager.dto.StoreDTO;
import gr.clothesmanager.service.exceptions.StoreAlreadyExistsException;
import gr.clothesmanager.service.exceptions.StoreNotFoundException;
import gr.clothesmanager.service.exceptions.UserNotFoundException;

import java.util.List;

public interface StoreService {

    StoreDTO save(StoreDTO storeDTO) throws StoreAlreadyExistsException;

    StoreDTO findById(Long id) throws StoreNotFoundException;

    List<StoreDTO> findAll() throws UserNotFoundException;

    void edit (Long id , StoreDTO storeDTO)  throws StoreNotFoundException;
}
