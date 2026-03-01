package com.wizeflow.crm.backend.controller;

import com.wizeflow.crm.backend.controller.dto.LoginRequest;
import com.wizeflow.crm.backend.controller.dto.LoginResponse;
import com.wizeflow.crm.backend.controller.dto.RefreshTokenRequest;
import com.wizeflow.crm.backend.controller.dto.RegisterRequest;
import com.wizeflow.crm.backend.infrastructure.entity.Company;
import com.wizeflow.crm.backend.infrastructure.entity.User;
import com.wizeflow.crm.backend.infrastructure.repository.CompanyRepository;
import com.wizeflow.crm.backend.infrastructure.repository.UserRepository;
import com.wizeflow.crm.backend.security.config.JwtProperties;
import com.wizeflow.crm.backend.security.service.JwtUtil;
import com.wizeflow.crm.backend.enums.Role;
import com.wizeflow.crm.backend.enums.UserStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        log.info("Login attempt for email: {}", request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            if (userDetails == null) {
                log.error("Authentication succeeded but principal is null for email {}", request.getEmail());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
            }
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));


            Tokens tokens = generateTokens(userDetails);


            LoginResponse response = buildLoginResponse(user, tokens);

            log.info("User {} logged in successfully", user.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {


            log.error("Authentication failed for email {}", request.getEmail(), e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());


        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }


        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));


        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                // Force default role to USER to prevent privilege escalation via client-supplied role
                .role(Role.USER)
                .company(company)
                .phone(request.getPhone())
                .cpf(request.getCpf())
                .jobTitle(request.getJobTitle())
                .department(request.getDepartment())
                // Use enum for status instead of magic string
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);
        log.info("User {} registered successfully", user.getEmail());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Tokens tokens = generateTokens(userDetails);

        LoginResponse response = buildLoginResponse(user, tokens);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {

            String refreshToken = request.getRefreshToken();


            String userEmail = jwtUtil.extractUsername(refreshToken);


            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtUtil.validateToken(refreshToken, userDetails)) {


                User user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));


                Tokens tokens = generateTokens(userDetails);


                LoginResponse response = buildLoginResponse(user, tokens);

                log.info("Token refreshed for user {}", userEmail);
                return ResponseEntity.ok(response);

            } else {

                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido");
            }

        } catch (Exception e) {


            log.error("Token refresh failed", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido ou expirado");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {


        log.info("User logged out");


        return ResponseEntity.noContent().build();
    }

    private Tokens generateTokens(UserDetails userDetails) {
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        return new Tokens(accessToken, refreshToken);
    }

    private LoginResponse buildLoginResponse(User user, Tokens tokens) {
        return LoginResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration())
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                        .build())
                .build();
    }
    private static final class Tokens {
        private final String accessToken;
        private final String refreshToken;

        private Tokens(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        private String accessToken() {
            return accessToken;
        }

        private String refreshToken() {
            return refreshToken;
        }
    }
}
