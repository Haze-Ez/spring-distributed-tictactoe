package org.example.tictactoe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.example.tictactoe.AppUser;

import java.time.LocalDateTime;

@Entity
public class GameSession {
    @Id
    @GeneratedValue
    private Long id;

    private String board;         // "X,O, , , , , , , "
    private String currentPlayer; // "X" or "O"
    private String status;        // NEW, IN_PROGRESS, FINISHED
    private String winner;

    @ManyToOne
    private AppUser playerX;

    @ManyToOne
    private AppUser playerO;

    private LocalDateTime createdAt;
}
