package com.example.myproject.repository;

import com.example.myproject.entity.GameStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GameStatsRepository extends JpaRepository<GameStatsEntity, Long> {
    Optional<GameStatsEntity> findByUserUsername(String username);
}
