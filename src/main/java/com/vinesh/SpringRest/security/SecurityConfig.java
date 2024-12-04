package com.vinesh.SpringRest.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.JOSEException;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // private final RsaKeyProperties rsaKeys;
    private RSAKey rsaKey;

    @Bean
    JWKSource<SecurityContext> jwkSource() {
        rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // @Bean
    // public InMemoryUserDetailsManager users() {
    // return new InMemoryUserDetailsManager(
    // User.withUsername("vinesh")
    // .password("{noop}password")
    // .authorities("read")
    // .build());
    // }

    @Bean
    AuthenticationManager authManager(UserDetailsService userDetailsService) {
        var authProvider = new DaoAuthenticationProvider();// Data Access Object(DAO)
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(authProvider);
    }

    // jwt encoder and jwt decoder are involed in part of implementing jwt
    // authentication
    // Encoder->Create JWT
    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwks) {
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    JwtDecoder jwtDecoder() throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/db-console/**")) // Disable CSRF
                // for H2 console
                .headers(headers -> headers.frameOptions(options -> options.sameOrigin())) // Allow frames from
                // same origin
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/token").permitAll()
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/auth/users/add").permitAll()
                        .requestMatchers("/auth/users").hasAuthority("SCOPE_ADMIN")
                        .requestMatchers("/auth/users/{user_id}/update-authorities").hasAuthority("SCOPE_ADMIN")
                        .requestMatchers("/auth/profile").authenticated()
                        .requestMatchers("/auth/profile/update-password").authenticated()
                        .requestMatchers("/auth/profile/delete").authenticated()
                        .requestMatchers("/album/{album_id}/upload-photos").authenticated()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/db-console/**").permitAll() // Allow access to the H2 console
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/test").authenticated())
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Additional configuration
        http.csrf(csrf -> csrf.disable()); // Disable CSRF globally if needed
        http.headers(headers -> headers.frameOptions(options -> options.disable())); // Disable frame options globally
                                                                                     // if needed

        return http.build();
    }

}
