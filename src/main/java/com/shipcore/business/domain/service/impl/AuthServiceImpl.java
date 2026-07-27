package com.shipcore.business.domain.service.impl;

import com.shipcore.business.api.dto.request.LoginRequest;
import com.shipcore.business.api.dto.request.RegisterRequest;
import com.shipcore.business.api.dto.response.AuthResponse;
import com.shipcore.business.api.exception.ResourceAlreadyExistsException;
import com.shipcore.business.api.exception.ResourceNotFoundException;
import com.shipcore.business.data.entity.Organization;
import com.shipcore.business.data.entity.User;
import com.shipcore.business.data.repository.OrganizationRepository;
import com.shipcore.business.data.repository.UserRepository;
import com.shipcore.business.domain.enums.Role;
import com.shipcore.business.domain.service.AuthService;
import com.shipcore.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("El correo ya esta registrado.");
        }

        Organization organization = organizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organizacion no encontrada."));

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_CLIENT)
                .organization(organization)
                .active(true)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(toUserDetails(user));

        return new AuthResponse(token, "Bearer", "Usuario registrado correctamente.");
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        String token = jwtService.generateToken(toUserDetails(user));

        return new AuthResponse(token, "Bearer", "Inicio de sesion exitoso.");
    }

    private UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }

}
