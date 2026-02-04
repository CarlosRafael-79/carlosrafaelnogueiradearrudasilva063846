package br.com.carlos.artist_manager_api.api.controller;

import br.com.carlos.artist_manager_api.api.dto.AuthResponse;
import br.com.carlos.artist_manager_api.api.dto.LoginRequest;
import br.com.carlos.artist_manager_api.api.dto.RefreshTokenRequest;
import br.com.carlos.artist_manager_api.domain.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        String accessToken = jwtService.generateToken(auth.getName());
        String refreshToken = jwtService.generateRefreshToken(auth.getName());

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        String token = request.refreshToken();
        String username = jwtService.extractUsername(token);

        if (jwtService.isTokenValid(token, username)) {
            String newAccessToken = jwtService.generateToken(username);
            return ResponseEntity.ok(new AuthResponse(newAccessToken, token));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}