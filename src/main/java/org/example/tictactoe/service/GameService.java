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


    // 1️⃣ List all games a user is involved in
    public List<Game> findGamesForUser(String username) {
        return gameRepository.findByPlayerX_UsernameOrPlayerO_Username(username, username);
    }

    // 2️⃣ Create new game where current user is X
    public Game createNewGameForUser(String username, boolean vsCpu) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Game game = new Game();
        game.initialize();

        game.setPlayerX(user);  // creator always plays as X
        game.setVsCpu(vsCpu);
        game.setCreatedAt(LocalDateTime.now());

        if (vsCpu) {
            // human vs CPU: game can start immediately
            game.setStatus("IN_PROGRESS");
            game.setPlayerO(null); // CPU is logical O
        } else {
            // PvP: wait for another human player to join as O
            game.setStatus("WAITING");
            game.setPlayerO(null);
        }

        return gameRepository.save(game);
    }


    // 3️⃣ Fetch a game safely
    public Game getGame(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found"));
    }

    // 4️⃣ Make a move (user-aware)
    public Game makeMove(Long gameId, int position, String username) {
        Game game = getGame(gameId);

        // check permission
        if (!isPlayerAllowedToMove(game, username)) {
            throw new RuntimeException("You are not allowed to move in this game.");
        }

        boolean moved = game.makeMove(position); // human move
        if (!moved) {
            return game;
        }

        // if vs CPU and now it's CPU's turn and game not finished
        if (game.isVsCpu()
                && "o".equalsIgnoreCase(game.getCurrentPlayer())
                && game.getWinner() == null) {

            int cpuPos = findFirstEmpty(game.getBoard());
            if (cpuPos >= 0) {
                game.makeMove(cpuPos);
            }
        }

        // status + stats
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


    // 5️⃣ Undo move (Restricted to CPU mode to prevent PvP trolling)
    public Game undoMove(Long gameId, String username) {
        Game game = getGame(gameId);

        // --- SECURITY CHECK ---
        // Only allow undo if it is a Single Player game
        if (!game.isVsCpu()) {
            throw new RuntimeException("Undo is only allowed in Player vs CPU mode.");
        }


        if (!belongsToUser(game, username)) {
            throw new RuntimeException("You are not allowed to undo moves in this game.");
        }

        game.undoMove();

        // If playing against CPU, we usually want to undo TWICE (the CPU's move AND the Player's move)
        // so the player can retry.
        // Check if the undo left it as 'o' (CPU) turn. If so, undo one more time to get back to 'x'.
        if (game.isVsCpu() && "o".equalsIgnoreCase(game.getCurrentPlayer())) {
            game.undoMove();
        }

        game.setStatus("IN_PROGRESS");
        return gameRepository.save(game);
    }

    // ===== Compatibility methods (so old calls don’t break) =====

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

    // ===== Helper logic =====

    private boolean isPlayerAllowedToMove(Game game, String username) {
        // vs CPU: only X can move, only on X turn
        if (game.isVsCpu()) {
            if (game.getPlayerX() == null) return false;
            return "x".equalsIgnoreCase(game.getCurrentPlayer()) &&
                    game.getPlayerX().getUsername().equals(username);
        }

        // normal PvP: both players must exist
        if (game.getPlayerX() != null && game.getPlayerO() != null) {
            String current = game.getCurrentPlayer();
            if ("x".equalsIgnoreCase(current)) {
                return game.getPlayerX().getUsername().equals(username);
            } else {
                return game.getPlayerO().getUsername().equals(username);
            }
        }

        // fallback: only X exists yet
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
        if (user == null) return;
        user.setWins(user.getWins() + 1);
        user.setGamesPlayed(user.getGamesPlayed() + 1);
        userRepository.save(user);
    }

    private void addLoss(AppUser user) {
        if (user == null) return;
        user.setLosses(user.getLosses() + 1);
        user.setGamesPlayed(user.getGamesPlayed() + 1);
        userRepository.save(user);
    }

    private void addTie(AppUser user) {
        if (user == null) return;
        user.setTies(user.getTies() + 1);
        user.setGamesPlayed(user.getGamesPlayed() + 1);
        userRepository.save(user);
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
        // all games that are WAITING
        List<Game> waiting = gameRepository.findByStatus("WAITING");

        //filter out my own games so I don't "join" myself
        waiting.removeIf(g ->
                g.getPlayerX() != null &&
                        g.getPlayerX().getUsername().equals(username)
        );

        return waiting;
    }



}
