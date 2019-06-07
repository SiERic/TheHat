package me.sieric.thehat.logic.data;

import lombok.Getter;
import lombok.Setter;

/**
 * Data class for players
 * Contains player name and the number of guessed/explained words
 */
@Getter
@Setter
public class Player {

    private String name;
    private int explained;
    private int guessed;

    public Player(String name) {
        this.name = name;
    }

    public Player(String name, int guessed, int explained) {
        this.name = name;
        this.guessed = guessed;
        this.explained = explained;
    }
}
