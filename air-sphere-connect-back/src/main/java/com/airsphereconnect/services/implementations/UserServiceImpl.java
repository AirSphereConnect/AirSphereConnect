package com.airsphereconnect.services.implementations;

import com.airsphereconnect.dtos.response.UserResponseDto;
import com.airsphereconnect.entities.User;
import com.airsphereconnect.exceptions.GlobalException;
import com.airsphereconnect.mapper.UserMapper;
import com.airsphereconnect.repositories.UserRepository;
import com.airsphereconnect.services.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND = "Utilisateur non trouvé avec l'id : ";
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
        return userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() ->
                        new GlobalException.ResourceNotFoundException("Utilisateur non trouvé avec le username : " + username));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new GlobalException.ResourceNotFoundException(USER_NOT_FOUND + id));
    }

    @Override
    public User createUser(User user) {
        //à vérifier l'utilité de ctrl l'existance d'un id avant de créer (la base de donnée n'accepte pas les doublons

        if (userRepository.existsByUsernameAndDeletedAtIsNull(user.getUsername())) {
            throw new GlobalException.BadRequestException("Le nom d'utilisateur existe déjà.");
        }
        if (userRepository.existsByEmailAndDeletedAtIsNull(user.getEmail())) {
            throw new GlobalException.BadRequestException("L'email existe déjà.");
        }

        if (user.getAddress() != null) {
            user.getAddress().setUser(user);
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User newUserData) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException(USER_NOT_FOUND + id));

        if (newUserData.getUsername() != null && !newUserData.getUsername().equals(user.getUsername())
                && userRepository.findByUsernameAndDeletedAtIsNull(newUserData.getUsername()).isPresent()) {
            throw new GlobalException.BadRequestException("Le nom d'utilisateur existe déjà.");
        }

        if (newUserData.getEmail() != null && !newUserData.getEmail().equals(user.getEmail())
                && userRepository.findByEmailAndDeletedAtIsNull(newUserData.getEmail()).isPresent()) {
            throw new GlobalException.BadRequestException("L'email existe déjà.");
        }

        if (newUserData.getUsername() != null) user.setUsername(newUserData.getUsername());
        if (newUserData.getEmail() != null) user.setEmail(newUserData.getEmail());
        if (newUserData.getPassword() != null && !newUserData.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(newUserData.getPassword()));
        }

        return userRepository.save(user);
    }

    @Transactional
    @Override
    public UserResponseDto deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new GlobalException.ResourceNotFoundException(USER_NOT_FOUND + id));
        user.softDelete();
        log.debug("DEBUG before save deletedAt : {}", user.getDeletedAt());
        User saved = userRepository.save(user);
        log.debug("DEBUG after save deletedAt : {}", saved.getDeletedAt());
        return userMapper.toDto(saved);
    }



    @Override
    public Optional<User> findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameAndDeletedAtIsNull(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameAndDeletedAtIsNull(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndDeletedAtIsNull(email);
    }

}
