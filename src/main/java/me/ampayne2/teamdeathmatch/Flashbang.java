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
import me.ampayne2.ultimategames.api.games.items.ThrowableGameItem;
import ninja.amp.ampeffects.effects.particles.ParticleEffect;
import ninja.amp.ampeffects.effects.sounds.SoundEffect;
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
    private static final SoundEffect THROW_SOUND = new SoundEffect(Sound.IRONGOLEM_THROW, 1, 1);
    private static final SoundEffect EXPLODE_SOUND = new SoundEffect(Sound.EXPLODE, 1, 1);

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
