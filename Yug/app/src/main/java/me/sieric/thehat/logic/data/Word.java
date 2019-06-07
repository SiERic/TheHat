package me.sieric.thehat.logic.data;

import lombok.Getter;
import lombok.Setter;

/**
 * Data class for word
 * Contains word id (from database), game information (time spent and status) and the word itself
 */
@Getter @Setter
public class Word {

    private long wordId;
    private String word;
    private int time;
    private Status status;

    public Word(long wordId, String word) {
        this.wordId = wordId;
        this.word = word;
        status = Status.UNUSED;
    }

    public Word(int id, String word, int time, Status status) {
        this.wordId = id;
        this.word = word;
        this.status = status;
        this.time = time;
    }

    public enum Status {
        UNUSED(""),
        USED("-"),
        GUESSED("+"),
        FAILED("x");

        Status(String statsString) {
            this.statsString = statsString;
        }

        /** String to show in statistics */
        private String statsString;

        public String toStatsString() {
            return statsString;
        }
    }
}
