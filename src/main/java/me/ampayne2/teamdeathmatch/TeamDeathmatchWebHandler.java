package me.ampayne2.teamdeathmatch;

import me.ampayne2.ultimategames.UltimateGames;
import me.ampayne2.ultimategames.arenas.Arena;
import me.ampayne2.ultimategames.arenas.scoreboards.ArenaScoreboard;
import me.ampayne2.ultimategames.gson.Gson;
import me.ampayne2.ultimategames.webapi.WebHandler;
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

        Map<String, Integer> map = new HashMap<String, Integer>();

        ArenaScoreboard scoreBoard = ug.getScoreboardManager().getScoreboard(arena);
        if (scoreBoard != null) {
            map.put("Team Blue", scoreBoard.getScore(ChatColor.BLUE + "Team Blue"));
            map.put("Team Red", scoreBoard.getScore(ChatColor.RED + "Team Red"));
        }

        return gson.toJson(map);
    }
}
