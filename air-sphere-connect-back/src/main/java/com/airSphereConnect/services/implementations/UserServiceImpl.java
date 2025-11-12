package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.UserMapper;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findByDeletedAtIsNull();
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new GlobalException.ResourceNotFoundException("Utilisateur non trouvé avec le username : " + username));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new GlobalException.ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + id));
    }

    @Override
    public User createUser(User user) {
        //à vérifier l'utilité de ctrl l'existance d'un id avant de créer (la base de donnée n'accepte pas les doublons
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new GlobalException.BadRequestException("Le nom d'utilisateur existe déjà.");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new GlobalException.BadRequestException("L'email existe déjà.");
        }

        if (user.getAddress() != null) {
            user.getAddress().setUser(user);
        }
        // TODO Une fois spring sécurity en place il faut revoir ce encoder
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User newUserData) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + id));

        if (newUserData.getUsername() != null && !newUserData.getUsername().equals(user.getUsername())
                && userRepository.findByUsername(newUserData.getUsername()).isPresent()) {
            throw new GlobalException.BadRequestException("Le nom d'utilisateur existe déjà.");
        }

        if (newUserData.getEmail() != null && !newUserData.getEmail().equals(user.getEmail())
                && userRepository.findByEmail(newUserData.getEmail()).isPresent()) {
            throw new GlobalException.BadRequestException("L'email existe déjà.");
        }

        if (newUserData.getUsername() != null) user.setUsername(newUserData.getUsername());
        if (newUserData.getEmail() != null) user.setEmail(newUserData.getEmail());
        if (newUserData.getPassword() != null && !newUserData.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(newUserData.getPassword()));
        }

        return userRepository.save(user);
    }


    @Override
    public UserResponseDto deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + id));
        user.softDelete();
        User saved = userRepository.save(user);
        System.out.println("DEBUG-DELETE id USER : " +saved.getUsername());
        return UserMapper.toDto(saved);
    }



    @Override
    public Optional<User> findByUsername(String username) throws UsernameNotFoundException {
        return Optional.empty();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameAndDeletedAtIsNull(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndDeletedAtIsNull(email);
    }

}
