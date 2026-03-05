package com.wizeflow.crm.backend.security.service;

import com.wizeflow.crm.backend.infrastructure.entity.User;
import com.wizeflow.crm.backend.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.debug("Loading user by email: {}", email);



        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));



        CustomUserDetails details = new CustomUserDetails(user, Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        ));

        return details;

    }
}
