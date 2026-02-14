package org.example.tictactoe.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game();
        game.initialize();
    }

    @Test
    void testInitialize() {
        assertNotNull(game.getBoard());
        assertEquals(9, game.getBoard().size());
        for (String cell : game.getBoard()) {
            assertEquals("-", cell);
        }
        assertEquals("x", game.getCurrentPlayer());
        assertNull(game.getWinner());
        assertEquals("NEW", game.getStatus());
        assertNotNull(game.getCreatedAt());
        // Verify default difficulty
        assertEquals("HARDER", game.getDifficulty());
    }

    @Test
    void testMakeMoveValid() {
        boolean result = game.makeMove(0);
        assertTrue(result);
        assertEquals("x", game.getBoard().get(0));
        assertEquals("o", game.getCurrentPlayer()); // Player should switch
        assertEquals("IN_PROGRESS", game.getStatus());
    }

    @Test
    void testMakeMoveInvalidPosition() {
        assertFalse(game.makeMove(-1));
        assertFalse(game.makeMove(9));
    }

    @Test
    void testMakeMoveOccupiedPosition() {
        game.makeMove(0);
        boolean result = game.makeMove(0); // Try to move to the same spot
        assertFalse(result);
        assertEquals("x", game.getBoard().get(0)); // Should still be x
        assertEquals("o", game.getCurrentPlayer()); // Player should not have switched back
    }

    @Test
    void testWinRow() {
        // x o x
        // x o x
        // x . .
        game.makeMove(0); // x
        game.makeMove(1); // o
        game.makeMove(3); // x
        game.makeMove(4); // o
        game.makeMove(6); // x wins

        assertTrue(game.isWin());
        assertEquals("x", game.getWinner());
        assertEquals("FINISHED", game.getStatus());
    }

    @Test
    void testWinCol() {
        // x x x
        // o o .
        // . . .
        game.makeMove(0); // x
        game.makeMove(3); // o
        game.makeMove(1); // x
        game.makeMove(4); // o
        game.makeMove(2); // x wins

        assertTrue(game.isWin());
        assertEquals("x", game.getWinner());
    }

    @Test
    void testWinDiagonal() {
        // x o .
        // . x o
        // . . x
        game.makeMove(0); // x
        game.makeMove(1); // o
        game.makeMove(4); // x
        game.makeMove(5); // o
        game.makeMove(8); // x wins

        assertTrue(game.isWin());
        assertEquals("x", game.getWinner());
    }

    @Test
    void testDraw() {
        // x o x
        // x o x
        // o x o
        game.makeMove(0); // x
        game.makeMove(1); // o
        game.makeMove(2); // x
        game.makeMove(4); // o
        game.makeMove(3); // x
        game.makeMove(5); // o
        game.makeMove(7); // x
        game.makeMove(6); // o
        game.makeMove(8); // x - Wait, looking at logic:
        // 0(x), 1(o), 2(x)
        // 3(x), 4(o), 5(o) -- if 5 is o
        // 6(o), 7(x), 8(o)

        // Let's force a draw state manually to avoid complex play simulation errors
        // x o x
        // x o x
        // o x o
        List<String> drawBoard = List.of(
                "x", "o", "x",
                "x", "o", "x",
                "o", "x", "o");
        game.setBoard(drawBoard);
        // We need to ensure logic doesn't think it's a win
        // x o x
        // x o x -> col 1: x,x,o ; col 2: o,o,x; col 3: x,x,o. No col win.
        // o x o
        // rows: xox, xox, oxo. No row win.
        // diag: x,o,o ; x,o,o. No diag win.

        assertTrue(game.isDraw());
        assertFalse(game.isWin());
    }
}
