package me.sieric.thehat.logic;

import me.sieric.thehat.logic.games.Game;

public class GameHolder {
    public static Game game;
    public static long dictId;
    public static String name = "Sasha";
    public static int playerId;
    public static boolean isCreator;
    public static String gameId;
    public static GameType gameType;

    public enum GameType {
        ONLINE,
        OFFLINE,
        ONE_TO_OTHERS;
    }
}
