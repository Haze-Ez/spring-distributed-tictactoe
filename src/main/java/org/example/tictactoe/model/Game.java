package org.example.tictactoe.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tictactoe.AppUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Game {
    private static final int BOARD_SIZE = 9;
    private static final String EMPTY_CELL = "-";
    private static final String PLAYER_X = "x";
    private static final String PLAYER_O = "o";
    private static final String DRAW = "Draw";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // who’s playing
    @ManyToOne
    private AppUser playerX;
    @ManyToOne
    private AppUser playerO;
    private String status; // NEW, IN_PROGRESS, FINISHED
    private LocalDateTime createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> moveHistory = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> board = new ArrayList<>();

    private boolean vsCpu;

    private String currentPlayer;
    private String winner;
    private String difficulty; // "EASY", "HARDER", "IMPOSSIBLE"

    public void initialize() {
        board.clear();
        for (int i = 0; i < BOARD_SIZE; i++) {
            board.add(EMPTY_CELL);
        }
        moveHistory.clear();
        currentPlayer = PLAYER_X;
        winner = null;

        // new stuff
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "NEW";
        }
        // Initialize difficulty
        if (difficulty == null) {
            difficulty = "HARDER";
        }
    }

    public boolean makeMove(int position) {
        if (!isValidMove(position)) {
            return false;
        }

        board.set(position, currentPlayer);
        moveHistory.add(position);

        if (isWin()) {
            winner = currentPlayer;
            status = "FINISHED";
        } else if (isDraw()) {
            winner = DRAW;
            status = "FINISHED";
        } else {
            switchPlayer();
            status = "IN_PROGRESS";
        }

        return true;
    }

    private boolean isValidMove(int position) {
        if (position < 0 || position >= BOARD_SIZE) {
            return false;
        }
        if (!board.get(position).equals(EMPTY_CELL)) {
            return false;
        }
        if (winner != null) {
            return false;
        }
        return true;
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer.equals(PLAYER_X) ? PLAYER_O : PLAYER_X;
    }

    public boolean isWin() {
        int[][] lines = {
                { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 },
                { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 },
                { 0, 4, 8 }, { 2, 4, 6 }
        };

        for (int[] line : lines) {
            if (!board.get(line[0]).equals(EMPTY_CELL) &&
                    board.get(line[0]).equals(board.get(line[1])) &&
                    board.get(line[1]).equals(board.get(line[2]))) {
                return true;
            }
        }
        return false;
    }

    public boolean isDraw() {
        return !board.contains(EMPTY_CELL) && !isWin();
    }

    public void undoMove() {
        if (moveHistory.isEmpty()) {
            return;
        }
        int lastPosition = moveHistory.remove(moveHistory.size() - 1);
        board.set(lastPosition, EMPTY_CELL);
        winner = null;
        switchPlayer();
        status = "IN_PROGRESS";
    }

    public String getCharAt(int position) {
        if (position < 0 || position >= BOARD_SIZE) {
            throw new IllegalArgumentException("Invalid board position: " + position);
        }
        return board.get(position);
    }
}
