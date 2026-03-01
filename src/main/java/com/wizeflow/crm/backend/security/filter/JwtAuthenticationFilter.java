package com.wizeflow.crm.backend.security.filter;

import com.wizeflow.crm.backend.security.service.CustomUserDetailsService;
import com.wizeflow.crm.backend.security.service.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {

            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authHeader.substring(7).trim();

            if (jwt.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            // Reject tokens that are not access tokens (prevent using refresh tokens as Bearer)
            String typ = jwtUtil.extractTokenType(jwt);
            if (typ == null || !typ.equalsIgnoreCase("access")) {
                log.warn("Non-access token used as bearer: typ={}", typ);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String userEmail = jwtUtil.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (jwtUtil.validateToken(jwt, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("User {} authenticated successfully", userEmail);
                }

            }

        } catch (Exception e) {

            log.error("Cannot set user authentication: {}", e.getMessage());
        }


        filterChain.doFilter(request, response);
    }
}
