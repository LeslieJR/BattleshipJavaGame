package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private String shiptype;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    //@ElementCollection is used to create lists of embeddable objects. An embeddable object is data intended for use only in the object containing it.
    @ElementCollection
    @Column(name="locations")
    private List<String> locations = new ArrayList<>();

    public Ship() { }

    public Ship (String shiptype, List<String> locations, GamePlayer gamePlayer){
        this.shiptype = shiptype;
        this.gamePlayer = gamePlayer;
        this.locations = locations;
        gamePlayer.addShip(this);  //really important line of code
    }

    public Long getId() {
        return id;
    }

    public String getShiptype() {
        return shiptype;
    }

    public void setShiptype(String shiptype) {
        this.shiptype = shiptype;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }


    public List<String> getLocations() {
        return locations;
    }
}
