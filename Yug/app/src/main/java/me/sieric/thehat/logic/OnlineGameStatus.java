package me.sieric.thehat.logic;

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

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public int getWordsNumber() {
        return wordsNumber;
    }

    public int getPlayersNumber() {
        return playersNumber;
    }

    public int getFirstPlayer() {
        return firstPlayer;
    }

    public int getSecondPlayer() {
        return secondPlayer;
    }

    public int getFinishedWords() {
        return finishedWords;
    }

    public boolean isSquare() {
        return isSquare;
    }
}
