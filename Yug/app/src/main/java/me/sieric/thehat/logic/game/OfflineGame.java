package me.sieric.thehat.logic.game;

import java.util.ArrayList;

import me.sieric.thehat.logic.data.Player;
import me.sieric.thehat.logic.data.Word;

/**
 * Game implementation for offline game
 */
public class OfflineGame extends Game {

    /** current explanation number */
    private int phase = 0;
    private int playersNumber;

    public OfflineGame(int playersNumber, ArrayList<String> playersNames, ArrayList<Word> words, boolean isSquare) {
        this.playersNumber = playersNumber;
        this.isSquare = isSquare;

        players = new ArrayList<>(playersNumber);
        for (int i = 0; i < playersNumber; i++) {
            players.add(new Player(playersNames.get(i)));
        }

        this.words = words;

        unfinishedWordsIds = new ArrayList<>(words.size());
        for (int i = 0; i < words.size(); i++) {
            unfinishedWordsIds.add(i);
        }
    }

    /** Special constructor for "One-to-others" game */
    public OfflineGame(ArrayList<Word> words) {
        playersNumber = 2;
        this.isSquare = true;

        players = new ArrayList<>(playersNumber);
        players.add(new Player("One"));
        players.add(new Player("Others"));

        this.words = words;

        unfinishedWordsIds = new ArrayList<>(words.size());
        for (int i = 0; i < words.size(); i++) {
            unfinishedWordsIds.add(i);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWordAsGuessed(int time) {
        words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).setStatus(Word.Status.GUESSED);
        words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).setTime(words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).getTime() + time);
        players.get(getFirstPlayer()).setExplained(players.get(getFirstPlayer()).getExplained() + 1);
        players.get(getSecondPlayer()).setGuessed(players.get(getSecondPlayer()).getGuessed() + 1);
        unfinishedWordsIds.remove(unfinishedWordsIds.size() - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWordAsFailed(int time) {
        words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).setStatus(Word.Status.FAILED);
        words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).setTime(words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).getTime() + time);
        unfinishedWordsIds.remove(unfinishedWordsIds.size() - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWordAsSkipped(int time) {
        words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).setTime(words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).getTime() + time);
    }

    /** Just increases explanation number */
    @Override
    public void doPhase() {
        phase++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFirstPlayer() {
        return phase % playersNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSecondPlayer() {
        if (isSquare) {
            return (phase % playersNumber + (phase / playersNumber) % (playersNumber - 1) + 1) % playersNumber;
        } else {
            return (phase + playersNumber / 2) % playersNumber;
        }
    }
}
