package me.sieric.thehat.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OnlineGame {
    private ArrayList<Player> players = new ArrayList<>();
    private List<Word> words = new ArrayList<>();
    private ArrayList<Integer> unfinishedWordsIds = new ArrayList<>();
    private int wordsNumber;
    private static final Random RANDOM = new Random();
    private int playerA;
    private int playerB;
    private List<Word> phaseWords = new ArrayList<>();
    private int playersNumber;
    private boolean isSquare = true;

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
        this.playersNumber = players.size();
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public ArrayList<Integer> getUnfinishedWordsIds() {
        return unfinishedWordsIds;
    }

    public void setUnfinishedWordsIds(ArrayList<Integer> unfinishedWordsIds) {
        this.unfinishedWordsIds = unfinishedWordsIds;
    }

    public int getWordsNumber() {
        return wordsNumber;
    }

    public void setWordsNumber(int wordsNumber) {
        this.wordsNumber = wordsNumber;
    }

    public Word getNextWord() {
        int index = RANDOM.nextInt(unfinishedWordsIds.size());
        Collections.swap(unfinishedWordsIds, index, unfinishedWordsIds.size() - 1);
        words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).status = Word.Status.USED;
        return words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1));
    }

    public void setWordAsGuessed(int time) {
        words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).status = Word.Status.GUESSED;
        words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).time += time;
        phaseWords.add(new Word(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1), words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).word, time, Word.Status.GUESSED));
        unfinishedWordsIds.remove(unfinishedWordsIds.size() - 1);
    }

    public void setWordAsFailed(int time) {
        words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).status = Word.Status.FAILED;
        words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).time += time;
        phaseWords.add(new Word(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1), words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).word, time, Word.Status.FAILED));
        unfinishedWordsIds.remove(unfinishedWordsIds.size() - 1);
    }

    public void setWordAsSkipped(int time) {
        phaseWords.add(new Word(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1), words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).word, time, Word.Status.USED));
        words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).time += time;
    }

    public int getPlayerA() {
        return playerA;
    }

    public void setPlayerA(int playerA) {
        this.playerA = playerA;
    }

    public int getPlayerB() {
        return playerB;
    }

    public void setPlayerB(int playerB) {
        this.playerB = playerB;
    }

    public List<Word> getPhaseWords() {
        return phaseWords;
    }

    public void setPhaseWords(List<Word> phaseWords) {
        this.phaseWords = phaseWords;
    }

    public int getNumberOfUnguessedWords() {
        return unfinishedWordsIds.size();
    }

    public ArrayList<Player> getPlayersInOrder() {
        ArrayList<Player> list = new ArrayList<>(players.size());
        list.addAll(players);
        Collections.sort(list, (o1, o2) -> (o2.explained + o2.guessed) - (o1.explained + o1.guessed));
        return list;
    }

    public ArrayList<Team> getTeamsInOrder() {
        ArrayList<Team> list = new ArrayList<>(playersNumber / 2);
        for (int i = 0; i < playersNumber / 2; i++) {
            list.add(new Team(players.get(i).name, players.get(i + playersNumber / 2).name, players.get(i).explained, players.get(i + playersNumber / 2).explained));
        }
        Collections.sort(list, (o1, o2) -> (o2.explainedA + o2.explainedB) - (o1.explainedA + o1.explainedB));
        return list;
    }

    public ArrayList<Word> getWordsStats() {
        ArrayList<Word> list = new ArrayList<>(words.size());
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).status != Word.Status.UNUSED) {
                list.add(words.get(i));
            }
        }
        Collections.sort(list, (o1, o2) -> o2.time - o1.time);
        return list;
    }

    public boolean isSquare() {
        return isSquare;
    }

    public void setSquare(boolean square) {
        isSquare = square;
    }
}
