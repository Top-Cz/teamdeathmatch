package me.ampayne2.teamdeathmatch;

import me.ampayne2.ultimategames.api.UltimateGames;
import me.ampayne2.ultimategames.api.arenas.Arena;
import me.ampayne2.ultimategames.api.arenas.ArenaStatus;
import me.ampayne2.ultimategames.api.events.players.PlayerPostJoinEvent;
import me.ampayne2.ultimategames.api.games.Game;
import me.ampayne2.ultimategames.api.games.items.GameItem;
import me.ampayne2.ultimategames.api.utils.IconMenu;
import ninja.amp.ampeffects.effects.sounds.SoundEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Killcoin item.
 */
public class Killcoin extends GameItem implements Listener {
    private final UltimateGames ultimateGames;
    private final Game game;
    private final TeamDeathmatch teamDeathmatch;
    private final Map<String, Integer> playerCoins = new HashMap<>();
    private final Map<String, IconMenu> perkMenus = new HashMap<>();
    private static final ItemStack ITEM;
    private static final SoundEffect OPEN_SOUND = new SoundEffect(Sound.CHEST_OPEN, 1, 1);

    public Killcoin(UltimateGames ultimateGames, Game game, TeamDeathmatch teamDeathmatch) {
        super(ITEM, false);
        this.ultimateGames = ultimateGames;
        this.game = game;
        this.teamDeathmatch = teamDeathmatch;

        Bukkit.getServer().getPluginManager().registerEvents(this, ultimateGames.getPlugin());
    }

    @Override
    public boolean click(Arena arena, PlayerInteractEvent event) {
        if (arena.getStatus() == ArenaStatus.RUNNING) {
            getPerkMenu(arena, event.getPlayer()).open(event.getPlayer());
            OPEN_SOUND.play(event.getPlayer().getLocation(), event.getPlayer());
        }
        return true;
    }

    /**
     * Adds a certain amount of killcoins to a player's inventory.
     *
     * @param playerName The player's name.
     * @param amount     The amount.
     */
    public void addCoins(String playerName, int amount) {
        if (playerCoins.containsKey(playerName)) {
            playerCoins.put(playerName, playerCoins.get(playerName) + amount);
        } else {
            playerCoins.put(playerName, amount);
        }
    }

    /**
     * Gets the amount of killcoins a player has.
     *
     * @param playerName The player's name.
     * @return The amount of killcoins the player has.
     */
    public int getCoins(String playerName) {
        return playerCoins.containsKey(playerName) ? playerCoins.get(playerName) : 0;
    }

    /**
     * Adds one killcoin to a player's inventory.
     *
     * @param playerName The player's name.
     */
    public void addCoin(String playerName) {
        addCoins(playerName, 1);
    }

    /**
     * Removes a certain amount of killcoins from a player's inventory.
     *
     * @param playerName The player's name.
     * @param amount     The amount.
     */
    public void removeCoins(String playerName, int amount) {
        if (playerCoins.containsKey(playerName)) {
            int coins = playerCoins.get(playerName) - amount;
            if (coins <= 0) {
                playerCoins.remove(playerName);
            } else {
                playerCoins.put(playerName, coins);
            }
        }
    }

    /**
     * Resets a player's killcoins.
     *
     * @param playerName The player's name.
     */
    public void resetCoins(String playerName) {
        if (playerCoins.containsKey(playerName)) {
            playerCoins.remove(playerName);
        }
    }

    /**
     * Updates a player's killcoins.
     *
     * @param player The player.
     */
    @SuppressWarnings("deprecation")
    public void updateCoins(Player player) {
        String playerName = player.getName();
        if (playerCoins.containsKey(playerName)) {
            ItemStack coins = getItem();
            coins.setAmount(playerCoins.get(playerName));
            player.getInventory().setItem(7, coins);
            player.updateInventory();
        } else {
            player.getInventory().setItem(7, null);
            player.updateInventory();
        }
    }

    /**
     * Gets a player's perk menu.
     *
     * @param arena  The arena.
     * @param player The player.
     * @return The player's perk menu.
     */
    public IconMenu getPerkMenu(final Arena arena, Player player) {
        final String playerName = player.getName();
        if (perkMenus.containsKey(playerName)) {
            return perkMenus.get(playerName);
        }

        final List<KillcoinPerk> killcoinPerks = new ArrayList<>();
        for (KillcoinPerk killcoinPerk : KillcoinPerk.class.getEnumConstants()) {
            if (killcoinPerk.showInMenu() && (killcoinPerk.getPerkName() == null || ultimateGames.getPointManager().hasPerk(game, playerName, killcoinPerk.getPerkName()))) {
                killcoinPerks.add(killcoinPerk);
            }
        }
        IconMenu menu = new IconMenu(ChatColor.GOLD + "Spoils of War", IconMenu.getRequiredSize(killcoinPerks.size()), new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                KillcoinPerk killcoinPerk = killcoinPerks.get(event.getPosition());
                if (getCoins(playerName) < killcoinPerk.getCost()) {
                    ultimateGames.getMessenger().sendGameMessage(event.getPlayer(), game, TDMessage.PERK_NOTENOUGHCOINS, killcoinPerk.getName());
                } else if (killcoinPerk.isActivated(playerName)) {
                    ultimateGames.getMessenger().sendGameMessage(event.getPlayer(), game, TDMessage.PERK_ALREADYACTIVE, killcoinPerk.getName());
                } else {
                    if (killcoinPerk.canActivate(ultimateGames, teamDeathmatch, arena, event.getPlayer())) {
                        killcoinPerk.activate(ultimateGames, teamDeathmatch, arena, event.getPlayer());
                        removeCoins(playerName, killcoinPerk.getCost());
                        updateCoins(event.getPlayer());
                    } else {
                        ultimateGames.getMessenger().sendGameMessage(event.getPlayer(), game, TDMessage.PERK_CANNOTACTIVATE, killcoinPerk.getName());
                    }
                }
            }
        }, ultimateGames.getPlugin());
        menu.setSpecificTo(player);
        for (int i = 0; i < killcoinPerks.size(); i++) {
            KillcoinPerk killcoinPerk = killcoinPerks.get(i);
            menu.setOption(i, killcoinPerk.getIcon(), ChatColor.AQUA + killcoinPerk.getName(), ChatColor.GOLD + "Cost: " + killcoinPerk.getCost());
        }
        return menu;
    }

    @EventHandler
    public void onPlayerJoin(PlayerPostJoinEvent event) {
        Arena arena = event.getArena();
        Player player = event.getPlayer();
        String playerName = player.getName();
        if (perkMenus.containsKey(playerName)) {
            perkMenus.get(playerName).destroy();
            perkMenus.remove(playerName);
        }
        perkMenus.put(playerName, getPerkMenu(arena, player));
    }

    static {
        ITEM = new ItemStack(Material.EMERALD);
        ItemMeta meta = ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD.toString() + ChatColor.BOLD + "Spoils of War");
        ITEM.setItemMeta(meta);
    }
}
