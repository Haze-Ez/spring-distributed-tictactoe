package org.example.tictactoe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class GameMove {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private GameSession game;

    private int position;
    private String player;
    private LocalDateTime playedAt;
    private int moveNumber;
}
