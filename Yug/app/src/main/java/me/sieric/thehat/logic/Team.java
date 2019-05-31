package me.sieric.thehat.logic;

import lombok.Getter;

@Getter
public class Team {

    private String firstPlayerName;
    private String secondPlayerName;
    private int firstPlayerExplained;
    private int secondPlayerExplained;

    public Team(String firstPlayerName, String secondPlayerName, int firstPlayerExplained, int secondPlayerExplained) {
        this.firstPlayerName = firstPlayerName;
        this.secondPlayerName = secondPlayerName;
        this.firstPlayerExplained = firstPlayerExplained;
        this.secondPlayerExplained = secondPlayerExplained;
    }
}
