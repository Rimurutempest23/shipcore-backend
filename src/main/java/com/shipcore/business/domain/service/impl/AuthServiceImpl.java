package com.shipcore.business.domain.service.impl;

import com.shipcore.business.api.dto.request.LoginRequest;
import com.shipcore.business.api.dto.request.RegisterRequest;
import com.shipcore.business.api.dto.response.AuthResponse;
import com.shipcore.business.api.dto.response.OrganizationResponse;
import com.shipcore.business.api.dto.response.UserResponse;
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
                .role(Role.ROLE_OPERATOR)
                .organization(organization)
                .active(true)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getOrganization() != null ? user.getOrganization().getId() : null,
                user.getRole() != null ? user.getRole().name() : "ROLE_OPERATOR"
        );

        return new AuthResponse(
                token,
                "Bearer",
                "Usuario registrado correctamente.",
                toUserResponse(user),
                toOrganizationResponse(organization)
        );
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

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getOrganization() != null ? user.getOrganization().getId() : null,
                user.getRole() != null ? user.getRole().name() : "ROLE_OPERATOR"
        );

        return new AuthResponse(
                token,
                "Bearer",
                "Inicio de sesion exitoso.",
                toUserResponse(user),
                toOrganizationResponse(user.getOrganization())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
        return toUserResponse(user);
    }

    private UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getProfile() == null ? null : user.getProfile().getPhone(),
                user.getProfile() == null ? null : user.getProfile().getAddress(),
                user.getProfile() == null ? null : user.getProfile().getBio(),
                user.getActive(),
                user.getOrganization().getId(),
                user.getOrganization().getName()
        );
    }

    private OrganizationResponse toOrganizationResponse(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getRuc(),
                organization.getAddress(),
                organization.getPhone(),
                organization.getCountry(),
                organization.getPlan(),
                organization.getSoftLimit(),
                organization.getHardLimit(),
                organization.getCurrentUsage(),
                organization.getCreatedAt(),
                organization.getActive()
        );
    }

}
