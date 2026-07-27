package com.kapil.jobtracker.auth.controller;

import com.kapil.jobtracker.auth.dto.AuthResponse;
import com.kapil.jobtracker.auth.dto.LoginRequest;
import com.kapil.jobtracker.auth.refresh.dto.LogoutRequest;
import com.kapil.jobtracker.auth.refresh.dto.RefreshTokenRequest;
import com.kapil.jobtracker.auth.refresh.dto.RefreshTokenResponse;
import com.kapil.jobtracker.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request){
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request){
        authService.logout(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> lougoutFromAllDevices(){
        authService.logoutFromAllDevices();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
