package me.ampayne2.teamdeathmatch;

import me.ampayne2.ultimategames.api.UltimateGames;
import me.ampayne2.ultimategames.api.arenas.Arena;
import me.ampayne2.ultimategames.api.arenas.ArenaStatus;
import me.ampayne2.ultimategames.api.arenas.scoreboards.Scoreboard;
import me.ampayne2.ultimategames.api.arenas.spawnpoints.PlayerSpawnPoint;
import me.ampayne2.ultimategames.api.games.Game;
import me.ampayne2.ultimategames.api.games.GamePlugin;
import me.ampayne2.ultimategames.api.games.items.GameItem;
import me.ampayne2.ultimategames.api.message.UGMessage;
import me.ampayne2.ultimategames.api.players.teams.Team;
import me.ampayne2.ultimategames.api.players.teams.TeamManager;
import me.ampayne2.ultimategames.api.players.teams.TeamSelector;
import me.ampayne2.ultimategames.api.utils.UGUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.List;

public class TeamDeathmatch extends GamePlugin {
    private UltimateGames ultimateGames;
    private Game game;

    private GameItem teamSelector;
    private Killcoin killcoin;

    @Override
    public boolean loadGame(UltimateGames ultimateGames, Game game) {
        this.ultimateGames = ultimateGames;
        this.game = game;
        game.setMessages(TDMessage.class);

        teamSelector = new TeamSelector(ultimateGames);
        killcoin = new Killcoin(ultimateGames, game, this);
        ultimateGames.getGameItemManager()
                .registerGameItem(game, teamSelector)
                .registerGameItem(game, killcoin)
                .registerGameItem(game, new Flashbang(ultimateGames));

        return true;
    }

    @Override
    public void unloadGame() {
    }

    @Override
    public boolean reloadGame() {
        return true;
    }

    @Override
    public boolean stopGame() {
        return true;
    }

    @Override
    public boolean loadArena(Arena arena) {
        TeamManager teamManager = ultimateGames.getTeamManager();
        teamManager.createTeam(ultimateGames, "Blue", arena, ChatColor.BLUE, false, true);
        teamManager.createTeam(ultimateGames, "Red", arena, ChatColor.RED, false, true);
        ultimateGames.addAPIHandler("/" + game.getName() + "/" + arena.getName(), new TeamDeathmatchWebHandler(ultimateGames, arena));
        return true;
    }

    @Override
    public boolean unloadArena(Arena arena) {
        ultimateGames.getTeamManager().removeTeamsOfArena(arena);
        return true;
    }

    @Override
    public boolean isStartPossible(Arena arena) {
        return arena.getStatus() == ArenaStatus.OPEN;
    }

    @Override
    public boolean startArena(Arena arena) {
        return true;
    }

    @Override
    public boolean beginArena(Arena arena) {
        // Creates a new ending countdown
        ultimateGames.getCountdownManager().createEndingCountdown(arena, ultimateGames.getConfigManager().getGameConfig(game).getInt("CustomValues.GameTime"), true);

        // Creates a new arena scoreboard and adds team blue and red
        Scoreboard scoreBoard = ultimateGames.getScoreboardManager().createScoreboard(arena, "Kills");
        scoreBoard.setScore(ChatColor.BLUE + "Team Blue", 0);
        scoreBoard.setScore(ChatColor.RED + "Team Red", 0);
        scoreBoard.setVisible(true);

        TeamManager teamManager = ultimateGames.getTeamManager();
        teamManager.sortPlayersIntoTeams(arena);
        Team blue = teamManager.getTeam(arena, "Blue");
        Team red = teamManager.getTeam(arena, "Red");
        for (String playerName : blue.getPlayers()) {
            Player player = Bukkit.getPlayerExact(playerName);
            scoreBoard.addPlayer(player, blue);
            PlayerSpawnPoint spawnPoint = ultimateGames.getSpawnpointManager().getSpawnPoint(arena, 0);
            spawnPoint.lock(false);
            spawnPoint.teleportPlayer(player);
        }
        for (String playerName : red.getPlayers()) {
            Player player = Bukkit.getPlayerExact(playerName);
            scoreBoard.addPlayer(player, red);
            PlayerSpawnPoint spawnPoint = ultimateGames.getSpawnpointManager().getSpawnPoint(arena, 1);
            spawnPoint.lock(false);
            spawnPoint.teleportPlayer(player);
        }

        for (String playerName : arena.getPlayers()) {
            if (ultimateGames.getPointManager().hasPerk(game, playerName, "StartWithCoins")) {
                killcoin.addCoins(playerName, 5);
            }
            resetInventory(Bukkit.getPlayerExact(playerName), arena);
        }
        return true;
    }

