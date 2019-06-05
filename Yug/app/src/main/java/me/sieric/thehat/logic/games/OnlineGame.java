package me.sieric.thehat.logic.games;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Setter;
import me.sieric.thehat.logic.GameHolder;
import me.sieric.thehat.logic.NetworkManager;
import me.sieric.thehat.logic.data.Player;
import me.sieric.thehat.logic.data.Word;

public class OnlineGame extends Game {

    @Setter
    private OnlineGameStatus status;
    private List<Word> phaseWords = new ArrayList<>();

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
        this.playersNumber = players.size();
    }

    public void setWords(List<Word> words) {
        this.words = words;
        unfinishedWordsIds = IntStream.range(0, words.size()).boxed().collect(Collectors.toList());
    }

    @Override
    public void setWordAsGuessed(int time) {
        phaseWords.add(new Word(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1),
                words.get(unfinishedWordsIds.get(unfinishedWordsIds.size() - 1)).getWord(), time, Word.Status.GUESSED));
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
        NetworkManager.playersStats(GameHolder.gameId, players -> {
            this.players = players;
        });
        return super.getPlayersStats();
    }

    @Override
    public ArrayList<Word> getWordsStats() {
        NetworkManager.wordsStats(GameHolder.gameId, words -> {
            for (int i = 0; i < words.size(); i++) {
                this.words.get(i).setTime(words.get(i).getTime());
                this.words.get(i).setStatus(words.get(i).getStatus());
            }
        });
        return super.getWordsStats();
    }

    public ArrayList<Player> getPlayers() {
       return players;
    }
}
