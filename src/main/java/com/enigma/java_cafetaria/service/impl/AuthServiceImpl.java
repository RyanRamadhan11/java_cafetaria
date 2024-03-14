package com.enigma.java_cafetaria.service.impl;

import com.enigma.java_cafetaria.constant.ERole;
import com.enigma.java_cafetaria.dto.requets.AuthRequest;
import com.enigma.java_cafetaria.dto.response.LoginResponse;
import com.enigma.java_cafetaria.dto.response.RegisterResponse;
import com.enigma.java_cafetaria.entity.*;
import com.enigma.java_cafetaria.repository.AdminRepository;
import com.enigma.java_cafetaria.repository.KasirRepository;
import com.enigma.java_cafetaria.repository.UserCredentialRepository;
import com.enigma.java_cafetaria.security.JwtUtil;
import com.enigma.java_cafetaria.service.AuthService;
import com.enigma.java_cafetaria.service.CustomerService;
import com.enigma.java_cafetaria.service.RoleService;
import com.enigma.java_cafetaria.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class AuthServiceImpl implements AuthService {

    @PersistenceContext
    private EntityManager entityManager;


    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;


    private final CustomerService customerService;
    private final RoleService roleService;

    private final AdminRepository adminRepository;
    private final KasirRepository kasirRepository;

    private final ValidationUtil validationUtil;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public RegisterResponse registerCustomer(AuthRequest request) {
        try {
            //TODO 1 : Set Role
            String roleId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_role (id, name) VALUES (?, ?)")
                    .setParameter(1, roleId)
                    .setParameter(2, ERole.ROLE_CUSTOMER.name())
                    .executeUpdate();

            //TODO 2 : Set credential
            String userCredentialId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_user_credential (id, username, password, role_id) VALUES (?, ?, ?, ?)")
                    .setParameter(1, userCredentialId)
                    .setParameter(2, request.getUsername())
                    .setParameter(3, passwordEncoder.encode(request.getPassword()))
                    .setParameter(4, roleId)
                    .executeUpdate();

            //TODO 3 : Set customer
            String customerId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_customer (id, user_credential_id, name, address, email, mobile_phone) VALUES (?, ?, ?, ?, ?, ?)")
                    .setParameter(1, customerId)
                    .setParameter(2, userCredentialId)
                    .setParameter(3, request.getCustomerName())
                    .setParameter(4, request.getAddress())
                    .setParameter(5, request.getEmail())
                    .setParameter(6, request.getMobilePhone())
                    .executeUpdate();

            return RegisterResponse.builder()
                    .username(request.getUsername())
                    .role(ERole.ROLE_CUSTOMER.name())
                    .build();

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer already exist");
        }
    }

    @Override
    public RegisterResponse registerKasir(AuthRequest request) {
        try {
            //TODO 1: Set Role
            String roleId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_role (id, name) VALUES (?, ?)")
                    .setParameter(1, roleId)
                    .setParameter(2, ERole.ROLE_KASIR.name())
                    .executeUpdate();

            //TODO 2: Set Credential
            String userCredentialId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_user_credential (id, username, password, role_id) VALUES (?, ?, ?, ?)")
                    .setParameter(1, userCredentialId)
                    .setParameter(2, request.getUsername())
                    .setParameter(3, passwordEncoder.encode(request.getPassword()))
                    .setParameter(4, roleId)
                    .executeUpdate();

            //TODO 3: Set Kasir
            String kasirId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_kasir (id, user_credential_id, name, address, email, phone) VALUES (?, ?, ?, ?, ?, ?)")
                    .setParameter(1, kasirId)
                    .setParameter(2, userCredentialId)
                    .setParameter(3, request.getCustomerName())
                    .setParameter(4, request.getAddress())
                    .setParameter(5, request.getEmail())
                    .setParameter(6, request.getMobilePhone())
                    .executeUpdate();

            return RegisterResponse.builder()
                    .username(request.getUsername())
                    .role(ERole.ROLE_KASIR.name())
                    .build();

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Kasir already exists");
        }
    }


    @Transactional(rollbackOn = Exception.class)
    @Override
    public RegisterResponse registerAdmin(AuthRequest request) {
        try {
            //TODO 1: Set Role
            String roleId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_role (id, name) VALUES (?, ?)")
                    .setParameter(1, roleId)
                    .setParameter(2, ERole.ROLE_ADMIN.name())
                    .executeUpdate();

            //TODO 2: Set Credential
            String userCredentialId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_user_credential (id, username, password, role_id) VALUES (?, ?, ?, ?)")
                    .setParameter(1, userCredentialId)
                    .setParameter(2, request.getUsername())
                    .setParameter(3, passwordEncoder.encode(request.getPassword()))
                    .setParameter(4, roleId)
                    .executeUpdate();

            //TODO 3: Set Admin
            String adminId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "INSERT INTO m_admin (id, name, email, phone, user_credential_id) VALUES (?, ?, ?, ?, ?)")
                    .setParameter(1, adminId)
                    .setParameter(2, request.getUsername())
                    .setParameter(3, request.getEmail())
                    .setParameter(4, request.getMobilePhone())
                    .setParameter(5, userCredentialId)
                    .executeUpdate();

            return RegisterResponse.builder()
                    .username(request.getUsername())
                    .role(ERole.ROLE_ADMIN.name())
                    .build();

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin already exists");
        }
    }


    @Override
    public LoginResponse login(AuthRequest authRequest) {
        //tempat untuk logic login
        validationUtil.validate(authRequest);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authRequest.getUsername().toLowerCase(),
                authRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUser appUser = (AppUser) authentication.getPrincipal();
        String token = jwtUtil.generateToken(appUser);

        return LoginResponse.builder()
                .token(token)
                .role(appUser.getRole().name())
                .build();
    }
}
