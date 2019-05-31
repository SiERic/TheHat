package me.sieric.thehat.logic;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Dictionary {
    private long id;
    private String name;

    public Dictionary(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
