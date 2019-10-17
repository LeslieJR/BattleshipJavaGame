package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;



@RestController

@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private ShipRepository shipRepository;
    @Autowired
    private SalvoRepository salvoRepository;


    //Add a create player method to the application controller (createUser) 'registration method'
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createUser(@RequestParam String userName, @RequestParam String password) {
        if (userName.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No userName"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.findByUserName(userName);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.CONFLICT);
        }
        Player newPlayer = playerRepository.save(new Player(userName, passwordEncoder.encode(password))); //the password needs to be encoded!!
        return new ResponseEntity<>(makeMap("userName", newPlayer.getUserName()), HttpStatus.CREATED);
    }

    //makeMap
    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
    //

    //Add a create game method to the application controller (createGame)
    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        if (authentication == null) {
            return new ResponseEntity<>(makeMap("error", "You have to be logged in"), HttpStatus.FORBIDDEN);
        }
        Game newGame = new Game();
        gameRepository.save(newGame);
        GamePlayer newGamePlayer = new GamePlayer(getLoggedUser(authentication), newGame);
        gamePlayerRepository.save(newGamePlayer);
        return new ResponseEntity<>(makeMap("gpId", newGamePlayer.getId()), HttpStatus.CREATED); //*****
    }

    //Add a join game method to the application controller (joinGame)
    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication, @PathVariable Long gameId) {
        Game createdGame = gameRepository.getOne(gameId);
        Player userPlayer = getLoggedUser(authentication);
        // this 1st IF checks if the createdGame exists or not
        if (createdGame == null) {
            return new ResponseEntity<>(makeMap("error", "this game does not exist"), HttpStatus.FORBIDDEN);
        }
        //this 2nd IF checks if the userPlayer exists (meaning that there is a user logged in) or not
        if (userPlayer == null) {
            return new ResponseEntity<>(makeMap("error", "you have to be logged in to join a game"), HttpStatus.UNAUTHORIZED);
        }
        //this 3rd IF checks if the game is already full (more than one gamePlayer)
        if (createdGame.getGameplayers().size() > 1) {
            return new ResponseEntity<>(makeMap("error", "this game is full"), HttpStatus.FORBIDDEN);
        }
        //in this for we compare the id of the players for the gamePlayers of this 'created' game are equal, so the userPlayer is already in the game, but if not a new gamePlayer is created
        for (GamePlayer gameplayer : createdGame.getGameplayers()) {
            if (gameplayer.getId() == userPlayer.getId()) {
                return new ResponseEntity<>(makeMap("error", "You are already in this game"), HttpStatus.FORBIDDEN);
            }
        }
        //here if the any of the IF statements are true, the newGP is created with the userPlayer(user logged in) and the game that is already created
        GamePlayer newGp = new GamePlayer(userPlayer, createdGame);
        gamePlayerRepository.save(newGp);
        return new ResponseEntity<>(makeMap("gpId", newGp.getId()), HttpStatus.CREATED);
    }

    //Add current user information to the JSON games object
    //If there is a user logged in, then authentication.getName() will return the name that was put into the UserDetails object
    private Player getLoggedUser(Authentication authentication) {
        return playerRepository.findByUserName(authentication.getName());
    }

    @RequestMapping("/games")
    public Map<String, Object> getAll(Authentication authentication) {
        Map<String, Object> playerObj = new HashMap<>();
        if (authentication != null) {
            playerObj.put("player", playerDTO(getLoggedUser(authentication))); //here we can use the getLoggedUser return name
        } else {
            playerObj.put("player", "guest");
        }
        playerObj.put("games", gameRepository
                .findAll()
                .stream()
                .map(game -> {
                    return makeGameDTO(game);
                })
                .collect(toList()));
        // Assume that returns a Set
        // Now we can use the Set.stream() method to get a string, then the stream map() and collect() methods to create and return a list of DTO objects
        return playerObj;
    }

    @RequestMapping("/game_view/{gp_Id}")
    public Map<String, Object> getGameView(@PathVariable Long gp_Id) {
        GamePlayer gamePlayer = gamePlayerRepository.getOne(gp_Id);
        Game game = gamePlayer.getGame();

        return GameDTO(game, gamePlayer);
    }

    @RequestMapping("/scores")
    public List<Map<String, Object>> getScores() {
        return playerRepository.findAll().stream().map(player -> scoreDTO(player)).collect(toList());
    }

    @RequestMapping(path = "/games/players/{gp_Id}/salvos", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> listOfSalvos(Authentication authentication, @PathVariable Long gp_Id, @RequestBody List<String> locations) {
        GamePlayer currentGP = gamePlayerRepository.getOne(gp_Id);
        int currentTurn = currentGP.getSalvoes().size() + 1;
        Player userPlayer = getLoggedUser(authentication);
        if (authentication == null) {
            return new ResponseEntity<>(makeMap("error", "there is no user logged in"), HttpStatus.UNAUTHORIZED);
        }
        if (currentGP == null) {
            return new ResponseEntity<>(makeMap("error", " there is no game player with the given ID"), HttpStatus.FORBIDDEN);
        }
        if (userPlayer.getId() != currentGP.getPlayer().getId()) {
            return new ResponseEntity<>(makeMap("error", "the current user is not the game player the ID references"), HttpStatus.UNAUTHORIZED);
        }


        Salvo newSalvo = new Salvo(currentGP, locations, currentTurn);
        salvoRepository.save(newSalvo);
        return new ResponseEntity<>(makeMap("New Salvo created", newSalvo.getId()), HttpStatus.CREATED);
    }

    // ****Implement a controller method for a list of placed ships
    @RequestMapping(path = "/games/players/{gp_Id}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> listOfShips(Authentication authentication, @PathVariable Long gp_Id, @RequestBody List<Ship> ships) {
        GamePlayer currentGP = gamePlayerRepository.getOne(gp_Id);

        if (authentication == null) {
            return new ResponseEntity<>(makeMap("error", "there is no user logged in"), HttpStatus.UNAUTHORIZED);
        }
        if (currentGP == null) {
            return new ResponseEntity<>(makeMap("error", " there is no game player with the given ID"), HttpStatus.FORBIDDEN);
        }
        if (getLoggedUser(authentication).getId() != currentGP.getPlayer().getId()) {
            return new ResponseEntity<>(makeMap("error", "the current user is not the game player the ID references"), HttpStatus.UNAUTHORIZED);
        }
        if (gamePlayerRepository.getOne(gp_Id).getShips().size() != 0 || gamePlayerRepository.getOne(gp_Id).getShips().size() == 5) {
            return new ResponseEntity<>(makeMap("error", "you already have ships placed"), HttpStatus.FORBIDDEN);
        }
        if (ships.size() == 0 || ships.size() < 5) {
            return new ResponseEntity<>(makeMap("error", "You have to place all the ships"), HttpStatus.FORBIDDEN);
        }
        //****the game player should be set to the ship and the ship should be saved to the repo, and a Created response should be sent.
        ships.forEach(ship -> {
            System.out.println(ship);
            ship.setGamePlayer(currentGP);
            shipRepository.save(ship);
        });
        return new ResponseEntity<>(makeMap("ok", "the ships are created"), HttpStatus.CREATED);
    }


    //***********************************************************************
    private Map<String, Object> shipsOpp(GamePlayer gamePlayer) {
        Map<String, Object> hits = new LinkedHashMap<>();
        GamePlayer oppPlayer = oppPlayer(gamePlayer);

        if (oppPlayer(gamePlayer) != null) {
            Set<Ship> ships = oppPlayer.getShips();
            //for the Set :forEach
            for (Ship ship : ships) {
                hits.put(ship.getShiptype(), hitsDTO(gamePlayer, ship)); //here we send the ship to the hitsDTO
            }
        }
        return hits;
    }

    private Map<Integer, Map<String, Object>> hitsDTO(GamePlayer gamePlayer, Ship ship) {
        Map<Integer, Map<String, Object>> dto = new LinkedHashMap<>();

        Set<Salvo> salvos = gamePlayer.getSalvoes().stream().sorted((s1, s2) -> s2.getTurn().compareTo(s1.getTurn())).collect(Collectors.toSet()); //is necessary to sort salvos based on turn
        ArrayList<String> locations = new ArrayList<>();
        locations.addAll(ship.getLocations());

        //forEach salvo in salvos
        for (Salvo salvo : salvos) {
            Map<String, Object> infodto = new LinkedHashMap<>();
            ArrayList<String> positions = new ArrayList<>();
            ArrayList<String> salvosLoc = new ArrayList<>();
            salvosLoc.addAll(salvo.getLocations());

            for (String position : salvosLoc) {
                if (locations.contains(position)) {
                    positions.add(position);

                }
            }
            infodto.put("hits", positions);
            dto.put(salvo.getTurn(), infodto);
        }
        return dto;

    }

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
                .sorted((a, b) -> {             // to sort the game player by id in ascending order
                    return (int) (a.getId() - b.getId());
                })
                .map(gamePlayer -> gameplayerDTO(gamePlayer))
                .collect(toList()));

        return dto;
    }

    private Map<String, Object> scoreDTO(Player player) {
        Map<String, Object> calc = new HashMap<>();
        calc.put("player", player.getUserName());
        calc.put("total", player.getScore().stream().map(Score::getScore).reduce(
                0.0,
                (a, b) -> a + b));
        calc.put("wins", player.getScore().stream().map(Score::getScore).filter(point -> point.equals(1.0)).count());
        calc.put("loses", player.getScore().stream().map(Score::getScore).filter(point -> point.equals(0.0)).count());
        calc.put("ties", player.getScore().stream().map(Score::getScore).filter(point -> point.equals(0.5)).count());
        return calc;

    }
    private Map<String, Object> gameplayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", playerDTO(gamePlayer.getPlayer()));
        return dto;
    }


    private Map<String, Object> playerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", player.getId());
        dto.put("email", player.getUserName());
        return dto;
    }

    private Map<String, Object> GameDTO(Game game, GamePlayer gamePlayer) {  //important: we pass two parameters the game and the gameplayer
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", game.getId());
        dto.put("creation", game.getCreationDate());
        dto.put("status", StatusDTO(gamePlayer, game));
        dto.put("gamePlayers", game.getGameplayers()
                .stream()
                .map(gp -> gameplayerDTO(gp))   //for one gameplayer, the one that is the current player
                .collect(Collectors.toList()));

        dto.put("ships", gamePlayer.getShips()        //here for the gameplayer we have passed we look for his ships type and locations
                .stream()
                .map(ship -> ShipDTO(ship))
                .collect(Collectors.toList()));
        dto.put("salvos", gamePlayer.getSalvoes()                 //we can see the shots the current player has fired
                .stream()
                .map(salvo -> SalvoDTO(salvo))
                .collect(Collectors.toList()));
        if (oppPlayer(gamePlayer) != null) {
            dto.put("oppSalvos", oppPlayer(gamePlayer).getSalvoes()
                    .stream()
                    .map(salvo -> SalvoDTO(salvo))
                    .collect(Collectors.toList()));
            dto.put("hits", shipsOpp(gamePlayer));

            return dto;
        }


        //we also need to know the hits on the ships of the current player
        return dto;
    }

    private Map<String, Object> ShipDTO(Ship ship) {
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

    private GamePlayer oppPlayer(GamePlayer gamePlayer) {
        return gamePlayer.getGame().getGameplayers().stream()
                .filter(gp -> gp.getId() != gamePlayer.getId())
                .findFirst().orElse(null);
    }

    private String StatusDTO(GamePlayer gamePlayer, Game game) {
        String status = new String();
        Game currentgame = gamePlayer.getGame();
        Integer players = game.getGameplayers().size();
        //to order gameplayers by id (the gp(0) is who has created the game)
        List<GamePlayer> ordgp = game.getGameplayers().stream().sorted((gp1, gp2) -> gp1.getId().compareTo(gp2.getId())).collect(Collectors.toList());
        Integer ships = gamePlayer.getShips().size();
        Integer oppships = oppPlayer(gamePlayer).getShips().size();

        //salvos contains all the positions the gameplayer has fired
        List<String> salvos = gamePlayer.getSalvoes().stream().flatMap(salvo -> salvo.getLocations().stream()).collect(toList());
        //shiploc contains all the locations of the gameplayer's ships
        List<String> shiploc = gamePlayer.getShips().stream().flatMap(ship -> ship.getLocations().stream()).collect(toList());

        //opploc contains all the locations of the opponent's ships
        List<String> opploc = oppPlayer(gamePlayer).getShips().stream().flatMap(ship -> ship.getLocations().stream()).collect(toList());
        //oppsalvos contains all the positions the opponent has fired
        List<String> oppsalvos = oppPlayer(gamePlayer).getSalvoes().stream().flatMap(salvo -> salvo.getLocations().stream()).collect(toList());

        if (players == 1) {
            status = "Waiting for opponent";

        }
        //System.out.println("gp1 "+ordgp.get(0).getId() + " gp2"+ordgp.get(1).getId());
        else if (ships == 0 && oppships == 0) {
            status = "Already two players, place your ships";

        }
        else if (ships != 0 && oppships == 0) {
            status = "Waiting for the ships of the opponent";

        }
       else if (ships == 0 && oppships != 0) {
            status = "Place your ships";

        }

        //no problem
        //Game over
       else if((ordgp.get(1).getSalvoes().size() == ordgp.get(0).getSalvoes().size()) && (oppsalvos.containsAll(shiploc) || salvos.containsAll(opploc))) {
            if ((oppsalvos.containsAll(shiploc) && salvos.containsAll(opploc))) {
                status = "Tie";
                setScores(gamePlayer, 0.5, 0.5);

            }else if (oppsalvos.containsAll(shiploc)) {
                status = "You have lost the game";
                setScores(gamePlayer, 0.0, 1.0);

            }
            else {
                status = "You have won the game";
                setScores(gamePlayer, 1.0, 0.0);

            }

        } else {

            if (ordgp.get(0).getSalvoes().size() == 0 && gamePlayer.getPlayer().equals(ordgp.get(0).getPlayer())) {
                status = "Waiting for your salvos";

            }
           else  if (ordgp.get(0).getSalvoes().size() == 0 && gamePlayer.getPlayer().equals(ordgp.get(1).getPlayer())) {
                status = "Waiting for salvos of the opponent";

            }

           else  if (ordgp.get(1).getSalvoes().size() == ordgp.get(0).getSalvoes().size() && gamePlayer.getPlayer().equals(ordgp.get(0).getPlayer())) {
                status = "Waiting for your salvos";


            }
            else if (ordgp.get(1).getSalvoes().size() == ordgp.get(0).getSalvoes().size() && gamePlayer.getPlayer().equals(ordgp.get(1).getPlayer())) {
                status = "Waiting for the opponent salvos";

            }
           else if (ordgp.get(1).getSalvoes().size() < ordgp.get(0).getSalvoes().size() && gamePlayer.getPlayer().equals(ordgp.get(1).getPlayer())) {
                status = "Send salvos";

            }

            else if (ordgp.get(1).getSalvoes().size() < ordgp.get(0).getSalvoes().size() && gamePlayer.getPlayer().equals(ordgp.get(0).getPlayer())) {
                status = "Waiting for opponent salvos";
            }
        }

        return status;

    }
    private void setScores(GamePlayer gamePlayer, double gpScore, double oppScore){
        if (gamePlayer.getPlayer().getScores(gamePlayer.getGame())==null && oppPlayer(gamePlayer).getPlayer().getScores(gamePlayer.getGame())==null) {
            Score scoreGp = new Score(gamePlayer.getPlayer(), gamePlayer.getGame(), gpScore);
            Score scoreOpp = new Score(oppPlayer(gamePlayer).getPlayer(), gamePlayer.getGame(), oppScore);
            scoreRepository.save(scoreGp);
            scoreRepository.save(scoreOpp);
        }
    }




}
//






