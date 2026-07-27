package com.shipcore.business.domain.service.impl;

import com.shipcore.business.api.dto.request.UserRequest;
import com.shipcore.business.api.dto.response.UserResponse;
import com.shipcore.business.api.exception.ResourceAlreadyExistsException;
import com.shipcore.business.api.exception.ResourceNotFoundException;
import com.shipcore.business.data.entity.Organization;
import com.shipcore.business.data.entity.User;
import com.shipcore.business.data.repository.OrganizationRepository;
import com.shipcore.business.data.repository.UserRepository;
import com.shipcore.business.domain.mapper.UserMapper;
import com.shipcore.business.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("El correo ya esta registrado.");
        }

        Organization organization = findOrganization(request.organizationId());
        User user = userMapper.toEntity(request);
        user.setOrganization(organization);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setActive(true);

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAllActiveWithOrganization()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return userMapper.toResponse(findUser(id));
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        User user = findUser(id);

        if (!user.getEmail().equalsIgnoreCase(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("El correo ya esta registrado.");
        }

        userMapper.updateEntity(request, user);
        user.setOrganization(findOrganization(request.organizationId()));
        user.setPassword(passwordEncoder.encode(request.password()));

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        User user = findUser(id);
        user.setActive(false);
        userRepository.save(user);
    }

    private User findUser(Long id) {
        return userRepository.findByIdWithOrganization(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
    }

    private Organization findOrganization(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizacion no encontrada."));
    }

}
