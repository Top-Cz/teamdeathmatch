/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2014, UltimateGames Staff <https://github.com/UltimateGames//>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.ampayne2.teamdeathmatch;

import me.ampayne2.ultimategames.api.message.Message;

public enum TDMessage implements Message {
    GAME_END("GameEnd", "%s won %s on arena %s!"),
    GAME_TIE("GameTie", "%s and %s tied %s on arena %s!"),
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
