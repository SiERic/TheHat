package me.sieric.thehat.logic.data;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Player {

    private String name;
    private int explained;
    private int guessed;
    private int id;

    public Player(String name) {
        this.name = name;
    }

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Player(String name, int guessed, int explained) {
        this.name = name;
        this.guessed = guessed;
        this.explained = explained;
    }
}
