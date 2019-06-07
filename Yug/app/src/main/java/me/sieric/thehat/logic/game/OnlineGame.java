package me.sieric.thehat.logic.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.Setter;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.NetworkManager;
import me.sieric.thehat.logic.data.Player;
import me.sieric.thehat.logic.data.Team;
import me.sieric.thehat.logic.data.Word;

/**
 * Game implementation for online game
 */
public class OnlineGame extends Game {

    @Setter
    private Status status;
    private List<Word> phaseWords = new ArrayList<>();

    /**
     * Sets words
     * @param words given words list
     */
    public void setWords(List<Word> words) {
        this.words = words;
        unfinishedWordsIds = IntStream.range(0, words.size()).boxed().collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWordAsGuessed(int time) {
        phaseWords.add(new Word(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1),
                words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).getWord(), time, Word.Status.GUESSED));
        unfinishedWordsIds.remove(unfinishedWordsIds.size() - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWordAsFailed(int time) {
        phaseWords.add(new Word(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1),
                words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).getWord(), time, Word.Status.FAILED));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWordAsSkipped(int time) {
        phaseWords.add(new Word(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1),
                words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).getWord(), time, Word.Status.USED));
    }


    /**
     * Sets words as finished (remove it from {@link Game#unfinishedWordsIds})
     * @param id given word id
     */
    public void setWordAsFinished(int id) {
        unfinishedWordsIds.remove(id);
    }

    /**
     * Sends phase data (about words explanation) to the server
     */
    @Override
    public void doPhase() {
        NetworkManager.doPhase(GameHolder.gameId, phaseWords);
        phaseWords.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFirstPlayer() {
        return status.getFirstPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSecondPlayer() {
        return status.getSecondPlayer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<Player> getPlayersStats() {
        updatePlayers();
        return super.getPlayersStats();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<Team> getTeamsStats() {
        updatePlayers();
        for (int i = 0; i < players.size(); i++) {
            System.out.println(players.get(i).getName());
            System.out.println(players.get(i).getGuessed());
            System.out.println(players.get(i).getExplained());
        }
        return super.getTeamsStats();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<Word> getWordsStats() {
        updateWords();
        return super.getWordsStats();
    }

    /**
     * Gets players list
     * @return players list
     */
    public ArrayList<Player> getPlayers() {
       return players;
    }

    /**
     * Gets number of finished (guessed or failed) words in the "hat"
     * @return number of finished words
     */
    public int getNumberOfFinishedWords() {
        return status.getFinishedWords();
    }

    /**
     * Gets total words number
     * @return words number
     */
    public int getWordsNumber() {
        return words.size();
    }

    /**
     * Updates players info (via server)
     */
    private void updatePlayers() {
        AtomicBoolean fl = new AtomicBoolean(false);
        NetworkManager.playersStats(GameHolder.gameId, players -> {
            this.players = players;
            fl.set(true);
        });
        while (!fl.get());
    }

    /**
     * Updates words info (via server)
     */
    private void updateWords() {
        AtomicBoolean fl = new AtomicBoolean(false);
        NetworkManager.wordsStats(GameHolder.gameId, words -> {
            for (int i = 0; i < words.size(); i++) {
                this.words.get(i).setTime(words.get(i).getTime());
                this.words.get(i).setStatus(words.get(i).getStatus());
            }
            fl.set(true);
        });
        while (!fl.get());
    }

    /**
     * Class for game status
     * Contains the main game parameters: status, words/players number, current players
     * Mostly used to store information provided by the server
     */
    @Getter
    public static class Status {

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

        public Status(String gameStatus, int wordsNumber, int playersNumber) {
            this.gameStatus = GameStatus.valueOf(gameStatus);
            this.wordsNumber = wordsNumber;
            this.playersNumber = playersNumber;
        }

        public Status(String gameStatus, int wordsNumber, int playersNumber, int firstPlayer, int secondPlayer, int finishedWords, boolean isSquare) {
            this.gameStatus = GameStatus.valueOf(gameStatus);
            this.wordsNumber = wordsNumber;
            this.playersNumber = playersNumber;
            this.firstPlayer = firstPlayer;
            this.secondPlayer = secondPlayer;
            this.finishedWords = finishedWords;
            this.isSquare = isSquare;
        }
    }
}