    @Override
    public void endArena(Arena arena) {
        for (String playerName : arena.getPlayers()) {
            killcoin.resetCoins(playerName);
            KillcoinPerk.deactivateAll(ultimateGames, arena, Bukkit.getPlayerExact(playerName));
        }
        Scoreboard scoreBoard = ultimateGames.getScoreboardManager().getScoreboard(arena);
        if (scoreBoard != null) {
            Integer teamOneScore = scoreBoard.getScore(ChatColor.BLUE + "Team Blue");
            Integer teamTwoScore = scoreBoard.getScore(ChatColor.RED + "Team Red");
            if (teamOneScore > teamTwoScore) {
                ultimateGames.getMessenger().sendGameMessage(Bukkit.getServer(), game, TDMessage.GAME_END, "Team Blue", game.getName(), arena.getName());
                for (String player : ultimateGames.getTeamManager().getTeam(arena, "Blue").getPlayers()) {
                    ultimateGames.getPointManager().addPoint(game, player, "store", 25);
                    ultimateGames.getPointManager().addPoint(game, player, "win", 1);
                }
                for (String player : ultimateGames.getTeamManager().getTeam(arena, "Red").getPlayers()) {
                    ultimateGames.getPointManager().addPoint(game, player, "store", 5);
                }
            } else if (teamOneScore < teamTwoScore) {
                ultimateGames.getMessenger().sendGameMessage(Bukkit.getServer(), game, TDMessage.GAME_END, "Team Red", game.getName(), arena.getName());
                for (String player : ultimateGames.getTeamManager().getTeam(arena, "Red").getPlayers()) {
                    ultimateGames.getPointManager().addPoint(game, player, "store", 25);
                    ultimateGames.getPointManager().addPoint(game, player, "win", 1);
                }
                for (String player : ultimateGames.getTeamManager().getTeam(arena, "Blue").getPlayers()) {
                    ultimateGames.getPointManager().addPoint(game, player, "store", 5);
                }
            } else {
                ultimateGames.getMessenger().sendGameMessage(Bukkit.getServer(), game, TDMessage.GAME_TIE, "Team Blue", "Team Red", game.getName(), arena.getName());
            }
        }
    }

    @Override
    public boolean openArena(Arena arena) {
        return true;
    }

