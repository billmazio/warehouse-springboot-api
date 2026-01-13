package gr.clothesmanager.interfaces;

import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.model.Store;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
import gr.clothesmanager.service.exceptions.UserAlreadyExistsException;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDTO saveUser(UserDTO userDTO, Store store) throws UserAlreadyExistsException;

    Optional<UserDTO> findUserById(Long id) throws UserNotFoundException;

    Optional<UserDTO> findUserByUsername(String username) throws UserNotFoundException;

    List<UserDTO> findAllUsers(String username) throws UserNotFoundException;

    void deleteUserById(Long id) throws UserNotFoundException;
}
