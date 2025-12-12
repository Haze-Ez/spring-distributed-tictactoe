package org.example.tictactoe.controller;

import org.example.tictactoe.model.Game;
import org.example.tictactoe.repository.AppUserRepository;
import org.example.tictactoe.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    @Autowired
    private AppUserRepository userRepo;


    public GameController(GameService gameService, AppUserRepository userRepo) {
        this.gameService = gameService;
        this.userRepo = userRepo;
    }

    // 1) list my games
    @GetMapping
    public String listMyGames(Model model, Principal principal) {
        String username = principal.getName();

        List<Game> myGames = gameService.findGamesForUser(username);
        List<Game> openGames = gameService.findOpenGames(username);

        model.addAttribute("games", myGames);
        model.addAttribute("openGames", openGames);
        return "games";
    }


    // 2) create new game (I am X)
    @PostMapping("/new")
    public String newGame(@RequestParam(required = false) Boolean cpu, Principal principal) {
        Game game = gameService.createNewGameForUser(principal.getName(), cpu != null && cpu);
        return "redirect:/game/" + game.getId();
    }

    @PostMapping("/join/{gameId}")
    public String joinGame(@PathVariable Long gameId, Principal principal) {
        gameService.joinGame(gameId, principal.getName());
        return "redirect:/game/" + gameId;
    }

    // 3) view one game (old home() basically)
    @GetMapping("/{id}")
    public String viewGame(@PathVariable Long id, Model model, Principal principal) {
        Game game = gameService.getGame(id);
        model.addAttribute("gameId", game.getId());
        model.addAttribute("game", game);
        model.addAttribute("players", userRepo.findTop10ByOrderByWinsDesc());
        if (principal != null) {
            model.addAttribute("currentUser", principal.getName());
        }
        return "game";
    }


    // 4) move (old code, just scoped to a game)
    @PostMapping("/move/{gameId}/{cell}")
    @ResponseBody
    public Map<String, Object> move(@PathVariable Long gameId, @PathVariable int cell, Principal principal) {
        Game game = gameService.makeMove(gameId, cell, principal.getName());

        Map<String, Object> map = new HashMap<>();
        map.put("board", game.getBoard());
        map.put("currentPlayer", game.getCurrentPlayer());
        map.put("winner", game.getWinner());
        return map;
    }

    // 5) undo stays
    @PostMapping("/undo/{gameId}")
    @ResponseBody
    public Map<String, Object> undo(@PathVariable Long gameId, Principal principal) {
        Game game = gameService.undoMove(gameId, principal.getName());

        Map<String, Object> map = new HashMap<>();
        map.put("board", game.getBoard());
        map.put("currentPlayer", game.getCurrentPlayer());
        map.put("winner", game.getWinner());
        return map;
    }

    @GetMapping("/state/{gameId}")
    @ResponseBody
    public Map<String, Object> gameState(@PathVariable Long gameId) {
        Game game = gameService.getGame(gameId);

        Map<String, Object> map = new HashMap<>();
        map.put("board", game.getBoard());
        map.put("currentPlayer", game.getCurrentPlayer());
        map.put("winner", game.getWinner());
        map.put("status", game.getStatus());
        return map;
    }


    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {
        model.addAttribute("players", userRepo.findTop10ByOrderByWinsDesc());
        return "leaderboard";
    }

    @GetMapping("/leaderboard-data")
    @ResponseBody
    public List<Map<String, Object>> leaderboardData() {
        return userRepo.findTop10ByOrderByWinsDesc()
                .stream()
                .map(u -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("username", u.getUsername());
                    m.put("wins", u.getWins());
                    m.put("losses", u.getLosses());
                    m.put("ties", u.getTies());
                    m.put("gamesPlayed", u.getGamesPlayed());
                    return m;
                })
                .toList();
    }


    @GetMapping("/puzzle")
    public String puzzlePage() { return "puzzle"; }

    @GetMapping("/Awaiting")
    public String toBeDeterminedPage() { return "Awaiting"; }


    @GetMapping("/mode")
    public String modePage() {
        return "mode";
    }

    //Get for mode redirection
    @GetMapping("/new")
    public String newGameGet(@RequestParam(required = false) Boolean cpu, Principal principal) {
        boolean vsCpu = cpu != null && cpu;
        Game game = gameService.createNewGameForUser(principal.getName(), vsCpu);
        return "redirect:/game/" + game.getId();
    }


}
