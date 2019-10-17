package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date creationDate;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<GamePlayer> gameplayers = new HashSet<>();


    //
    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();


    public Game() {
        this.creationDate = new Date();
    }

    public long getId() {
        return id;
    }

    public Set<GamePlayer> getGameplayers() {
        return gameplayers;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date date){
        this.creationDate = date;
}
    public void addGamePlayer(GamePlayer gameplayer) {
        gameplayers.add(gameplayer);
    }






}
