package br.com.carlos.artist_manager_api.infrastructure.config;

import br.com.carlos.artist_manager_api.domain.service.JwtService;
import br.com.carlos.artist_manager_api.infrastructure.security.RateLimitFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        .requestMatchers(HttpMethod.POST, "/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/v1/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/v1/**").hasAnyRole("ADMIN", "USER")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://meu-front.com"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private OncePerRequestFilter jwtFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(@NonNull HttpServletRequest request,
                                            @NonNull HttpServletResponse response,
                                            @NonNull FilterChain filterChain) throws ServletException, IOException {
                final String authHeader = request.getHeader("Authorization");
                final String jwt;
                final String username;

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    filterChain.doFilter(request, response);
                    return;
                }

                jwt = authHeader.substring(7);
                try {
                    username = jwtService.extractUsername(jwt);
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        if (jwtService.isTokenValid(jwt, username)) {

                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }
                } catch (Exception e) {

                }
                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}