package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ScoreRepository scoreRepository;

    @RequestMapping("/games")

    public List<Object> getAll() {
        return gameRepository
                .findAll()
                .stream()
                .map(game -> {
                    return makeGameDTO(game);
                })
                .collect(toList());
        // Assume that returns a Set
        // Now we can use the Set.stream() method to get a string, then the stream map() and collect() methods to create and return a list of DTO objects
    }

    @RequestMapping("/game_view/{gp_Id}")

    public Map<String, Object> getGameView(@PathVariable Long gp_Id) {
        GamePlayer gamePlayer = gamePlayerRepository.getOne(gp_Id);
        Game game = gamePlayer.getGame();
        return GameDTO(game, gamePlayer);
    }


    @RequestMapping("/scores")

    public List<Map<String,List>> getScores(){

        return playerRepository.findAll().stream().map(player -> scoreDTO(player)).collect(toList());
    }

    private Map<String,List> scoreDTO(Player player) {
        Map<String,List> dto = new HashMap<>();
        dto.put(
                player.getUserName(),
                player
                        .getScores()
                        .stream()
                        .map(Score::getPoints)     //here because the score is an object we need to call the method getScore
                        .collect(Collectors.toList()));
        return dto;

    }





    //This says:
    //if GET /api is received


    //call the model method GameRepository.findAll()
    //and return the list of Game instances it returns

    //Add a method to get a list of game IDs:
    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        //We use a LinkedHashMap so that we can control the order of keys in the map.
        dto.put("id", game.getId());
        dto.put("creation", game.getCreationDate());
        //For the value for the gamePlayers key, create a List with a Map for each GamePlayer.
        //In the Map for each GamePlayer, put keys and values for the GamePlayer ID and the player.
        //For the value of the player, create a Map with keys for the player ID and the player's email.
        dto.put("gamePlayers", game.getGameplayers()
                                .stream()
                                .map (gamePlayer -> gameplayerDTO(gamePlayer))
                                .collect(toList()));

        return dto;
    }

    private Map<String, Object> gameplayerDTO(GamePlayer gamePlayer){
        Map<String, Object>  dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", playerDTO(gamePlayer.getPlayer()));
     return dto;
    }


    private Map<String, Object> playerDTO(Player player){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }

    private Map<String, Object> GameDTO(Game game, GamePlayer gamePlayer) {  //important: we pass two parameters the game and the gameplayer
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("creation", game.getCreationDate());
        dto.put("gamePlayers", game.getGameplayers()
                .stream()
                .map (gp -> gameplayerDTO(gp))        //for one gameplayer, the one that is the current player
                .collect(Collectors.toList()));
        dto.put("ships", gamePlayer.getShips()        //here for the gameplayer we have passed we look for his ships type and locations
                                   .stream()
                                   .map(ship -> ShipDTO(ship))
                                   .collect(toList()));
        dto.put("salvoes", gamePlayer.getSalvoes()                 //we can see the shots the current player has fired
                                      .stream()
                                      .map(salvo -> SalvoDTO(salvo))
                                      .collect(toList()));
        dto.put("oppSalvoes", oppPlayer(gamePlayer).getSalvoes()
                                                   .stream()
                                                   .map(salvo -> SalvoDTO(salvo))
                                                   .collect(toList()));

        //we also need to know the hits on the ships of the current player
        return dto;
    }

    private Map<String, Object> ShipDTO(Ship ship){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", ship.getId());
        dto.put("type", ship.getShiptype());
        dto.put("locations", ship.getLocations());
        return dto;
    }

    private Map<Long, Map<Integer, List<String>>> SalvoDTO(Salvo salvo) {
        Map<Long, Map<Integer, List<String>>> dto = new LinkedHashMap<>();
        Map<Integer, List<String>> turn = new HashMap<>();
        turn.put(salvo.getTurn(), salvo.getLocations());
        dto.put(salvo.getGamePlayer().getId(), turn);
        return dto;
    }

    private GamePlayer oppPlayer(GamePlayer gamePlayer){
        return gamePlayer.getGame().getGameplayers().stream()
                                                    .filter(gp->gp.getId()!= gamePlayer.getId())
                                                    .findFirst().orElse(null);
    }









}
