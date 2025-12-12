package org.example.tictactoe.repository;

import org.example.tictactoe.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game,Long> {
    List<Game> findByPlayerX_UsernameOrPlayerO_Username(String x, String o);
    List<Game> findByStatus(String status);

}