    @Override
    public boolean stopArena(Arena arena) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean addPlayer(Player player, Arena arena) {
        if (arena.getStatus() == ArenaStatus.OPEN && arena.getPlayers().size() >= arena.getMinPlayers() && !ultimateGames.getCountdownManager().hasStartingCountdown(arena)) {
            ultimateGames.getCountdownManager().createStartingCountdown(arena, ultimateGames.getConfigManager().getGameConfig(game).getInt("CustomValues.StartWaitTime"));
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
                teamManager.setPlayerTeam(newPlayer, team);
            }
        }
        if (arena.getStatus() == ArenaStatus.RUNNING && (teamManager.getTeam(arena, "Red").getPlayers().size() <= 0 || teamManager.getTeam(arena, "Blue").getPlayers().size() <= 0)) {
            ultimateGames.getArenaManager().endArena(arena);
        }
        killcoin.resetCoins(playerName);
        KillcoinPerk.deactivateAll(ultimateGames, arena, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean addSpectator(Player player, Arena arena) {
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
                String killerName = killer.getName();
                ultimateGames.getMessenger().sendGameMessage(arena, game, TDMessage.KILL, killerName, playerName);
                ultimateGames.getPointManager().addPoint(game, killerName, "kill", 1);
                ultimateGames.getPointManager().addPoint(game, killerName, "store", 2);
                if (KillcoinPerk.DOUBLE_KILLCOINS.isActivated(killerName)) {
                    killcoin.addCoins(killerName, 2);
                } else {
                    killcoin.addCoin(killerName);
                }
            } else {
                ultimateGames.getMessenger().sendGameMessage(arena, game, TDMessage.DEATH, playerName);
            }
            killcoin.updateCoins(killer);
            ultimateGames.getPointManager().addPoint(game, playerName, "death", 1);
            Scoreboard scoreBoard = ultimateGames.getScoreboardManager().getScoreboard(arena);
            if (scoreBoard != null) {
                Team team = ultimateGames.getTeamManager().getPlayerTeam(playerName);
                if (team.getName().equals("Red")) {
                    scoreBoard.setScore(ChatColor.BLUE + "Team Blue", scoreBoard.getScore(ChatColor.BLUE + "Team Blue") + 1);
                } else if (team.getName().equals("Blue")) {
                    scoreBoard.setScore(ChatColor.RED + "Team Red", scoreBoard.getScore(ChatColor.RED + "Team Red") + 1);
                }
            }
        }
        KillcoinPerk.deactivateAll(ultimateGames, arena, event.getEntity());
        event.getDrops().clear();
        UGUtils.autoRespawn(ultimateGames.getPlugin(), event.getEntity());
    }

    @Override
    public void onPlayerRespawn(Arena arena, PlayerRespawnEvent event) {
        event.setRespawnLocation(ultimateGames.getSpawnpointManager().getSpawnPoint(arena, ultimateGames.getTeamManager().getPlayerTeam(event.getPlayer().getName()).getName().equals("Blue") ? 0 : 1).getLocation());
        resetInventory(event.getPlayer(), arena);
    }

    @Override
    public void onEntityDamage(Arena arena, EntityDamageEvent event) {
        if (arena.getStatus() == ArenaStatus.RUNNING) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                switch (event.getCause()) {
                    case FALL:
                    case FIRE:
                    case FIRE_TICK:
                    case POISON:
                    case SUICIDE:
                    case WITHER:
                        player.getWorld().playEffect(player.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                        break;
                    default:
                        player.getWorld().playEffect(player.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                }
            }
        } else {
            event.setCancelled(true);
        }
    }

    @Override
    public void onEntityDamageByEntity(Arena arena, EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Wolf) {
            AnimalTamer animalTamer = ((Wolf) event.getEntity()).getOwner();
            if (animalTamer != null && animalTamer instanceof Player) {
                Player damager = null;
                if (event.getDamager() instanceof Player) {
                    damager = (Player) event.getDamager();
                } else if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
                    damager = (Player) ((Arrow) event.getDamager()).getShooter();
                }
                if (damager != null) {
                    Player owner = (Player) animalTamer;
                    Team team = ultimateGames.getTeamManager().getPlayerTeam(owner.getName());
                    if (team != null && team.hasPlayer(damager.getName())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlayerInteract(Arena arena, PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (arena.getStatus() == ArenaStatus.RUNNING) {
                Player player = event.getPlayer();
                ItemStack item = event.getItem();
                if (item.getType() == Material.BOW) {
                    if (!player.getInventory().contains(Material.ARROW)) {
                        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                            ultimateGames.getMessenger().sendGameMessage(player, game, TDMessage.OUT_OF_ARROWS);
                        } else if (event.getAction().equals(Action.LEFT_CLICK_AIR)) {
                            if (killcoin.getCoins(player.getName()) >= KillcoinPerk.ARROWS.getCost()) {
                                KillcoinPerk.ARROWS.activate(ultimateGames, this, arena, player);
                            } else {
                                ultimateGames.getMessenger().sendGameMessage(player, game, TDMessage.PERK_NOTENOUGHCOINS, KillcoinPerk.ARROWS.getName());
                            }
                        }
                    }
                } else {
                    for (KillcoinPerk killcoinPerk : KillcoinPerk.class.getEnumConstants()) {
                        if (!killcoinPerk.showInMenu() && item.getType() == killcoinPerk.getIcon().getType()) {
                            if (item.getType() == Material.POTION) {
                                Potion potion = Potion.fromItemStack(item);
                                if (!((potion.getType() == PotionType.INSTANT_DAMAGE && killcoinPerk == KillcoinPerk.DAMAGE_POTION) || (potion.getType() == PotionType.POISON && killcoinPerk == KillcoinPerk.POISON_POTION))) {
                                    continue;
                                }
                            }

                            String playerName = player.getName();
                            if (killcoin.getCoins(playerName) < killcoinPerk.getCost()) {
                                ultimateGames.getMessenger().sendGameMessage(player, game, TDMessage.PERK_NOTENOUGHCOINS, killcoinPerk.getName());
                            } else if (killcoinPerk.isActivated(playerName)) {
                                ultimateGames.getMessenger().sendGameMessage(player, game, TDMessage.PERK_ALREADYACTIVE, killcoinPerk.getName());
                            } else {
                                if (killcoinPerk.canActivate(ultimateGames, this, arena, player)) {
                                    killcoinPerk.activate(ultimateGames, this, arena, player);
                                    killcoin.removeCoins(playerName, killcoinPerk.getCost());
                                    killcoin.updateCoins(player);
                                    return;
                                } else {
                                    ultimateGames.getMessenger().sendGameMessage(player, game, TDMessage.PERK_CANNOTACTIVATE, killcoinPerk.getName());
                                }
                            }
                            event.setCancelled(true);
                            player.updateInventory();
                            break;
                        }
                    }
                }
            } else {
                event.setCancelled(true);
                event.getPlayer().updateInventory();
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

    @Override
    public void onArenaCommand(Arena arena, String command, CommandSender sender, String[] args) {
        if (arena.getStatus() == ArenaStatus.RUNNING && (command.equalsIgnoreCase("shout") || command.equalsIgnoreCase("s"))) {
            Player player = (Player) sender;
            String playerName = player.getName();
            ChatColor teamColor = ChatColor.WHITE;
            if (ultimateGames.getTeamManager().isPlayerInTeam(playerName)) {
                teamColor = ultimateGames.getTeamManager().getPlayerTeam(playerName).getColor();
            }
            StringBuilder message = new StringBuilder();
            for (String s : args) {
                message.append(s);
                message.append(" ");
            }
            ultimateGames.getMessenger().sendRawMessage(arena, ChatColor.DARK_GRAY + "[S]" +  String.format(UGMessage.CHAT.getMessage(), teamColor + playerName, message.toString()));
        }
    }

    @SuppressWarnings("deprecation")
    private void resetInventory(Player player, Arena arena) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setItem(0, new ItemStack(Material.IRON_SWORD));
        inventory.setItem(1, new ItemStack(Material.BOW));
        inventory.setItem(3, KillcoinPerk.DAMAGE_POTION.getMenuIcon().clone());
        inventory.setItem(4, KillcoinPerk.POISON_POTION.getMenuIcon().clone());
        killcoin.updateCoins(player);
        inventory.setItem(8, UGUtils.createInstructionBook(game));
        inventory.setItem(9, new ItemStack(Material.ARROW, 32));
        String playerName = player.getName();
        if (ultimateGames.getPlayerManager().isPlayerInArena(playerName)) {
            /*
            if (arena.getStatus() == ArenaStatus.OPEN || arena.getStatus() == ArenaStatus.STARTING) {
                player.getInventory().addItem(TEAM_SELECTOR.getItem());
            }
            */
            Color color = ultimateGames.getTeamManager().getPlayerTeam(playerName).getName().equals("Blue") ? Color.BLUE : Color.RED;
            ItemStack helmet = UGUtils.colorArmor(new ItemStack(Material.LEATHER_HELMET), color);
            ItemStack chestplate = UGUtils.colorArmor(new ItemStack(Material.LEATHER_CHESTPLATE), color);
            ItemStack leggings = UGUtils.colorArmor(new ItemStack(Material.LEATHER_LEGGINGS), color);
            ItemStack boots = UGUtils.colorArmor(new ItemStack(Material.LEATHER_BOOTS), color);
            player.getInventory().setArmorContents(new ItemStack[]{boots, leggings, chestplate, helmet});
        }
        player.updateInventory();
    }
}
