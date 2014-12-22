package me.ampayne2.teamdeathmatch;

import me.ampayne2.ultimategames.api.message.Message;

public enum TDMessage implements Message {
    GAME_END("GameEnd", "b%s won %s on arena %s!"),
    GAME_TIE("GameTie", "b%s and %s tied %s on arena %s!"),
    GAME_NOT_STARTED("GameNotStarted", "&4Cannot buy perks before the game starts!"),
    KILL("Kill", "%s killed %s!"),
    DEATH("Death", "%s died!"),
    PERK_ACTIVATE("Perk.Activate", "&bActivated %s&b!"),
    PERK_DEACTIVATE("Perk.Deactivate", "&b%s&b is no longer active!"),
    PERK_CANNOTACTIVATE("Perk.CannotActivate", "&4Cannot activate %s!"),
    PERK_ALREADYACTIVE("Perk.AlreadyActive", "&b%s&b is already active!"),
    PERK_NOTENOUGHCOINS("Perk.NotEnoughCoins", "&4Not enough killcoins to activate %s!"),
    PERK_NOTUNLOCKED("Perk.NotUnlocked", "&4You haven't unlocked %s!"),
    OUT_OF_ARROWS("Perk.OutOfArrows", "&4You are out of arrows! Left click to refill for 2 killcoins."),
    SUN_SONG("SunSong", "&b%s sung the Sun Song!"),
    RAIN_DANCE("RainDance", "&b%s danced the Rain Dance!");

    private String message;
    private final String path;
    private final String defaultMessage;

    private TDMessage(String path, String defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getDefault() {
        return defaultMessage;
    }

    @Override
    public String toString() {
        return message;
    }
}
