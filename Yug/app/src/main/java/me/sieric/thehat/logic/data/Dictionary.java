package me.sieric.thehat.logic.data;

import lombok.Getter;
import lombok.Setter;

/**
 * Data class for dictionary
 * Contains dictionary id (from database) and name
 */
@Getter @Setter
public class Dictionary {

    private long id;
    private String name;

    public Dictionary(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
