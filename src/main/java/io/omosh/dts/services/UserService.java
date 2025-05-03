package io.omosh.dts.services;

import io.omosh.dts.dtos.UserDTO;
import io.omosh.dts.models.User;
import io.omosh.dts.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Mono<User> createUser(UserDTO userDTO) {
        return userRepository.existsByUsername(userDTO.getUsername())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new RuntimeException("Username already exists"));
                    }

                    User user = new User();
                    user.setUsername(userDTO.getUsername());
                    user.setFullname(userDTO.getFullname());
                    user.setStatus(userDTO.getStatus());
                    user.setRole(userDTO.getRole());
                    user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                    user.setEmail(userDTO.getEmail());

                    return userRepository.save(user);
                });
    }

    public Mono<User> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .flatMap(user -> {
                    user.setFullname(updatedUser.getFullname());
                    user.setEmail(updatedUser.getEmail());
                    user.setStatus(updatedUser.getStatus());
                    user.setRole(updatedUser.getRole());
                    return userRepository.save(user);
                }).switchIfEmpty(Mono.error(new RuntimeException("Update failed. User not found")));
    }

}