package com.airSphereConnect.services.implementations;

import com.airSphereConnect.dtos.request.UserRequestDto;
import com.airSphereConnect.dtos.response.UserResponseDto;
import com.airSphereConnect.entities.User;
import com.airSphereConnect.exceptions.GlobalException;
import com.airSphereConnect.mapper.UserMapper;
import com.airSphereConnect.repositories.UserRepository;
import com.airSphereConnect.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new GlobalException.UserNotFoundException("Utilisateur non trouvé avec le username : " + username));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new GlobalException.UserNotFoundException("Utilisateur non trouvé avec l'id : " + id));
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userDto) {
        User user = UserMapper.toEntity(userDto);
        User created = userRepository.save(user);
        UserResponseDto dto = UserMapper.toResponseDto(created);
        return null;
    }

    @Override
    public User updateUser(Long id, User newUserData) {
        User user = getUserById(id);
        user.setUsername(newUserData.getUsername());
        user.setEmail(newUserData.getEmail());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
