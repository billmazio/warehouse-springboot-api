package gr.clothesmanager.service;

import gr.clothesmanager.dto.UserDTO;
import gr.clothesmanager.interfaces.UserService;
import gr.clothesmanager.model.User;
import gr.clothesmanager.repository.UserRepository;
import gr.clothesmanager.service.exceptions.UserAlreadyExistsException;
import gr.clothesmanager.service.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Override
    public UserDTO saveUser(UserDTO userDTO) throws UserAlreadyExistsException {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            LOGGER.error("User with username '{}' already exists", userDTO.getUsername());
            throw new UserAlreadyExistsException("User with username '" + userDTO.getUsername() + "' already exists");
        }

        User user = userDTO.toModel();
        User savedUser = userRepository.save(user);
        LOGGER.info("User saved with ID: {}", savedUser.getId());
        return UserDTO.fromModel(savedUser);
    }

    @Override
    public Optional<UserDTO> findUserById(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
        return Optional.of(UserDTO.fromModel(user));
    }

    @Override
    public Optional<UserDTO> findUserByUsername(String username) throws UserNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username '" + username + "' not found"));
        return Optional.of(UserDTO.fromModel(user));
    }

    @Override
    public List<UserDTO> findAllUsers() {
        List<User> users = userRepository.findAll();
        LOGGER.info("Fetched {} users from the database", users.size());
        return users.stream().map(UserDTO::fromModel).collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long id) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            LOGGER.error("User with ID '{}' does not exist", id);
            throw new UserNotFoundException("User with ID " + id + " does not exist");
        }
        userRepository.deleteById(id);
        LOGGER.info("User with ID '{}' deleted successfully", id);
    }
}
