package me.sieric.thehat.logic.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import me.sieric.thehat.logic.data.Player;
import me.sieric.thehat.logic.data.Team;
import me.sieric.thehat.logic.data.Word;

/**
 * Class to manage the logic of the game:
 * words guessing, players turn changing
 * Also, contains main game information: players names, words pack, etc
 */
public abstract class Game {

    protected ArrayList<Player> players = new ArrayList<>();
    protected List<Word> words = new ArrayList<>();
    protected List<Integer> unfinishedWordsIds = new ArrayList<>();

    protected static final Random RANDOM = new Random();
    protected boolean isSquare;

    /**
     * Gets next word to guess
     * In fact, moves its id to the back of {@link Game#unfinishedWordsIds}
     * @return next word
     */
    public Word getNextWord() {
        int index = RANDOM.nextInt(unfinishedWordsIds.size());
        Collections.swap(unfinishedWordsIds, index, unfinishedWordsIds.size() - 1);
        words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).setStatus(Word.Status.USED);
        return words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1));
    }

    /**
     * Gets number of unfinished (not guessed and not failed) words in the "hat"
     * @return number of unfinished words
     */
    public int getNumberOfUnfinishedWords() {
        return unfinishedWordsIds.size();
    }

    /**
     * Gets true if game "is square" (players explain words each to each), else otherwise
     * @return if the game "is square"
     */
    public boolean isSquare() {
        return isSquare;
    }

    /**
     * Gets player name by id
     * @param id give id
     * @return player name
     */
    public String getPlayerName(int id) {
        return players.get(id).getName();
    }

    /**
     * Gets list of players sorted by their results
     * @return sorted players list
     */
    public ArrayList<Player> getPlayersStats() {
        ArrayList<Player> playersList = new ArrayList<>(players.size());
        playersList.addAll(players);
        Collections.sort(playersList, (o1, o2) -> (o2.getExplained() + o2.getGuessed()) - (o1.getExplained() + o1.getGuessed()));
        return playersList;
    }

    /**
     * Gets list of team sorted by their results
     * @return sorted teams list
     */
    public ArrayList<Team> getTeamsStats() {
        int playersNumber = players.size();
        ArrayList<Team> teamsList = new ArrayList<>(playersNumber / 2);
        for (int i = 0; i < playersNumber / 2; i++) {
            teamsList.add(new Team(players.get(i).getName(), players.get(i + playersNumber / 2).getName(), players.get(i).getExplained(), players.get(i + playersNumber / 2).getExplained()));
        }
        Collections.sort(teamsList, (o1, o2) -> (o2.getFirstPlayerExplained() + o2.getSecondPlayerExplained()) - (o1.getFirstPlayerExplained() + o1.getSecondPlayerExplained()));
        return teamsList;
    }

    /**
     * Gets list of used words sorted by explanation time
     * @return sorted words list
     */
    public ArrayList<Word> getWordsStats() {
        ArrayList<Word> wordsList = new ArrayList<>(words.size());
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).getStatus() != Word.Status.UNUSED) {
                wordsList.add(words.get(i));
            }
        }
        Collections.sort(wordsList, (o1, o2) -> o1.getTime() - o2.getTime());
        return wordsList;
    }

    /**
     * Sets last (in {@link Game#unfinishedWordsIds}) word as guessed (in time {@code time})
     * @param time explanation time
     */
    public abstract void setWordAsGuessed(int time);

    /**
     * Sets last (in {@link Game#unfinishedWordsIds}) word as failed (in time {@code time})
     * @param time explanation time
     */
    public abstract void setWordAsFailed(int time);

    /**
     * Sets last (in {@link Game#unfinishedWordsIds}) word as skipped (in time {@code time})
     * @param time explanation time
     */
    public abstract void setWordAsSkipped(int time);

    /**
     * Does the game phase
     */
    public abstract void doPhase();

    /**
     * Gets id of player, which is going to explain
     * @return id of player, which is going to explain
     */
    public abstract int getFirstPlayer();

    /**
     * Gets id of player, which is going to guess
     * @return id of player, which is going to guess
     */
    public abstract int getSecondPlayer();
}
