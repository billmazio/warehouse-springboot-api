package gr.clothesmanager.interfaces;


import gr.clothesmanager.dto.SizeDTO;
import gr.clothesmanager.service.exceptions.SizeAlreadyExistsException;

import java.util.List;

public interface SizeService {

    SizeDTO findById(Long id) throws SizeAlreadyExistsException;


    List<SizeDTO> findAll();
}
