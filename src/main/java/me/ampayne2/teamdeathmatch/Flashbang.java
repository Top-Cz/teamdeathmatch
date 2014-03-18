package me.ampayne2.teamdeathmatch;

import me.ampayne2.ultimategames.api.UltimateGames;
import me.ampayne2.ultimategames.api.arenas.Arena;
import me.ampayne2.ultimategames.api.effects.GameSound;
import me.ampayne2.ultimategames.api.effects.ParticleEffect;
import me.ampayne2.ultimategames.api.games.items.ThrowableGameItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Flashbang item.
 */
public class Flashbang extends ThrowableGameItem {
    private final UltimateGames ultimateGames;
    private static final GameSound THROW_SOUND = new GameSound(Sound.IRONGOLEM_THROW, 1, 1);
    private static final GameSound EXPLODE_SOUND = new GameSound(Sound.EXPLODE, 1, 1);

    public Flashbang(UltimateGames ultimateGames) {
        super(KillcoinPerk.FLASHBANG.getIcon());
        this.ultimateGames = ultimateGames;
    }

    @Override
    public void onItemThrow(Arena arena, final Item item) {
        THROW_SOUND.play(item.getLocation());
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ultimateGames.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if (!item.isDead()) {
                    Location location = item.getLocation();
                    EXPLODE_SOUND.play(location);
                    ParticleEffect.HUGE_EXPLOSION.display(location, 0, 0, 0, 0, 1);
                    for (Entity entity : item.getNearbyEntities(5, 5, 5)) {
                        if (entity instanceof LivingEntity) {
                            ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 2), true);
                        }
                    }
                    item.remove();
                }
            }
        }, 60);
    }
}
