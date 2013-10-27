package me.ampayne2.teamdeathmatch;

import java.util.List;
import me.ampayne2.ultimategames.UltimateGames;
import me.ampayne2.ultimategames.api.GamePlugin;
import me.ampayne2.ultimategames.arenas.Arena;
import me.ampayne2.ultimategames.arenas.PlayerSpawnPoint;
import me.ampayne2.ultimategames.enums.ArenaStatus;
import me.ampayne2.ultimategames.games.Game;
import me.ampayne2.ultimategames.scoreboards.ArenaScoreboard;
import me.ampayne2.ultimategames.teams.Team;
import me.ampayne2.ultimategames.teams.TeamManager;

import me.ampayne2.ultimategames.utils.UGUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class TeamDeathmatch extends GamePlugin {

    private UltimateGames ultimateGames;
    private Game game;

    @Override
    public Boolean loadGame(UltimateGames ultimateGames, Game game) {
        this.ultimateGames = ultimateGames;
        this.game = game;
        return true;
    }

    @Override
    public void unloadGame() {

    }

    @Override
    public Boolean reloadGame() {
        return true;
    }

    @Override
    public Boolean stopGame() {
        return true;
    }

    @Override
    public Boolean loadArena(Arena arena) {
        TeamManager teamManager = ultimateGames.getTeamManager();
        teamManager.addTeam(new Team(ultimateGames, "Blue", arena, ChatColor.BLUE, false));
        teamManager.addTeam(new Team(ultimateGames, "Red", arena, ChatColor.RED, false));
        ultimateGames.addAPIHandler("/" + game.getName() + "/" + arena.getName(), new TeamDeathmatchWebHandler(ultimateGames, arena));
        return true;
    }

    @Override
    public Boolean unloadArena(Arena arena) {
        ultimateGames.getTeamManager().removeTeamsOfArena(arena);
        return true;
    }

    @Override
    public Boolean isStartPossible(Arena arena) {
        return arena.getStatus() == ArenaStatus.OPEN;
    }

    @Override
    public Boolean startArena(Arena arena) {
        return true;
    }

    @Override
    public Boolean beginArena(Arena arena) {
        // Creates a new ending countdown
        ultimateGames.getCountdownManager().createEndingCountdown(arena, ultimateGames.getConfigManager().getGameConfig(game).getConfig().getInt("CustomValues.GameTime"), true);

        // Creates a new arena scoreboard and adds team blue and red
        ArenaScoreboard scoreBoard = ultimateGames.getScoreboardManager().createArenaScoreboard(arena, "Kills");
        scoreBoard.setScore(ChatColor.BLUE + "Team Blue", 0);
        scoreBoard.setScore(ChatColor.RED + "Team Red", 0);
        scoreBoard.setVisible(true);

        TeamManager teamManager = ultimateGames.getTeamManager();
        teamManager.sortPlayersIntoTeams(arena);
        Team blue = teamManager.getTeam(arena, "Blue");
        Team red = teamManager.getTeam(arena, "Red");
        for (String playerName : blue.getPlayers()) {
            Player player = Bukkit.getPlayerExact(playerName);
            scoreBoard.addPlayer(player);
            PlayerSpawnPoint spawnPoint = ultimateGames.getSpawnpointManager().getSpawnPoint(arena, 0);
            spawnPoint.lock(false);
            spawnPoint.teleportPlayer(player);
            blue.setPlayerColorToTeamColor(player);
        }
        for (String playerName : red.getPlayers()) {
            Player player = Bukkit.getPlayerExact(playerName);
            scoreBoard.addPlayer(player);
            PlayerSpawnPoint spawnPoint = ultimateGames.getSpawnpointManager().getSpawnPoint(arena, 1);
            spawnPoint.lock(false);
            spawnPoint.teleportPlayer(player);
            red.setPlayerColorToTeamColor(player);
        }

        for (String playerName : arena.getPlayers()) {
            resetInventory(Bukkit.getPlayerExact(playerName));
        }
        return true;
    }

    @Override
    public void endArena(Arena arena) {
        ArenaScoreboard scoreBoard = ultimateGames.getScoreboardManager().getArenaScoreboard(arena);
        if (scoreBoard != null) {
            Integer teamOneScore = scoreBoard.getScore(ChatColor.BLUE + "Team Blue");
            Integer teamTwoScore = scoreBoard.getScore(ChatColor.RED + "Team Red");
            if (teamOneScore > teamTwoScore) {
                ultimateGames.getMessageManager().sendGameMessage(ultimateGames.getServer(), game, "GameEnd", "Team Blue", game.getName(), arena.getName());
                for (String player : ultimateGames.getTeamManager().getTeam(arena, "Blue").getPlayers()) {
                    ultimateGames.getPointManager().addPoint(game, player, "store", 25);
                    ultimateGames.getPointManager().addPoint(game, player, "win", 1);
                }
            } else if (teamOneScore < teamTwoScore) {
                ultimateGames.getMessageManager().sendGameMessage(ultimateGames.getServer(), game, "GameEnd", "Team Red", game.getName(), arena.getName());
                for (String player : ultimateGames.getTeamManager().getTeam(arena, "Red").getPlayers()) {
                    ultimateGames.getPointManager().addPoint(game, player, "store", 25);
                    ultimateGames.getPointManager().addPoint(game, player, "win", 1);
                }
            } else {
                ultimateGames.getMessageManager().sendGameMessage(ultimateGames.getServer(), game, "GameTie", "Team Blue", "Team Red", game.getName(), arena.getName());
            }
        }
    }

    @Override
    public Boolean resetArena(Arena arena) {
        return true;
    }

    @Override
    public Boolean openArena(Arena arena) {
        return true;
    }

    @Override
    public Boolean stopArena(Arena arena) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Boolean addPlayer(Player player, Arena arena) {
        if (arena.getStatus() == ArenaStatus.OPEN && arena.getPlayers().size() >= arena.getMinPlayers() && !ultimateGames.getCountdownManager().hasStartingCountdown(arena)) {
            ultimateGames.getCountdownManager().createStartingCountdown(arena, ultimateGames.getConfigManager().getGameConfig(game).getConfig().getInt("CustomValues.StartWaitTime"));
        }
        PlayerSpawnPoint spawnPoint = ultimateGames.getSpawnpointManager().getRandomSpawnPoint(arena);
        spawnPoint.lock(false);
        spawnPoint.teleportPlayer(player);
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();
        return true;
    }

    @Override
    public void removePlayer(Player player, Arena arena) {
        String playerName = player.getName();
        List<String> queuePlayer = ultimateGames.getQueueManager().getNextPlayers(1, arena);
        TeamManager teamManager = ultimateGames.getTeamManager();
        if (!queuePlayer.isEmpty()) {
            String newPlayerName = queuePlayer.get(0);
            Player newPlayer = Bukkit.getPlayerExact(newPlayerName);
            ultimateGames.getPlayerManager().addPlayerToArena(newPlayer, arena, true);
            Team team = teamManager.getPlayerTeam(playerName);
            if (team != null) {
                team.addPlayer(newPlayer);
            }
        }
        if (arena.getStatus() == ArenaStatus.RUNNING && (teamManager.getTeam(arena, "Red").getPlayers().size() <= 0 || teamManager.getTeam(arena, "Blue").getPlayers().size() <= 0)) {
            ultimateGames.getArenaManager().endArena(arena);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public Boolean addSpectator(Player player, Arena arena) {
        ultimateGames.getSpawnpointManager().getSpectatorSpawnPoint(arena).teleportPlayer(player);
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().addItem(UGUtils.createInstructionBook(game));
        player.getInventory().setArmorContents(null);
        player.updateInventory();
        return true;
    }

    @Override
    public void removeSpectator(Player player, Arena arena) {

    }

    @Override
    public void onPlayerDeath(Arena arena, PlayerDeathEvent event) {
        if (arena.getStatus() == ArenaStatus.RUNNING) {
            String playerName = event.getEntity().getName();
            Player killer = event.getEntity().getKiller();
            if (killer != null) {
                ultimateGames.getMessageManager().sendGameMessage(arena, game, "Kill", killer.getName(), playerName);
                ultimateGames.getPointManager().addPoint(game, killer.getName(), "kill", 1);
                ultimateGames.getPointManager().addPoint(game, killer.getName(), "store", 1);
            } else {
                ultimateGames.getMessageManager().sendGameMessage(arena, game, "Death", playerName);
            }
            ultimateGames.getPointManager().addPoint(game, playerName, "death", 1);
            ArenaScoreboard scoreBoard = ultimateGames.getScoreboardManager().getArenaScoreboard(arena);
            if (scoreBoard != null) {
                Team team = ultimateGames.getTeamManager().getPlayerTeam(playerName);
                if (team.getName().equals("Red")) {
                    scoreBoard.setScore(ChatColor.BLUE + "Team Blue", scoreBoard.getScore(ChatColor.BLUE + "Team Blue") + 1);
                } else if (team.getName().equals("Blue")) {
                    scoreBoard.setScore(ChatColor.RED + "Team Red", scoreBoard.getScore(ChatColor.RED + "Team Red") + 1);
                }
            }
        }
        event.getDrops().clear();
        UGUtils.autoRespawn(event.getEntity());
    }

    @Override
    public void onPlayerRespawn(Arena arena, PlayerRespawnEvent event) {
        event.setRespawnLocation(ultimateGames.getSpawnpointManager().getSpawnPoint(arena, ultimateGames.getTeamManager().getPlayerTeam(event.getPlayer().getName()).getName().equals("Blue") ? 0 : 1)
                .getLocation());
        resetInventory(event.getPlayer());
    }

    @Override
    public void onEntityDamage(Arena arena, EntityDamageEvent event) {
        if (arena.getStatus() != ArenaStatus.RUNNING) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPlayerFoodLevelChange(Arena arena, FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onItemPickup(Arena arena, PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onItemDrop(Arena arena, PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onArenaCommand(Arena arena, String command, CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if ((arena.getStatus() == ArenaStatus.STARTING || arena.getStatus() == ArenaStatus.OPEN) && command.equals("team") && args.length == 1) {
            String teamName = args[0].toLowerCase();
            TeamManager teamManager = ultimateGames.getTeamManager();
            if (teamName.equals("blue")) {
                Team team = teamManager.getTeam(arena, "Blue");
                if (team.hasSpace()) {
                    teamManager.setPlayerTeam(player, team);
                    PlayerSpawnPoint spawnPoint = ultimateGames.getSpawnpointManager().getSpawnPoint(arena, 0);
                    spawnPoint.lock(false);
                    spawnPoint.teleportPlayer(player);
                } else {
                    ultimateGames.getMessageManager().sendGameMessage(sender, game, "Teamfull", teamName);
                }
            } else if (teamName.equals("red")) {
                Team team = teamManager.getTeam(arena, "Red");
                if (team.hasSpace()) {
                    teamManager.setPlayerTeam(player, team);
                    PlayerSpawnPoint spawnPoint = ultimateGames.getSpawnpointManager().getSpawnPoint(arena, 1);
                    spawnPoint.lock(false);
                    spawnPoint.teleportPlayer(player);
                } else {
                    ultimateGames.getMessageManager().sendGameMessage(sender, game, "Teamfull", teamName);
                }
            } else {
                ultimateGames.getMessageManager().sendGameMessage(sender, game, "Notateam", teamName);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void resetInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1), new ItemStack(Material.BOW, 1), new ItemStack(Material.ARROW, 32), UGUtils.createInstructionBook(game));
        String playerName = player.getName();
        if (ultimateGames.getPlayerManager().isPlayerInArena(playerName)) {
            Color color = ultimateGames.getTeamManager().getPlayerTeam(playerName).getName().equals("Blue") ? Color.BLUE : Color.RED;
            ItemStack helmet = UGUtils.colorArmor(new ItemStack(Material.LEATHER_HELMET, 1), color);
            ItemStack chestplate = UGUtils.colorArmor(new ItemStack(Material.LEATHER_CHESTPLATE, 1), color);
            ItemStack leggings = UGUtils.colorArmor(new ItemStack(Material.LEATHER_LEGGINGS, 1), color);
            ItemStack boots = UGUtils.colorArmor(new ItemStack(Material.LEATHER_BOOTS, 1), color);
            player.getInventory().setArmorContents(new ItemStack[] { boots, leggings, chestplate, helmet });
        }
        player.updateInventory();
    }

}
