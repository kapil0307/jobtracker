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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request = mock(LoginRequest.class);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        User user = new User();
        user.setEmail("user@example.com");

        RefreshToken refreshToken = RefreshToken.builder()
                .token("test-refresh-token")
                .user(user)
                .build();

        when(request.getEmail())
                .thenReturn(" User@Example.com ");

        when(request.getPassword())
                .thenReturn("password123");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("test-access-token");

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(refreshTokenService.createRefreshToken(user))
                .thenReturn(refreshToken);

        AuthResponse response = authService.login(request);

        assertThat(response.getAccessToken())
                .isEqualTo("test-access-token");

        assertThat(response.getRefreshToken())
                .isEqualTo("test-refresh-token");

        assertThat(response.getTokenType())
                .isEqualTo("Bearer");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundDuringLogin() {

        LoginRequest request = mock(LoginRequest.class);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getEmail())
                .thenReturn("user@example.com");

        when(request.getPassword())
                .thenReturn("password123");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("test-access-token");

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenLoginCredentialsAreInvalid() {

        LoginRequest request = mock(LoginRequest.class);

        when(request.getEmail())
                .thenReturn("user@example.com");

        when(request.getPassword())
                .thenReturn("wrong-password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void shouldRefreshAccessTokenSuccessfully() {

        RefreshTokenRequest request = mock(RefreshTokenRequest.class);
        UserDetails userDetails = mock(UserDetails.class);

        User user = new User();
        user.setEmail("user@example.com");

        RefreshToken refreshToken = RefreshToken.builder()
                .token("valid-refresh-token")
                .user(user)
                .revoked(false)
                .build();

        when(request.getRefreshToken())
                .thenReturn("valid-refresh-token");

        when(refreshTokenService.validateRefreshToken("valid-refresh-token"))
                .thenReturn(refreshToken);

        when(customUserDetailsService.loadUserByUsername("user@example.com"))
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("new-access-token");

        RefreshTokenResponse response =
                authService.refreshAccessToken(request);

        assertThat(response.getAccessToken())
                .isEqualTo("new-access-token");

        assertThat(response.getTokenType())
                .isEqualTo("Bearer");
    }

    @Test
    void shouldLogoutSuccessfully() {

        LogoutRequest request = mock(LogoutRequest.class);

        when(request.getRefreshToken())
                .thenReturn("refresh-token");

        authService.logout(request);

        verify(refreshTokenService)
                .revokeRefreshToken("refresh-token");
    }

    @Test
    void shouldLogoutFromAllDevicesSuccessfully() {

        User currentUser = new User();
        currentUser.setEmail("user@example.com");

        when(currentUserService.getCurrentUser())
                .thenReturn(currentUser);

        authService.logoutFromAllDevices();

        verify(refreshTokenService)
                .revokeAllRefreshTokens(currentUser);
    }
}