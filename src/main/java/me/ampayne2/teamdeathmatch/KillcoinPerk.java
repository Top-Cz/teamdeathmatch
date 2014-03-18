package me.ampayne2.teamdeathmatch;

import me.ampayne2.ultimategames.api.UltimateGames;
import me.ampayne2.ultimategames.api.arenas.Arena;
import me.ampayne2.ultimategames.api.effects.GameSound;
import me.ampayne2.ultimategames.api.utils.UGUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.HashSet;
import java.util.Set;

/**
 * Deathmatch Killcoin Perks.
 */
public enum KillcoinPerk {
    FLASHBANG("Flashbang", "Blind players standing near the flashbang!", UGUtils.nameItem(new ItemStack(Material.CLAY_BALL), ChatColor.GOLD.toString() + ChatColor.BOLD + "Flashbang"), 1, false) {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().addItem(getIcon().clone());
        }
    },
    ARROWS("Extra Arrows", "Get 16 more arrows!", new ItemStack(Material.ARROW, 16), 2, false) {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().addItem(getIcon().clone());
        }
    },
    DAMAGE_POTION("Damage Potion", "Get a Damage Potion!", new ItemStack(Material.POTION), 3, false) {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().addItem(getIcon().clone());
        }
    },
    POISON_POTION("Poison Potion", "Get a Poison Potion!", new ItemStack(Material.POTION), 3, false) {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().addItem(getIcon().clone());
        }
    },
    LONGBOW("Longbow", "Increase your bows power!", new ItemStack(Material.BOW), 4, true, "Longbow") {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().remove(Material.BOW);
            player.getInventory().addItem(getIcon().clone());
        }
    },
    BROADSWORD("Broadsword", "Increase your sword strength!", new ItemStack(Material.DIAMOND_SWORD), 5, true, "Broadsword") {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().remove(Material.IRON_SWORD);
            player.getInventory().addItem(getIcon().clone());
        }
    },
    TANK("Tank", "Increase your damage resistance!", new ItemStack(Material.DIAMOND_CHESTPLATE), 10, true, "Tank") {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
            player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
            player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
            player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
        }
    },
    @SuppressWarnings("deprecation")
    SPY("Spy", "Disguise as an enemy!", new ItemStack(397, 1, (short) 3), 5, true, "Spy") {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            Color color = ultimateGames.getTeamManager().getPlayerTeam(player.getName()).getName().equals("Blue") ? Color.RED : Color.BLUE;
            player.getInventory().setHelmet(UGUtils.colorArmor(new ItemStack(Material.LEATHER_HELMET), color));
            player.getInventory().setChestplate(UGUtils.colorArmor(new ItemStack(Material.LEATHER_CHESTPLATE), color));
            player.getInventory().setLeggings(UGUtils.colorArmor(new ItemStack(Material.LEATHER_LEGGINGS), color));
            player.getInventory().setBoots(UGUtils.colorArmor(new ItemStack(Material.LEATHER_BOOTS), color));
        }
    };

    private final String name;
    private final String description;
    private final ItemStack icon;
    private final int cost;
    private final String perkName;
    private final boolean activePerk;

    /**
     * Players with the perk currently active.
     */
    private final Set<String> players = new HashSet<>();

    private static final GameSound ACTIVATE_SOUND = new GameSound(Sound.HORSE_ARMOR, 1, 1.5F);

    /**
     * Creates a perk that must be unlocked.
     *
     * @param name        The display name.
     * @param description The description.
     * @param icon        The icon.
     * @param cost        The cost.
     * @param activePerk  If the perk stays active when used.
     * @param perkName    The perk name.
     */
    private KillcoinPerk(String name, String description, ItemStack icon, int cost, boolean activePerk, String perkName) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.cost = cost;
        this.activePerk = activePerk;
        this.perkName = perkName;
    }

    /**
     * Creates a perk that doesn't need to be unlocked.
     *
     * @param name        The display name.
     * @param description The description.
     * @param icon        The icon.
     * @param cost        The cost.
     * @param activePerk  If the perk stays active when used.
     */
    private KillcoinPerk(String name, String description, ItemStack icon, int cost, boolean activePerk) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.cost = cost;
        this.activePerk = activePerk;
        this.perkName = null;
    }

    /**
     * Gets the perk's display name.
     *
     * @return The perk's display name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the perk's description.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the perk's icon.
     *
     * @return The icon.
     */
    public ItemStack getIcon() {
        return icon;
    }

    /**
     * Gets the perk's cost.
     *
     * @return The cost.
     */
    public int getCost() {
        return cost;
    }

    /**
     * Checks if the perk stays active after used.
     *
     * @return True if the perk is an active perk, else false.
     */
    public boolean isActivePerk() {
        return activePerk;
    }

    /**
     * Gets the perk's perk name.
     *
     * @return The perk name.
     */
    public String getPerkName() {
        return perkName;
    }

    /**
     * Checks if the perk is already activated for a player.
     *
     * @param playerName The player's name.
     * @return True if the perk is already activated, else false.
     */
    public boolean isActivated(String playerName) {
        return players.contains(playerName);
    }

    /**
     * Activates a perk for a player.
     *
     * @param ultimateGames  The UltimateGames instance.
     * @param teamDeathmatch The TeamDeathmatch instance.
     * @param arena          The arena.
     * @param player         The player.
     */
    public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
        if (activePerk) {
            players.add(player.getName());
        }
        ultimateGames.getMessenger().sendGameMessage(player, arena.getGame(), TDMessage.PERK_ACTIVATE, name);
        ACTIVATE_SOUND.play(player, player.getLocation());
    }

    /**
     * Deactivates a perk for a player.
     *
     * @param ultimateGames The UltimateGames instance.
     * @param arena         The arena.
     * @param player        The player.
     */
    public void deactivate(UltimateGames ultimateGames, Arena arena, Player player) {
        if (players.contains(player.getName())) {
            players.remove(player.getName());
            ultimateGames.getMessenger().sendGameMessage(player, arena.getGame(), TDMessage.PERK_DEACTIVATE, name);
        }
    }

    /**
     * Deactivates all of a player's active perks.
     *
     * @param ultimateGames The UltimateGames instance.
     * @param arena         The arena.
     * @param player        The player.
     */
    public static void deactivateAll(UltimateGames ultimateGames, Arena arena, Player player) {
        for (KillcoinPerk killcoinPerk : KillcoinPerk.class.getEnumConstants()) {
            if (killcoinPerk.isActivePerk()) {
                killcoinPerk.deactivate(ultimateGames, arena, player);
            }
        }
    }

    static {
        Potion damage = new Potion(PotionType.INSTANT_DAMAGE, 1);
        damage.setSplash(true);
        damage.apply(DAMAGE_POTION.getIcon());

        Potion poison = new Potion(PotionType.POISON, 1);
        poison.setSplash(true);
        poison.apply(POISON_POTION.getIcon());

        LONGBOW.getIcon().addEnchantment(Enchantment.ARROW_DAMAGE, 2);

        BROADSWORD.getIcon().addEnchantment(Enchantment.DAMAGE_ALL, 1);
    }
}
