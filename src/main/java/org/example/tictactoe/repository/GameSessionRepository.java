package org.example.tictactoe.repository;

import org.example.tictactoe.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    List<GameSession> findByPlayerX_UsernameOrPlayerO_Username(String x, String o);
}
