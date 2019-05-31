package me.sieric.thehat.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class Game {

    protected List<Player> players = new ArrayList<>();
    protected List<Word> words = new ArrayList<>();
    protected List<Integer> unfinishedWordsIds = new ArrayList<>();

    protected static final Random RANDOM = new Random();
    protected boolean isSquare;
    protected int wordsNumber;
    protected int playersNumber;

    public Word getNextWord() {
        int index = RANDOM.nextInt(unfinishedWordsIds.size());
        Collections.swap(unfinishedWordsIds, index, unfinishedWordsIds.size() - 1);
        words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).setStatus(Word.Status.USED);
        return words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1));
    }

    public int getNumberOfUnfinishedWords() {
        return unfinishedWordsIds.size();
    }

    public boolean isSquare() {
        return isSquare;
    }

    public String getPlayersName(int id) {
        return players.get(id).getName();
    }

    public ArrayList<Player> getPlayersStats() {
        ArrayList<Player> playersList = new ArrayList<>(players.size());
        playersList.addAll(players);
        Collections.sort(playersList, (o1, o2) -> (o2.getExplained() + o2.getGuessed()) - (o1.getExplained() + o1.getGuessed()));
        return playersList;
    }

    public ArrayList<Team> getTeamsStats() {
        ArrayList<Team> teamsList = new ArrayList<>(playersNumber / 2);
        for (int i = 0; i < playersNumber / 2; i++) {
            teamsList.add(new Team(players.get(i).getName(), players.get(i + playersNumber / 2).getName(), players.get(i).getExplained(), players.get(i + playersNumber / 2).getExplained()));
        }
        Collections.sort(teamsList, (o1, o2) -> (o2.getFirstPlayerExplained() + o2.getSecondPlayerExplained()) - (o1.getFirstPlayerExplained() + o1.getSecondPlayerExplained()));
        return teamsList;
    }

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

    public int getWordsNumber() {
        return wordsNumber;
    }

    public abstract void setWordAsGuessed(int time);

    public abstract void setWordAsFailed(int time);

    public abstract void setWordAsSkipped(int time);

    public abstract void doPhase();

    public abstract int getFirstPlayer();

    public abstract int getSecondPlayer();
}
