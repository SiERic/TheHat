package me.sieric.thehat.logic;

import me.sieric.thehat.logic.game.Game;


/**
 * Special class to hold some game information
 */
public class GameHolder {

    public static Game game;
    public static GameType gameType;
    public static String name = "Sasha";

    public static long dictId;

    public static boolean isCreator;
    public static int playerId;
    public static String gameId;

    public enum GameType {
        ONLINE,
        OFFLINE,
        ONE_TO_OTHERS
    }
}
