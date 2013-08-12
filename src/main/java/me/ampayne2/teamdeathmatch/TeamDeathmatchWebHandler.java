package me.ampayne2.teamdeathmatch;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

import me.ampayne2.ultimategames.UltimateGames;
import me.ampayne2.ultimategames.arenas.Arena;
import me.ampayne2.ultimategames.gson.Gson;
import me.ampayne2.ultimategames.scoreboards.ArenaScoreboard;
import me.ampayne2.ultimategames.webapi.WebHandler;
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

        for (ArenaScoreboard scoreBoard : ug.getScoreboardManager().getArenaScoreboards(arena)) {
            if (scoreBoard.getName().equals("Kills")) {
                map.put("Team Blue", scoreBoard.getScore(ChatColor.BLUE + "Team Blue"));
                map.put("Team Red", scoreBoard.getScore(ChatColor.RED + "Team Red"));
                break;
            }
        }
        return gson.toJson(map);
    }
}
