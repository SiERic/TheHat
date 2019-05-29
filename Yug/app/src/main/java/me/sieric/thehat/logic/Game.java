package me.sieric.thehat.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game {
    private final int playersNumber;
    private ArrayList<Word> words;
    private ArrayList<Integer> unguessedWords;

    private ArrayList<Player> players;

    private int phase = -1;
    private boolean isSquare;

    private static final Random RANDOM = new Random();

    public Game(int playersNumber, ArrayList<String> playersNames, ArrayList<Word> words, boolean isSquare) {
        this.playersNumber = playersNumber;
        this.isSquare = isSquare;

        players = new ArrayList<>(playersNumber);
        for (int i = 0; i < playersNumber; i++) {
            players.add(new Player(playersNames.get(i)));
        }

        this.words = words;

        unguessedWords = new ArrayList<>(words.size());
        for (int i = 0; i < words.size(); i++) {
            unguessedWords.add(i);
        }
    }

    public Game(ArrayList<Word> words) {
        playersNumber = 2;
        this.isSquare = true;

        players = new ArrayList<>(playersNumber);
        players.add(new Player("One"));
        players.add(new Player("Other"));

        this.words = words;

        unguessedWords = new ArrayList<>(words.size());
        for (int i = 0; i < words.size(); i++) {
            unguessedWords.add(i);
        }
    }

    public int getNumberOfUnguessedWords() {
        return unguessedWords.size();
    }

    public Word getNextWord() {
        int index = RANDOM.nextInt(unguessedWords.size());
        Collections.swap(unguessedWords, index, unguessedWords.size() - 1);
        words.get(unguessedWords.get(unguessedWords.size() - 1)).status = Word.Status.USED;
        return words.get(unguessedWords.get(unguessedWords.size() - 1));
    }

    public void setWordAsGuessed(int time) {
        words.get(unguessedWords.get(unguessedWords.size() - 1)).status = Word.Status.GUESSED;
        words.get(unguessedWords.get(unguessedWords.size() - 1)).time += time;
        players.get(getCurrentPlayerA()).explained++;
        players.get(getCurrentPlayerB()).guessed++;
        unguessedWords.remove(unguessedWords.size() - 1);
    }

    public void setWordAsFailed(int time) {
        words.get(unguessedWords.get(unguessedWords.size() - 1)).status = Word.Status.FAILED;
        words.get(unguessedWords.get(unguessedWords.size() - 1)).time += time;
        unguessedWords.remove(unguessedWords.size() - 1);
    }

    public void setWordAsSkipped(int time) {
        words.get(unguessedWords.get(unguessedWords.size() - 1)).time += time;
    }

    public void increasePhase() {
        phase++;
    }

    private int getCurrentPlayerA() {
        return phase % playersNumber;
    }

    private int getCurrentPlayerB() {
        if (isSquare) {
            return (phase % playersNumber + (phase / playersNumber) % (playersNumber - 1) + 1) % playersNumber;
        } else {
            return (phase + playersNumber / 2) % playersNumber;
        }
    }

    public String getCurrentPlayerAName() {
        return players.get(getCurrentPlayerA()).name;
    }

    public String getCurrentPlayerBName() {
        return players.get(getCurrentPlayerB()).name;
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
}
