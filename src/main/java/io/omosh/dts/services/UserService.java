package io.omosh.dts.services;

import io.omosh.dts.dtos.CreateUserDTO;
import io.omosh.dts.dtos.UserDTO;
import io.omosh.dts.models.User;
import io.omosh.dts.repositories.UserRepository;
import kotlin.collections.ArrayDeque;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UserDTO> getAllUsersDTOS() {
        List<UserDTO> userDTOS = new ArrayDeque<>();
        for (User user : userRepository.findAll()) {
            userDTOS.add(new UserDTO(user));
        }
        return userDTOS;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User createUser(CreateUserDTO createUserDTO) {
        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setFullname(createUserDTO.getFullname());
        user.setStatus(createUserDTO.getStatus());
        user.setRole(createUserDTO.getRole());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword())); // Encrypt password
        user.setEmail(createUserDTO.getEmail());

        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setFullname(updatedUser.getFullname());
            user.setEmail(updatedUser.getEmail());
            user.setStatus(updatedUser.getStatus());
            user.setRole(updatedUser.getRole());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("Update failed. User not found"));
    }

}