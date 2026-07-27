package com.kapil.jobtracker.auth.service;

import com.kapil.jobtracker.auth.dto.AuthResponse;
import com.kapil.jobtracker.auth.dto.LoginRequest;
import com.kapil.jobtracker.auth.refresh.dto.LogoutRequest;
import com.kapil.jobtracker.auth.refresh.dto.RefreshTokenRequest;
import com.kapil.jobtracker.auth.refresh.dto.RefreshTokenResponse;
import com.kapil.jobtracker.auth.refresh.entity.RefreshToken;
import com.kapil.jobtracker.auth.refresh.service.RefreshTokenService;
import com.kapil.jobtracker.security.jwt.JwtService;
import com.kapil.jobtracker.security.service.CurrentUserService;
import com.kapil.jobtracker.security.service.CustomUserDetailsService;
import com.kapil.jobtracker.user.entity.User;
import com.kapil.jobtracker.user.exception.UserNotFoundException;
import com.kapil.jobtracker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public AuthResponse login(LoginRequest request){
        String email = request.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                token,
                refreshToken.getToken(),
                "Bearer");
    }

    public RefreshTokenResponse refreshAccessToken(RefreshTokenRequest request){
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

        User user = refreshToken.getUser();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        String newAccessToken = jwtService.generateToken(userDetails);

        return new RefreshTokenResponse(
                newAccessToken,
                "Bearer"
        );
    }

    public void logout(LogoutRequest request){
        refreshTokenService.revokeRefreshToken(request.getRefreshToken());
    }

    public void logoutFromAllDevices(){
        User currentUser = currentUserService.getCurrentUser();
        refreshTokenService.revokeAllRefreshTokens(currentUser);
    }
}
