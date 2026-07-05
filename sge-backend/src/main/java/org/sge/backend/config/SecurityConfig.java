package org.sge.backend.config;

import lombok.RequiredArgsConstructor;
import org.sge.backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**", "/actuator/health").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/roles/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/roles/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/roles/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/permisos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/permisos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/permisos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/transiciones-estado/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/transiciones-estado/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/transiciones-estado/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/periodos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/periodos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/periodos/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
