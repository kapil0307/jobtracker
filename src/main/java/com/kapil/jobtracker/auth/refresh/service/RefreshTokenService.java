package com.kapil.jobtracker.auth.refresh.service;

import com.kapil.jobtracker.auth.refresh.entity.RefreshToken;
import com.kapil.jobtracker.auth.refresh.exception.RefreshTokenExpiredException;
import com.kapil.jobtracker.auth.refresh.exception.RefreshTokenNotFoundException;
import com.kapil.jobtracker.auth.refresh.exception.RefreshTokenRevokedException;
import com.kapil.jobtracker.auth.refresh.repository.RefreshTokenRepository;
import com.kapil.jobtracker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepo;

    @Value("${app.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public RefreshToken createRefreshToken(User user){

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiration))
                .revoked(false)
                .user(user)
                .build();

        return refreshTokenRepo.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(RefreshToken refreshToken){
        if(refreshToken.isRevoked()){
            throw new RefreshTokenRevokedException("Refresh token has been revoked");
        }
        if(refreshToken.getExpiresAt().isBefore(Instant.now())){
            throw new RefreshTokenExpiredException("Refresh token has been expired");
        }
        return refreshToken;
    }

    public RefreshToken getByToken(String token){
        return refreshTokenRepo.findByToken(token)
                .orElseThrow(()-> new RefreshTokenNotFoundException("Refresh token not found")
                );
    }

    public RefreshToken validateRefreshToken(String token){
        RefreshToken refreshToken = getByToken(token);
        return verifyRefreshToken(refreshToken);
    }

    public void revokeRefreshToken(String token){
        RefreshToken refreshToken = getByToken(token);
        refreshToken.setRevoked(true);
        refreshTokenRepo.save(refreshToken);
    }

    public void revokeAllRefreshTokens(User user){
        List<RefreshToken> activeTokens = refreshTokenRepo.findAllByUserAndRevokedFalse(user);
        activeTokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepo.saveAll(activeTokens);
    }
}
