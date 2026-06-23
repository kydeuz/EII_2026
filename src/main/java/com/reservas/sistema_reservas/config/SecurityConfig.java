package com.reservas.sistema_reservas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/*.html", "/css/**", "/js/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/reset-password").permitAll()
                .requestMatchers("/api/reportes/**").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.POST, "/api/reservas").hasAnyRole("ALUMNO", "DOCENTE", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/historial").hasAnyRole("ALUMNO", "DOCENTE", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/laboratorios").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.PUT, "/api/laboratorios/**").hasAnyRole("ADMIN", "DOCENTE")
                .requestMatchers(HttpMethod.DELETE, "/api/laboratorios/**").hasAnyRole("ADMIN", "DOCENTE")
                // Gestión de usuarios solo ADMIN
                .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}