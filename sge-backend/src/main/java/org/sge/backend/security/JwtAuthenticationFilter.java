package org.sge.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            var token = header.substring(7);
            if (jwtService.isValid(token)) {
                var username = jwtService.extractUsername(token);
                var roles = jwtService.extractRoles(token);
                var permissions = jwtService.extractPermissions(token);

                var authorities = new ArrayList<SimpleGrantedAuthority>();
                // Add roles with ROLE_ prefix for hasRole() checks
                for (var role : roles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
                // Add fine-grained permissions for hasAuthority() checks
                for (var perm : permissions) {
                    authorities.add(new SimpleGrantedAuthority(perm));
                }

                var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(request, response);
    }
}
