package com.airSphereConnect.services.implementations;

import com.airSphereConnect.entities.User;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new GlobalException.RessourceNotFoundException("Utilisateur non trouvé avec le username : " + username));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new GlobalException.RessourceNotFoundException("Utilisateur non trouvé avec l'id : " + id));
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
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User newUserData) {
        //à vérifier l'utilité de CTRL pour l'existence d'un id avant de modifier, c'est l'utilisateur qui veut modifier (la base de données n'accepte pas les doublons)
        if (userRepository.findById(id).isEmpty()) {
            userRepository.findById(id).orElseThrow(() ->
                    new GlobalException.RessourceNotFoundException("Utilisateur non trouvé avec l'id : " + id));
        }
        if (userRepository.findByUsername(newUserData.getUsername()).isPresent()) {
            throw new GlobalException.BadRequestException("Le nom d'utilisateur existe déjà.");
        }
        if (userRepository.findByEmail(newUserData.getEmail()).isPresent()) {
            throw new GlobalException.BadRequestException("L'email existe déjà.");
        }
        User user = getUserById(id);
        user.setUsername(newUserData.getUsername());
        user.setEmail(newUserData.getEmail());
        user.setPassword(encoder.encode(newUserData.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new GlobalException.RessourceNotFoundException("Utilisateur non trouvé avec l'id : " + id));
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
        return user;
    }
}
