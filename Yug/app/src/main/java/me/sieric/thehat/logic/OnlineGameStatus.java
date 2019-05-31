package me.sieric.thehat.logic;

import lombok.Getter;

@Getter
public class OnlineGameStatus {

    public enum GameStatus {
        CREATED, RUNNING, FINISHED, DOES_NOT_EXISTS;
    }

    private GameStatus gameStatus;
    private int wordsNumber;
    private int playersNumber;
    private int firstPlayer;
    private int secondPlayer;
    private int finishedWords;
    private boolean isSquare;

    public OnlineGameStatus(String gameStatus, int wordsNumber, int playersNumber) {
        this.gameStatus = GameStatus.valueOf(gameStatus);
        this.wordsNumber = wordsNumber;
        this.playersNumber = playersNumber;
    }

    public OnlineGameStatus(String gameStatus, int wordsNumber, int playersNumber, int firstPlayer, int secondPlayer, int finishedWords, boolean isSquare) {
        this.gameStatus = GameStatus.valueOf(gameStatus);
        this.wordsNumber = wordsNumber;
        this.playersNumber = playersNumber;
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.finishedWords = finishedWords;
        this.isSquare = isSquare;
    }
}
