package com.example.myproject.controller;

import com.example.myproject.entity.UserEntity;
import com.example.myproject.service.GameStatsService;
import com.example.myproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameStatsService gameStatsService;

    @GetMapping
    public ResponseEntity<?> gameInfo(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        String username = principal.getName();
        var stats = gameStatsService.getStatsByUsername(username);
        return ResponseEntity.ok(Map.of(
                "username", username,
                "score", stats.getScore()
        ));
    }

    // Эндпоинт для увеличения score при правильном ответе
    @PostMapping("/incrementScore")
    public ResponseEntity<Void> incrementScore(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        String username = principal.getName();
        gameStatsService.incrementScore(username);
        return ResponseEntity.ok().build();
    }
}
