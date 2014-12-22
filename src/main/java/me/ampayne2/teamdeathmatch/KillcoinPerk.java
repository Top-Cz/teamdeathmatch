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

import me.ampayne2.ultimategames.api.UltimateGames;
import me.ampayne2.ultimategames.api.arenas.Arena;
import me.ampayne2.ultimategames.api.utils.UGUtils;
import ninja.amp.ampeffects.effects.sounds.SoundEffect;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.HashSet;
import java.util.Set;

/**
 * Deathmatch Killcoin Perks.
 */
public enum KillcoinPerk {

    /**
     * Item Related Perks
     */

    FLASHBANG("Flashbang", "Blind players standing near the flashbang!", UGUtils.nameItem(new ItemStack(Material.CLAY_BALL), ChatColor.GOLD.toString() + ChatColor.BOLD + "Flashbang"), 1, false, true) {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().addItem(getIcon().clone());
        }
    },
    DAMAGE_POTION("Damage Potion", "Throw a Damage Potion!", new ItemStack(Material.POTION), 2, false, false) {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.setItemInHand(UGUtils.setAmount(player.getItemInHand(), 2));
        }
    },
    POISON_POTION("Poison Potion", "Throw a Poison Potion!", new ItemStack(Material.POTION), 2, false, false) {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.setItemInHand(UGUtils.setAmount(player.getItemInHand(), 2));
        }
    },
    HEALTH_POTION("Health Potion", "Get a Health Potion!", new ItemStack(Material.POTION), 1, false, true) {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().addItem(getIcon().clone());
        }
    },
    STRENGTH("Strength", "Increase your strength!", new ItemStack(Material.POTION), 8, false, true) {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            UGUtils.increasePotionEffect(player, PotionEffectType.INCREASE_DAMAGE);
        }
    },
    SPEED("Speed", "Get a speed boost!", new ItemStack(Material.FEATHER), 2, false, true) {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            UGUtils.increasePotionEffect(player, PotionEffectType.SPEED, 2);
        }
    },
    FIRE_RESISTANCE("Fire Resistance", "Become resistant to fire!", new ItemStack(Material.FIRE), 3, true, true) {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            UGUtils.increasePotionEffect(player, PotionEffectType.FIRE_RESISTANCE);
        }
    },
    ENDER_PEARL("Ender Pearl", "Get an Ender Pearl!", new ItemStack(Material.ENDER_PEARL), 3, false, true, "EnderPearl") {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().addItem(getIcon().clone());
        }
    },
    GOLDEN_APPLE("Golden Apple", "Get a Golden Apple!", new ItemStack(Material.GOLDEN_APPLE), 5, false, true, "GoldenApple") {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().addItem(getIcon().clone());
        }
    },

    /**
     * Bow Related Perks
     */

    ARROWS("Extra Arrows", "Get 32 more arrows!", new ItemStack(Material.ARROW, 32), 2, false, false) {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().addItem(getIcon().clone());
        }
    },
    LONGBOW("Longbow", "Increase your bow's power!", new ItemStack(Material.BOW), 3, true, true, "Longbow") {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().remove(Material.BOW);
            if (FIREBOW.isActivated(player.getName())) {
                ItemStack bow = getIcon().clone();
                bow.addEnchantment(Enchantment.ARROW_FIRE, 1);
                player.getInventory().addItem(bow);
            } else {
                player.getInventory().addItem(getIcon().clone());
            }
        }
    },
    FIREBOW("Firebow", "Set fire to your arrows!", new ItemStack(Material.BOW), 4, true, true, "Firebow") {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().remove(Material.BOW);
            if (LONGBOW.isActivated(player.getName())) {
                ItemStack bow = getIcon().clone();
                bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
                player.getInventory().addItem(bow);
            } else {
                player.getInventory().addItem(getIcon().clone());
            }
        }
    },

    /**
     * Sword Related Perks
     */

    FIRESWORD("Firesword", "Set fire to your sword!", new ItemStack(Material.IRON_SWORD), 4, true, true, "Firesword") {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            if (BROADSWORD.isActivated(player.getName())) {
                player.getInventory().remove(Material.DIAMOND_SWORD);
                ItemStack sword = BROADSWORD.getIcon().clone();
                sword.addEnchantment(Enchantment.FIRE_ASPECT, 1);
                player.getInventory().addItem(sword);
            } else {
                player.getInventory().remove(Material.IRON_SWORD);
                player.getInventory().addItem(getIcon().clone());
            }
        }
    },
    BROADSWORD("Broadsword", "Increase your sword's strength!", new ItemStack(Material.DIAMOND_SWORD), 5, true, true, "Broadsword") {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            if (FIRESWORD.isActivated(player.getName())) {
                player.getInventory().remove(Material.IRON_SWORD);
                ItemStack sword = FIRESWORD.getIcon().clone();
                sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
                player.getInventory().addItem(sword);
            } else {
                player.getInventory().remove(Material.IRON_SWORD);
                player.getInventory().addItem(getIcon().clone());
            }
        }
    },

    /**
     * Armor Related Perks
     */

    CACTUS_ARMOR("Cactus Armor", "Hurt enemies when they attack!", UGUtils.colorArmor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.fromRGB(127, 204, 25)), 4, true, true, "CactusArmor") {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            ItemStack helmet;
            ItemStack chestplate;
            ItemStack leggings;
            ItemStack boots;
            if (TANK.isActivated(player.getName())) {
                helmet = new ItemStack(Material.IRON_HELMET);
                chestplate = new ItemStack(Material.IRON_CHESTPLATE);
                leggings = new ItemStack(Material.IRON_LEGGINGS);
                boots = new ItemStack(Material.IRON_BOOTS);
            } else {
                Color color = Color.fromRGB(127, 204, 25);
                helmet = UGUtils.colorArmor(new ItemStack(Material.LEATHER_HELMET), color);
                chestplate = UGUtils.colorArmor(new ItemStack(Material.LEATHER_CHESTPLATE), color);
                leggings = UGUtils.colorArmor(new ItemStack(Material.LEATHER_LEGGINGS), color);
                boots = UGUtils.colorArmor(new ItemStack(Material.LEATHER_BOOTS), color);
            }
            UGUtils.enchantItems(Enchantment.THORNS, 5, helmet, chestplate, leggings, boots);
            player.getInventory().setHelmet(helmet);
            player.getInventory().setChestplate(chestplate);
            player.getInventory().setLeggings(leggings);
            player.getInventory().setBoots(boots);
        }
    },
    TANK("Tank", "Increase your damage resistance!", new ItemStack(Material.IRON_CHESTPLATE), 8, true, true, "Tank") {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            ItemStack helmet = new ItemStack(Material.IRON_HELMET);
            ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
            ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
            ItemStack boots = new ItemStack(Material.IRON_BOOTS);
            if (CACTUS_ARMOR.isActivated(player.getName())) {
                UGUtils.enchantItems(Enchantment.THORNS, 5, helmet, chestplate, leggings, boots);
            }
            player.getInventory().setHelmet(helmet);
            player.getInventory().setChestplate(chestplate);
            player.getInventory().setLeggings(leggings);
            player.getInventory().setBoots(boots);
        }
    },

    /**
     * Construction Perks
     */

    @SuppressWarnings("deprecation")
    BUILDING_BLOCKS("Building Blocks", "Build a base!", new ItemStack(Material.STAINED_CLAY, 32, DyeColor.GREEN.getWoolData()), 5, false, true) {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().addItem(getIcon().clone());
        }
    },

    PICKAXE("Iron Pickaxe", "Demolish player buildings!", new ItemStack(Material.IRON_PICKAXE), 2, false, true) {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getInventory().addItem(getIcon().clone());
        }
    },

    /**
     * Miscellaneous Perks
     */

    @SuppressWarnings("deprecation")
    SUN_SONG("Sun Song", "Clear the rainy weather!", new ItemStack(175), 3, false, true, "SunSong") {
        @Override
        public boolean canActivate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            return player.getWorld().hasStorm();
        }

        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getWorld().setStorm(false);
            ultimateGames.getMessenger().sendGameMessage(arena, arena.getGame(), TDMessage.SUN_SONG, player.getName());
        }
    },
    RAIN_DANCE("Rain Dance", "Bring on the downpour!", new ItemStack(Material.WATER_BUCKET), 3, false, true, "RainDance") {
        @Override
        public boolean canActivate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            return !player.getWorld().hasStorm();
        }

        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.getWorld().setStorm(true);
            ultimateGames.getMessenger().sendGameMessage(arena, arena.getGame(), TDMessage.RAIN_DANCE, player.getName());
        }
    },
    WOLF_TAMER("Wolf Tamer", "Spawn a loyal attack dog!", new ItemStack(Material.BONE), 5, false, true, "WolfTamer") {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            Wolf wolf = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
            wolf.setCollarColor(DyeColor.BLACK);
            wolf.setOwner(player);
            wolf.setAngry(true);
            UGUtils.increasePotionEffect(wolf, PotionEffectType.INCREASE_DAMAGE);
            UGUtils.increasePotionEffect(wolf, PotionEffectType.SPEED);
        }
    },
    INCREASE_MAX_HEALTH("+10 Max Health", "Get an extra row of hearts!", new ItemStack(Material.BED), 10, true, true, "IncreaseMaxHealth") {
        @Override
        public void activate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
            super.activate(ultimateGames, teamDeathmatch, arena, player);
            player.setMaxHealth(player.getMaxHealth() + 20);
            player.setHealth(player.getHealth() + 20);
        }

        @Override
        public void deactivate(UltimateGames ultimateGames, Arena arena, Player player) {
            if (isActivated(player.getName())) {
                player.setMaxHealth(player.getMaxHealth() - 20);
            }
            super.deactivate(ultimateGames, arena, player);
        }
    },
    DOUBLE_KILLCOINS("Double Killcoins", "Get 2 killcoins per kill until you die!", new ItemStack(Material.EMERALD), 6, true, true, "DoubleKillcoins"),
    @SuppressWarnings("deprecation")
    SPY("Spy", "Disguise as an enemy!", new ItemStack(397, 1, (short) 3), 5, true, true, "Spy") {
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
    private final ItemStack menuIcon;
    private final int cost;
    private final String perkName;
    private final boolean activePerk;
    private final boolean showInMenu;
    private SoundEffect activateSound = new SoundEffect(Sound.HORSE_ARMOR, 1, 1.5F);

    /**
     * Players with the perk currently active.
     */
    private final Set<String> players = new HashSet<>();

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
    private KillcoinPerk(String name, String description, ItemStack icon, int cost, boolean activePerk, boolean showInMenu, String perkName) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        menuIcon = icon.clone();
        UGUtils.nameItem(menuIcon, ChatColor.AQUA + name);
        UGUtils.setLore(menuIcon, ChatColor.GOLD + "Cost: " + cost);
        this.cost = cost;
        this.activePerk = activePerk;
        this.showInMenu = showInMenu;
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
    private KillcoinPerk(String name, String description, ItemStack icon, int cost, boolean activePerk, boolean showInMenu) {
        this(name, description, icon, cost, activePerk, showInMenu, null);
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
     * Gets the perk's menu icon.
     *
     * @return The perk's icon in the menu.
     */
    public ItemStack getMenuIcon() {
        return menuIcon;
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
     * Checks if the perk should be shown in the perk menu.
     *
     * @return True if the perk should be shown in the menu, else false.
     */
    public boolean showInMenu() {
        return showInMenu;
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
     * Sets the perk's activation sound.
     *
     * @param activateSound The activation sound.
     */
    public void setActivateSound(SoundEffect activateSound) {
        this.activateSound = activateSound;
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
     * Checks if the perk can be activated. Used in cases where the perk may be global and already activated (i.e. weather).
     *
     * @param ultimateGames The UltimateGames instance.
     * @param teamDeathmatch    The Deathmatch instance.
     * @param arena         The arena.
     * @param player        The player.
     * @return True if the perk can be activated, else false.
     */
    public boolean canActivate(UltimateGames ultimateGames, TeamDeathmatch teamDeathmatch, Arena arena, Player player) {
        return true;
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
        if (activateSound != null) {
            activateSound.play(player.getLocation(), player);
        }
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
        damage.apply(DAMAGE_POTION.getMenuIcon());
        DAMAGE_POTION.setActivateSound(null);

        Potion poison = new Potion(PotionType.POISON, 1);
        poison.setSplash(true);
        poison.apply(POISON_POTION.getIcon());
        poison.apply(POISON_POTION.getMenuIcon());
        POISON_POTION.setActivateSound(null);

        new Potion(PotionType.INSTANT_HEAL, 2).apply(HEALTH_POTION.getIcon());
        HEALTH_POTION.setActivateSound(new SoundEffect(Sound.DRINK, 1, 1));

        new Potion(PotionType.STRENGTH, 1).apply(STRENGTH.getIcon());
        STRENGTH.setActivateSound(new SoundEffect(Sound.DRINK, 1, 1));

        SPEED.setActivateSound(new SoundEffect(Sound.DRINK, 1, 1));

        FIRE_RESISTANCE.setActivateSound(new SoundEffect(Sound.DRINK, 1, 1));

        ENDER_PEARL.setActivateSound(new SoundEffect(Sound.ENDERMAN_TELEPORT, 1, 1));

        ARROWS.setActivateSound(new SoundEffect(Sound.SHOOT_ARROW, 1, 1));

        LONGBOW.getIcon().addEnchantment(Enchantment.ARROW_DAMAGE, 1);

        FIREBOW.getIcon().addEnchantment(Enchantment.ARROW_FIRE, 1);
        FIREBOW.setActivateSound(new SoundEffect(Sound.FIRE_IGNITE, 1, 1));

        BROADSWORD.getIcon().addEnchantment(Enchantment.DAMAGE_ALL, 2);

        FIRESWORD.getIcon().addEnchantment(Enchantment.FIRE_ASPECT, 1);
        FIRESWORD.setActivateSound(new SoundEffect(Sound.FIRE_IGNITE, 1, 1));

        WOLF_TAMER.setActivateSound(new SoundEffect(Sound.WOLF_BARK, 1, 1));
    }
}
