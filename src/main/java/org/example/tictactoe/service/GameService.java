package org.example.tictactoe.service;

import org.example.tictactoe.AppUser;
import org.example.tictactoe.model.Game;
import org.example.tictactoe.repository.AppUserRepository;
import org.example.tictactoe.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final AppUserRepository userRepository;

    public GameService(GameRepository gameRepository, AppUserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public List<Game> findGamesForUser(String username) {
        return gameRepository.findByPlayerX_UsernameOrPlayerO_Username(username, username);
    }

    public Game createNewGameForUser(String username, boolean vsCpu, String difficulty) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Game game = new Game();
        game.initialize();

        game.setPlayerX(user);
        game.setVsCpu(vsCpu);
        game.setCreatedAt(LocalDateTime.now());

        // Validation for difficulty
        if (difficulty != null && !difficulty.isEmpty()) {
            game.setDifficulty(difficulty.toUpperCase());
        } else {
            game.setDifficulty("HARDER"); // Default
        }

        if (vsCpu) {
            game.setStatus("IN_PROGRESS");
            game.setPlayerO(null);
        } else {
            game.setStatus("WAITING");
            game.setPlayerO(null);
        }

        return gameRepository.save(game);
    }

    public Game getGame(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Game ID cannot be null");
        }
        return gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));
    }

    public Game makeMove(Long gameId, int position, String username) {
        Game game = getGame(gameId);

        if (!isPlayerAllowedToMove(game, username)) {
            throw new RuntimeException("You are not allowed to move in this game.");
        }

        boolean moved = game.makeMove(position);
        if (!moved) {
            return game;
        }

        if (game.isVsCpu()
                && "o".equalsIgnoreCase(game.getCurrentPlayer())
                && game.getWinner() == null) {

            int maxDepth;
            int cpuPos = -1;
            String difficulty = game.getDifficulty();

            if ("EASY".equalsIgnoreCase(difficulty)) {
                // EASY: Just pick the first open spot
                int firstEmpty = findFirstEmpty(game.getBoard());
                if (firstEmpty != -1) {
                    game.makeMove(firstEmpty);
                    // We already moved
                    cpuPos = -1;
                }
            } else {
                if ("HARDER".equalsIgnoreCase(difficulty)) {
                    maxDepth = 2;
                } else {
                    maxDepth = -1; // IMPOSSIBLE
                }
                cpuPos = findBestMove(game.getBoard(), maxDepth);
            }

            if (cpuPos >= 0) {
                game.makeMove(cpuPos);
            }
        }

        if (game.getWinner() != null && !game.getWinner().isEmpty()) {
            game.setStatus("FINISHED");

            if ("x".equalsIgnoreCase(game.getWinner())) {
                addWin(game.getPlayerX());
                addLoss(game.getPlayerO());
            } else if ("o".equalsIgnoreCase(game.getWinner())) {
                addWin(game.getPlayerO());
                addLoss(game.getPlayerX());
            } else if ("Draw".equalsIgnoreCase(game.getWinner())) {
                addTie(game.getPlayerX());
                addTie(game.getPlayerO());
            }

        } else {
            game.setStatus("IN_PROGRESS");
        }

        return gameRepository.save(game);
    }

    public Game undoMove(Long gameId, String username) {
        Game game = getGame(gameId);

        if (!game.isVsCpu()) {
            throw new RuntimeException("Undo is only allowed in Player vs CPU mode.");
        }

        if (!belongsToUser(game, username)) {
            throw new RuntimeException("You are not allowed to undo moves in this game.");
        }

        game.undoMove();

        if (game.isVsCpu() && "o".equalsIgnoreCase(game.getCurrentPlayer())) {
            game.undoMove();
        }

        game.setStatus("IN_PROGRESS");
        return gameRepository.save(game);
    }

    // Compatibility methods
    public Game createNewGame() {
        Game game = new Game();
        game.initialize();
        return gameRepository.save(game);
    }

    public Game makeMove(Long gameId, int position) {
        Game game = getGame(gameId);
        game.makeMove(position);
        return gameRepository.save(game);
    }

    public Game undoMove(Long gameId) {
        Game game = getGame(gameId);
        game.undoMove();
        return gameRepository.save(game);
    }

    // Helpers
    private boolean isPlayerAllowedToMove(Game game, String username) {
        if (game.isVsCpu()) {
            if (game.getPlayerX() == null)
                return false;
            return "x".equalsIgnoreCase(game.getCurrentPlayer()) &&
                    game.getPlayerX().getUsername().equals(username);
        }

        if (game.getPlayerX() != null && game.getPlayerO() != null) {
            String current = game.getCurrentPlayer();
            if ("x".equalsIgnoreCase(current)) {
                return game.getPlayerX().getUsername().equals(username);
            } else {
                return game.getPlayerO().getUsername().equals(username);
            }
        }

        if (game.getPlayerX() != null) {
            return game.getPlayerX().getUsername().equals(username);
        }

        return false;
    }

    private boolean belongsToUser(Game game, String username) {
        return (game.getPlayerX() != null && game.getPlayerX().getUsername().equals(username)) ||
                (game.getPlayerO() != null && game.getPlayerO().getUsername().equals(username));
    }

    private void addWin(AppUser user) {
        if (user == null)
            return;
        user.setWins(user.getWins() + 1);
        user.setGamesPlayed(user.getGamesPlayed() + 1);
        userRepository.save(user);
    }

    private void addLoss(AppUser user) {
        if (user == null)
            return;
        user.setLosses(user.getLosses() + 1);
        user.setGamesPlayed(user.getGamesPlayed() + 1);
        userRepository.save(user);
    }

    private void addTie(AppUser user) {
        if (user == null)
            return;
        user.setTies(user.getTies() + 1);
        user.setGamesPlayed(user.getGamesPlayed() + 1);
        userRepository.save(user);
    }

    public int findBestMove(List<String> board, int maxDepth) {
        int bestVal = -1000;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (board.get(i).equals("-")) {
                board.set(i, "o");
                int moveVal = minimax(board, 0, false, maxDepth);
                board.set(i, "-");

                if (moveVal > bestVal) {
                    bestMove = i;
                    bestVal = moveVal;
                }
            }
        }
        return bestMove;
    }

    public int findBestMove(List<String> board) {
        return findBestMove(board, -1);
    }

    private int minimax(List<String> board, int depth, boolean isMax, int maxDepth) {
        int score = evaluate(board);

        if (score == 10)
            return score - depth;
        if (score == -10)
            return score + depth;
        if (!board.contains("-"))
            return 0;

        if (maxDepth != -1 && depth >= maxDepth) {
            return 0;
        }

        if (isMax) {
            int best = -1000;
            for (int i = 0; i < 9; i++) {
                if (board.get(i).equals("-")) {
                    board.set(i, "o");
                    best = Math.max(best, minimax(board, depth + 1, false, maxDepth));
                    board.set(i, "-");
                }
            }
            return best;
        } else {
            int best = 1000;
            for (int i = 0; i < 9; i++) {
                if (board.get(i).equals("-")) {
                    board.set(i, "x");
                    best = Math.min(best, minimax(board, depth + 1, true, maxDepth));
                    board.set(i, "-");
                }
            }
            return best;
        }
    }

    private int evaluate(List<String> board) {
        int[][] lines = {
                { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 },
                { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 },
                { 0, 4, 8 }, { 2, 4, 6 }
        };
        for (int[] line : lines) {
            if (board.get(line[0]).equals(board.get(line[1])) &&
                    board.get(line[1]).equals(board.get(line[2]))) {
                if (board.get(line[0]).equals("o"))
                    return 10;
                if (board.get(line[0]).equals("x"))
                    return -10;
            }
        }
        return 0;
    }

    private int findFirstEmpty(List<String> board) {
        for (int i = 0; i < board.size(); i++) {
            if ("-".equals(board.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public void joinGame(Long gameId, String username) {
        Game game = getGame(gameId);

        if (!"WAITING".equals(game.getStatus())) {
            throw new RuntimeException("Game is not open for joining.");
        }

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (game.getPlayerX() != null &&
                game.getPlayerX().getUsername().equals(username)) {
            throw new RuntimeException("You cannot join your own game as O.");
        }

        game.setPlayerO(user);
        game.setStatus("IN_PROGRESS");
        gameRepository.save(game);
    }

    public List<Game> findOpenGames(String username) {
        List<Game> waiting = gameRepository.findByStatus("WAITING");

        waiting.removeIf(g -> g.getPlayerX() != null &&
                g.getPlayerX().getUsername().equals(username));

        return waiting;
    }
}
