package com.spmf.auth;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;

@ApplicationScoped
public class RefreshTokenService {

    @Inject
    RefreshTokenRepository repository;

    public RefreshToken create(Long userId) {

        // Revoke every previous refresh token for this user
        repository.update(
                "revoked = true where userId = ?1",
                userId
        );
        
        RefreshToken token = new RefreshToken();

        token.userId = userId;

        token.token =
                RefreshTokenUtil.generate();

        token.createdAt =
                LocalDateTime.now();

        token.expiresAt =
                LocalDateTime.now()
                        .plusDays(30);

        repository.persist(token);

        return token;

    }

}