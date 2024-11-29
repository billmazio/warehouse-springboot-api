package gr.clothesmanager.interfaces;

import gr.clothesmanager.dto.OrderDTO;
import gr.clothesmanager.service.exceptions.OrderAlreadyExistsException;
import gr.clothesmanager.service.exceptions.OrderNotFoundException;

import java.util.List;

public interface OrderService {

    OrderDTO save(OrderDTO orderDTO) throws OrderAlreadyExistsException;

    OrderDTO findById(Long id) throws OrderNotFoundException;

    List<OrderDTO> findAll();

    void accept(Long id) throws OrderNotFoundException;

    void deny(Long id) throws OrderNotFoundException;

    void delete(Long id) throws OrderNotFoundException;
}
