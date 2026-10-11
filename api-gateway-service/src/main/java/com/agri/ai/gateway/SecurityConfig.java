package com.agri.ai.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/", "/index.html", "/assets/**").permitAll()
                .anyExchange().authenticated()
            )
            .httpBasic(basic -> {});
        return http.build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService(
            @Value("${app.security.username:farmer}") String username,
            @Value("${app.security.password:change-this-dev-password}") String password) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new MapReactiveUserDetailsService(
                User.withUsername(username).password(encoder.encode(password)).roles("FARMER").build());
    }
}
