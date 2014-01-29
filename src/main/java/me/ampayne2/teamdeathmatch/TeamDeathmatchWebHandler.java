package me.ampayne2.teamdeathmatch;

import me.ampayne2.ultimategames.api.UltimateGames;
import me.ampayne2.ultimategames.api.arenas.Arena;
import me.ampayne2.ultimategames.api.arenas.scoreboards.Scoreboard;
import me.ampayne2.ultimategames.api.webapi.WebHandler;
import me.ampayne2.ultimategames.gson.Gson;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class TeamDeathmatchWebHandler implements WebHandler {
    private Arena arena;
    private UltimateGames ug;

    public TeamDeathmatchWebHandler(UltimateGames ug, Arena arena) {
        this.arena = arena;
        this.ug = ug;
    }

    @Override
    public String sendResult() {
        Gson gson = new Gson();

        Map<String, Integer> map = new HashMap<>();

        Scoreboard scoreBoard = ug.getScoreboardManager().getScoreboard(arena);
        if (scoreBoard != null) {
            map.put("Team Blue", scoreBoard.getScore(ChatColor.BLUE + "Team Blue"));
            map.put("Team Red", scoreBoard.getScore(ChatColor.RED + "Team Red"));
        }

        return gson.toJson(map);
    }
}
