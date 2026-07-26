package com.shipcore.user.service.impl;

import com.shipcore.common.exception.ResourceAlreadyExistsException;
import com.shipcore.common.exception.ResourceNotFoundException;
import com.shipcore.user.dto.request.UserRequest;
import com.shipcore.user.dto.response.UserResponse;
import com.shipcore.user.entity.User;
import com.shipcore.user.mapper.UserMapper;
import com.shipcore.user.repository.UserRepository;
import com.shipcore.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("El correo ya está registrado.");
        }

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);

        user = userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> findAll() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();

    }

    @Override
    public UserResponse findById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado."));

        return userMapper.toResponse(user);

    }

    @Override
    public UserResponse update(Long id, UserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado."));

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            throw new ResourceAlreadyExistsException("El correo ya está registrado.");
        }

        userMapper.updateEntity(request, user);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);

        return userMapper.toResponse(user);

    }

    @Override
    public void delete(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado."));

        userRepository.delete(user);

    }

}