package me.sieric.thehat.logic;

public class Player {
    public String name;
    public int explained;
    public int guessed;
    public Player(String name) {
        this.name = name;
    }

    public Player(String name, int guessed, int explained) {
        this.name = name;
        this.guessed = guessed;
        this.explained = explained;
    }
}
