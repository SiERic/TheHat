package me.sieric.thehat.logic;

import lombok.Getter;
import lombok.Setter;

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

        Status(String string) {
            this.string = string;
        }

        private String string;

        public String toStatsString() {
            return string;
        }

        public static Status getFromString(String str) {
            if (str.equals("UNUSED")) return UNUSED;
            if (str.equals("USED")) return USED;
            if (str.equals("FAILED")) return FAILED;
            if (str.equals("GUESSED")) return GUESSED;
            return null;
        }

    }
}
