package me.ampayne2.teamdeathmatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.ampayne2.ultimategames.UltimateGames;
import me.ampayne2.ultimategames.api.ArenaScoreboard;
import me.ampayne2.ultimategames.api.GamePlugin;
import me.ampayne2.ultimategames.arenas.Arena;
import me.ampayne2.ultimategames.enums.ArenaStatus;
import me.ampayne2.ultimategames.games.Game;
import me.ampayne2.ultimategames.players.SpawnPoint;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TeamDeathmatch extends GamePlugin {

    private UltimateGames ultimateGames;
    private Game game;
    private Map<Arena, List<String>> teamBlue = new HashMap<Arena, List<String>>();
    private Map<Arena, List<String>> teamRed = new HashMap<Arena, List<String>>();

    @Override
    public Boolean loadGame(UltimateGames ultimateGames, Game game) {
        this.ultimateGames = ultimateGames;
        this.game = game;
        return true;
    }

    @Override
    public Boolean unloadGame() {
        return true;
    }

    @Override
    public Boolean stopGame() {
        return true;
    }

    @Override
    public Boolean loadArena(Arena arena) {
        ultimateGames.addAPIHandler("/" + game.getGameDescription().getName() + "/" + arena.getName(), new TeamDeathmatchWebHandler(ultimateGames, arena));
        teamBlue.put(arena, new ArrayList<String>());
        teamRed.put(arena, new ArrayList<String>());
        return true;
    }

    @Override
    public Boolean unloadArena(Arena arena) {
        if (teamBlue.containsKey(arena)) {
            teamBlue.remove(arena);
        }
        if (teamRed.containsKey(arena)) {
            teamRed.remove(arena);
        }
        return true;
    }

    @Override
    public Boolean isStartPossible(Arena arena) {
        if (arena.getStatus() == ArenaStatus.OPEN) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean startArena(Arena arena) {
        return true;
    }

    @Override
    public Boolean beginArena(Arena arena) {
        ultimateGames.getCountdownManager().createEndingCountdown(arena, ultimateGames.getConfigManager().getGameConfig(game).getConfig().getInt("CustomValues.GameTime"), true);

        for (ArenaScoreboard scoreBoard : new ArrayList<ArenaScoreboard>(ultimateGames.getScoreboardManager().getArenaScoreboards(arena))) {
            ultimateGames.getScoreboardManager().removeArenaScoreboard(arena, scoreBoard.getName());
        }
        ArenaScoreboard scoreBoard = ultimateGames.getScoreboardManager().createArenaScoreboard(arena, "Kills");
        scoreBoard.setScore(ChatColor.BLUE + "Team Blue", 0);
        scoreBoard.setScore(ChatColor.RED + "Team Red", 0);

        List<String> teamOne = new ArrayList<String>();
        List<String> teamTwo = new ArrayList<String>();

        Random generator = new Random();
        while (teamOne.size() + teamTwo.size() != arena.getPlayers().size()) {
            String playerName = arena.getPlayers().get(generator.nextInt(arena.getPlayers().size()));
            if (!teamOne.contains(playerName) && !teamTwo.contains(playerName)) {
                if (teamOne.size() <= teamTwo.size()) {
                    teamOne.add(playerName);
                    ultimateGames.getMessageManager().sendReplacedGameMessage(game, playerName, "Team", ChatColor.BLUE + "Team Blue");
                    scoreBoard.addPlayer(playerName);
                    scoreBoard.setPlayerColor(playerName, ChatColor.BLUE);
                    SpawnPoint spawnPoint = ultimateGames.getSpawnpointManager().getSpawnPoint(arena, 0);
                    spawnPoint.lock(false);
                    spawnPoint.teleportPlayer(playerName);
                } else if (teamOne.size() > teamTwo.size()) {
                    teamTwo.add(playerName);
                    ultimateGames.getMessageManager().sendReplacedGameMessage(game, playerName, "Team", ChatColor.RED + "Team Red");
                    scoreBoard.addPlayer(playerName);
                    scoreBoard.setPlayerColor(playerName, ChatColor.RED);
                    SpawnPoint spawnPoint = ultimateGames.getSpawnpointManager().getSpawnPoint(arena, 1);
                    spawnPoint.lock(false);
                    spawnPoint.teleportPlayer(playerName);
                }
            }
        }
        teamBlue.put(arena, teamOne);
        teamRed.put(arena, teamTwo);

        for (String playerName : arena.getPlayers()) {
            resetInventory(Bukkit.getPlayerExact(playerName));
        }

        scoreBoard.setVisible(true);
        return true;
    }

    @Override
    public void endArena(Arena arena) {
        for (ArenaScoreboard scoreBoard : ultimateGames.getScoreboardManager().getArenaScoreboards(arena)) {
            if (scoreBoard.getName().equals("Kills")) {
                Integer teamOneScore = scoreBoard.getScore(ChatColor.BLUE + "Team Blue");
                Integer teamTwoScore = scoreBoard.getScore(ChatColor.RED + "Team Red");
                if (teamOneScore > teamTwoScore) {
                    ultimateGames.getMessageManager().broadcastReplacedGameMessage(game, "GameEnd", "Team Blue", game.getGameDescription().getName(), arena.getName());
                } else if (teamOneScore < teamTwoScore) {
                    ultimateGames.getMessageManager().broadcastReplacedGameMessage(game, "GameEnd", "Team Red", game.getGameDescription().getName(), arena.getName());
                } else {
                    ultimateGames.getMessageManager().broadcastReplacedGameMessage(game, "GameTie", "Team Blue", "Team Red", game.getGameDescription().getName(), arena.getName());
                }
            }
        }
        ultimateGames.getScoreboardManager().removeArenaScoreboard(arena, "Kills");
        teamBlue.put(arena, new ArrayList<String>());
        teamBlue.put(arena, new ArrayList<String>());
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

    @Override
    public Boolean addPlayer(Arena arena, String playerName) {
        if (arena.getStatus() == ArenaStatus.OPEN && arena.getPlayers().size() >= arena.getMinPlayers() && !ultimateGames.getCountdownManager().isStartingCountdownEnabled(arena)) {
            ultimateGames.getCountdownManager().createStartingCountdown(arena, ultimateGames.getConfigManager().getGameConfig(game).getConfig().getInt("CustomValues.StartWaitTime"));
        }
        SpawnPoint spawnPoint = ultimateGames.getSpawnpointManager().getRandomSpawnPoint(arena);
        spawnPoint.lock(false);
        spawnPoint.teleportPlayer(playerName);
        Player player = Bukkit.getPlayerExact(playerName);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        return true;
    }

    @Override
    public Boolean removePlayer(Arena arena, String playerName) {
        return true;
    }

    @Override
    public void onPlayerDeath(Arena arena, PlayerDeathEvent event) {
        if (arena.getStatus() == ArenaStatus.RUNNING) {
            Player killer = event.getEntity().getKiller();
            String killerName = null;
            if (killer != null) {
                killerName = killer.getName();
                if (ultimateGames.getPlayerManager().isPlayerInArena(killer.getName()) && ultimateGames.getPlayerManager().getPlayerArena(killer.getName()).equals(arena)) {
                    killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 2));
                }
                for (ArenaScoreboard scoreBoard : ultimateGames.getScoreboardManager().getArenaScoreboards(arena)) {
                    if (scoreBoard.getName().equals("Kills") && killerName != null) {
                        if (teamBlue.get(arena).contains(killerName)) {
                            scoreBoard.setScore(ChatColor.BLUE + "Team Blue", scoreBoard.getScore(ChatColor.BLUE + "Team Blue") + 1);
                        } else if (teamRed.get(arena).contains(killerName)) {
                            scoreBoard.setScore(ChatColor.RED + "Team Red", scoreBoard.getScore(ChatColor.RED + "Team Red") + 1);
                        }
                    }
                }
                ultimateGames.getMessageManager().broadcastReplacedGameMessageToArena(game, arena, "Kill", killerName, event.getEntity().getName());
            } else {
                ultimateGames.getMessageManager().broadcastReplacedGameMessageToArena(game, arena, "Death", event.getEntity().getName());
            }
        }
        event.getDrops().clear();
        ultimateGames.getUtils().autoRespawn(event.getEntity());
    }

    @Override
    public void onPlayerRespawn(Arena arena, PlayerRespawnEvent event) {
        event.setRespawnLocation(ultimateGames.getSpawnpointManager().getSpawnPoint(arena, teamBlue.get(arena).contains(event.getPlayer().getName()) ? 0 : 1).getLocation());
        resetInventory(event.getPlayer());
    }

    @Override
    public void onEntityDamage(Arena arena, EntityDamageEvent event) {
        if (arena.getStatus() != ArenaStatus.RUNNING) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onEntityDamageByEntity(Arena arena, EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            String playerName = player.getName();
            Player damager;
            String damagerName = "";
            Entity entity = event.getDamager();
            if (entity instanceof Arrow) {
                LivingEntity shooter = ((Arrow) entity).getShooter();
                if (shooter instanceof Player) {
                    damager = (Player) shooter;
                    damagerName = damager.getName();
                    if (!ultimateGames.getPlayerManager().isPlayerInArena(damagerName) || !ultimateGames.getPlayerManager().getPlayerArena(damagerName).equals(arena)) {
                        event.setCancelled(true);
                    }
                }
            } else if (entity instanceof Player) {
                damager = (Player) entity;
                damagerName = damager.getName();
            }
            if ((teamBlue.get(arena).contains(playerName) && teamBlue.get(arena).contains(damagerName)) || (teamRed.get(arena).contains(playerName) && teamRed.get(arena).contains(damagerName))) {
                event.setCancelled(true);
            }
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

    @SuppressWarnings("deprecation")
    private void resetInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1), new ItemStack(Material.BOW, 1), new ItemStack(Material.ARROW, 32));
        String playerName = player.getName();
        if (ultimateGames.getPlayerManager().isPlayerInArena(playerName)) {
            Arena arena = ultimateGames.getPlayerManager().getPlayerArena(playerName);
            player.getInventory().addItem(ultimateGames.getUtils().createInstructionBook(arena.getGame()));
            Color color = teamBlue.get(arena).contains(playerName) ? Color.BLUE : Color.RED;
            ItemStack helmet = ultimateGames.getUtils().colorArmor(new ItemStack(Material.LEATHER_HELMET, 1), color);
            ItemStack chestplate = ultimateGames.getUtils().colorArmor(new ItemStack(Material.LEATHER_CHESTPLATE, 1), color);
            ItemStack leggings = ultimateGames.getUtils().colorArmor(new ItemStack(Material.LEATHER_LEGGINGS, 1), color);
            ItemStack boots = ultimateGames.getUtils().colorArmor(new ItemStack(Material.LEATHER_BOOTS, 1), color);
            player.getInventory().setArmorContents(new ItemStack[] { boots, leggings, chestplate, helmet });
        }
        player.updateInventory();
    }

}
