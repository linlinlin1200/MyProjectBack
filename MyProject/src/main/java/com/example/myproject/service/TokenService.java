package com.example.myproject.service;

import com.example.myproject.entity.TokenEntity;
import com.example.myproject.entity.UserEntity;
import com.example.myproject.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final long TOKEN_VALIDITY_SECONDS = 7 * 24 * 60 * 60;

    public TokenEntity createTokenForUser(UserEntity user) {
        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(TOKEN_VALIDITY_SECONDS);
        TokenEntity tokenEntity = TokenEntity.builder()
                .token(tokenValue)
                .expiryDate(expiryDate)
                .user(user)
                .build();
        return tokenRepository.save(tokenEntity);
    }

    public TokenEntity findByToken(String token) {
        return tokenRepository.findByToken(token).orElse(null);
    }

    public void removeToken(String token) {
        tokenRepository.findByToken(token).ifPresent(tokenRepository::delete);
    }
}
