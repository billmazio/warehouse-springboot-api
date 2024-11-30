package gr.clothesmanager.interfaces;


import gr.clothesmanager.dto.StoreDTO;

import java.util.List;

public interface StoreService {

    StoreDTO save(StoreDTO storeDTO);

    StoreDTO findById(Long id);

    List<StoreDTO> findAll();

    void edit (Long id , StoreDTO storeDTO ) ;
}
