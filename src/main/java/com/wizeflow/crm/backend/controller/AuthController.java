package com.wizeflow.crm.backend.controller;

import com.wizeflow.crm.backend.controller.dto.LoginRequest;
import com.wizeflow.crm.backend.controller.dto.LoginResponse;
import com.wizeflow.crm.backend.controller.dto.RefreshTokenRequest;
import com.wizeflow.crm.backend.controller.dto.RegisterRequest;
import com.wizeflow.crm.backend.infrastructure.entity.User;
import com.wizeflow.crm.backend.infrastructure.repository.UserRepository;
import com.wizeflow.crm.backend.security.config.JwtProperties;
import com.wizeflow.crm.backend.security.service.CustomUserDetails;
import com.wizeflow.crm.backend.security.service.JwtUtil;
import com.wizeflow.crm.backend.enums.Role;
import com.wizeflow.crm.backend.enums.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        // Log at DEBUG to avoid leaking PII in production; mask email in logs when INFO needed
        String maskedEmail = maskEmail(request.getEmail());
        log.debug("Login attempt for email: {}", maskedEmail);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Avoid redundant DB lookup: principal is CustomUserDetails (returned by CustomUserDetailsService)
            Object principal = authentication.getPrincipal();
            User userEntity;
            if (principal instanceof CustomUserDetails) {
                userEntity = ((CustomUserDetails) principal).getUser();
            } else {
                // Fallback: load from repository
                userEntity = userRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            if (userDetails == null) {
                log.error("Authentication succeeded but principal is null for email {}", request.getEmail());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
            }

            Tokens tokens = generateTokens(userDetails);

            LoginResponse response = buildLoginResponse(userEntity, tokens);

            log.info("User {} logged in successfully", userEntity.getEmail());
            return ResponseEntity.ok(response);

        } catch (org.springframework.security.core.AuthenticationException ae) {
            log.warn("Authentication failed for {}: {}", maskedEmail, ae.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
        } catch (ResponseStatusException rse) {
            // propagate application-specific responses (e.g., 404)
            throw rse;
        } catch (Exception e) {
            log.error("Unexpected error during authentication for {}: {}", maskedEmail, e.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        String maskedEmail = maskEmail(request.getEmail());
        log.debug("Registration attempt for email: {}", maskedEmail);


        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }


        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                // Force default role to USER to prevent privilege escalation via client-supplied role
                .role(Role.USER)
                // No company association during public registration to prevent arbitrary linking
                .company(null)
                .phone(request.getPhone())
                .cpf(request.getCpf())
                .jobTitle(request.getJobTitle())
                .department(request.getDepartment())
                // Use enum for status instead of magic string
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: {}", maskEmail(user.getEmail()));

        // Use CustomUserDetails to avoid double load during immediate token generation
        UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(user.getEmail());
        Tokens tokens = generateTokens(userDetails);

        LoginResponse response = buildLoginResponse(user, tokens);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {

            String refreshToken = request.getRefreshToken();

            // Ensure token type is refresh
            String typ = jwtUtil.extractTokenType(refreshToken);
            if (typ == null || !typ.equalsIgnoreCase("refresh")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido");
            }

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

        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        // logout no longer blocks tokens (blocklist removed)
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
    private record Tokens(String accessToken, String refreshToken) {}

    private String maskEmail(String email) {
        if (email == null || email.isBlank()) return "<unknown>";
        int at = email.indexOf('@');
        if (at <= 1) return "***@" + (at > 1 ? email.substring(at + 1) : "***");
        String prefix = email.substring(0, Math.min(2, at));
        return prefix + "***@" + email.substring(at + 1);
    }
 }
