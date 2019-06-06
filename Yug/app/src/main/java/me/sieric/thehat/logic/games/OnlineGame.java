package me.sieric.thehat.logic.games;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Setter;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.NetworkManager;
import me.sieric.thehat.logic.data.Player;
import me.sieric.thehat.logic.data.Team;
import me.sieric.thehat.logic.data.Word;

public class OnlineGame extends Game {

    @Setter
    private OnlineGameStatus status;
    private List<Word> phaseWords = new ArrayList<>();

    public void setWords(List<Word> words) {
        this.words = words;
        unfinishedWordsIds = IntStream.range(0, words.size()).boxed().collect(Collectors.toList());
    }

    @Override
    public void setWordAsGuessed(int time) {
        phaseWords.add(new Word(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1),
                words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).getWord(), time, Word.Status.GUESSED));
        unfinishedWordsIds.remove(unfinishedWordsIds.size() - 1);
    }

    @Override
    public void setWordAsFailed(int time) {
        phaseWords.add(new Word(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1),
                words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).getWord(), time, Word.Status.FAILED));
    }

    @Override
    public void setWordAsSkipped(int time) {
        phaseWords.add(new Word(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1),
                words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).getWord(), time, Word.Status.USED));
    }

    public void setWordAsFinished(int id) {
        unfinishedWordsIds.remove(id);
    }

    @Override
    public void doPhase() {
        NetworkManager.doPhase(GameHolder.gameId, phaseWords);
        phaseWords.clear();
    }

    @Override
    public int getFirstPlayer() {
        return status.getFirstPlayer();
    }

    @Override
    public int getSecondPlayer() {
        return status.getSecondPlayer();
    }

    @Override
    public ArrayList<Player> getPlayersStats() {
        updatePlayers();
        return super.getPlayersStats();
    }

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

    @Override
    public ArrayList<Word> getWordsStats() {
        updateWords();
        return super.getWordsStats();
    }

    public ArrayList<Player> getPlayers() {
       return players;
    }

    public int getNumberOfFinishedWords() {
        return status.getFinishedWords();
    }

    private void updatePlayers() {
        AtomicBoolean fl = new AtomicBoolean(false);
        NetworkManager.playersStats(GameHolder.gameId, players -> {
            this.players = players;
            fl.set(true);
        });
        while (!fl.get());
    }

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
}
