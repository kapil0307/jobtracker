package com.kapil.jobtracker.security.service;

import com.kapil.jobtracker.auth.refresh.entity.RefreshToken;
import com.kapil.jobtracker.auth.refresh.exception.RefreshTokenExpiredException;
import com.kapil.jobtracker.auth.refresh.exception.RefreshTokenNotFoundException;
import com.kapil.jobtracker.auth.refresh.exception.RefreshTokenRevokedException;
import com.kapil.jobtracker.auth.refresh.repository.RefreshTokenRepository;
import com.kapil.jobtracker.auth.refresh.service.RefreshTokenService;
import com.kapil.jobtracker.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepo;
    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldReturnRefreshTokenWhenTokenExists(){

        RefreshToken refreshToken = RefreshToken.builder()
                .token("test-refresh-token")
                .revoked(false)
                .build();

        when(refreshTokenRepo.findByToken("test-refresh-token"))
                .thenReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.getByToken("test-refresh-token");
        assertThat(result).isEqualTo(refreshToken);
    }

    @Test
    void shouldThrowExceptionWhenTokenDoesNotExist(){
        when(refreshTokenRepo.findByToken("missing-token"))
                .thenReturn(Optional.empty());
        assertThatThrownBy(
                ()->refreshTokenService.getByToken("missing-token")
        )
                .isInstanceOf(RefreshTokenNotFoundException.class);

    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenIsRevoked() {
        RefreshToken refreshToken=RefreshToken.builder()
                .token("revoked-token")
                .revoked(true)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        assertThatThrownBy(
                ()-> refreshTokenService.verifyRefreshToken(refreshToken)
        )
                .isInstanceOf(RefreshTokenRevokedException.class);
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenIsExpired() {
        RefreshToken refreshToken = RefreshToken.builder()
                .token("expired-token")
                .revoked(false)
                .expiresAt(Instant.now().minusSeconds(60))
                .build();
        assertThatThrownBy(
                ()-> refreshTokenService.verifyRefreshToken(refreshToken)
        )
                .isInstanceOf(RefreshTokenExpiredException.class);
    }

    @Test
    void shouldReturnTokenWhenRefreshTokenIsValid(){
        RefreshToken refreshToken = RefreshToken.builder()
                .token("valid-token")
                .revoked(false)
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
        RefreshToken result = refreshTokenService.verifyRefreshToken(refreshToken);
        assertThat(result).isEqualTo(refreshToken);
    }

    @Test
    void shouldRevokeRefreshToken(){
        RefreshToken refreshToken = RefreshToken.builder()
                .token("active-token")
                .revoked(false)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(refreshTokenRepo.findByToken("active-token"))
                .thenReturn(Optional.of(refreshToken));

        refreshTokenService.revokeRefreshToken("active-token");

        assertThat(refreshToken.isRevoked()).isTrue();

        verify(refreshTokenRepo).save(refreshToken);
    }

    @Test
    void shouldRevokeAllActiveRefreshTokensForUser(){
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();

        RefreshToken tokenOne = RefreshToken.builder()
                .token("token-one")
                .revoked(false)
                .user(user)
                .build();

        RefreshToken tokenTwo = RefreshToken.builder()
                .token("token-two")
                .revoked(false)
                .user(user)
                .build();

        List<RefreshToken> activeTokens = List.of(tokenOne, tokenTwo);

        when(refreshTokenRepo.findAllByUserAndRevokedFalse(user))
                .thenReturn(activeTokens);

        refreshTokenService.revokeAllRefreshTokens(user);

        assertThat(tokenOne.isRevoked()).isTrue();
        assertThat(tokenTwo.isRevoked()).isTrue();

        verify(refreshTokenRepo).saveAll(activeTokens);
    }

}
