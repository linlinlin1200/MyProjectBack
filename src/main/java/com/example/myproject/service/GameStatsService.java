package com.example.myproject.service;

import com.example.myproject.entity.GameStatsEntity;
import com.example.myproject.entity.UserEntity;
import com.example.myproject.repository.GameStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameStatsService {

    private final GameStatsRepository gameStatsRepository;

    // Создание статистики для нового пользователя (score = 0)
    public GameStatsEntity createStatsForUser(UserEntity user) {
        GameStatsEntity stats = GameStatsEntity.builder()
                .user(user)
                .score(0)
                .build();
        return gameStatsRepository.save(stats);
    }

    // Инкремент очков (score) для пользователя с указанным username
    public GameStatsEntity incrementScore(String username) {
        GameStatsEntity stats = gameStatsRepository.findByUserUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("GameStats not found for user " + username));
        stats.setScore(stats.getScore() + 1);
        return gameStatsRepository.save(stats);
    }

    // Получение статистики по username
    public GameStatsEntity getStatsByUsername(String username) {
        return gameStatsRepository.findByUserUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("GameStats not found for user " + username));
    }
}
