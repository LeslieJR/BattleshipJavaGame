package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;
    private String password;


    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<GamePlayer> gameplayers = new HashSet<>();


    //
    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    private Set<Score> score = new HashSet<>();

    public Player() { }

    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public Set<GamePlayer> getGameplayers() {
        return gameplayers;
    }

    public void setGameplayers(Set<GamePlayer> gameplayers) {
        this.gameplayers = gameplayers;
    }

    public void addGamePlayer(GamePlayer gameplayer) {
        gameplayers.add(gameplayer);
    }

    public long getId() {
        return id;
    }

    public String toString() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Score> getScore() {
        return score;
    }

    public void setScore(Set<Score> score) {
        this.score = score;
    }

    //method to get the score for an specific Game (game_id)
    public Score getScores(Game game) {
        return this.getScore().stream().filter(score -> score.getGame().getId()==game.getId()).findFirst().orElse(null);
    }
}
