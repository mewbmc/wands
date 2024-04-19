package net.willowmc.wands.libs.utils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@With
@NoArgsConstructor
@AllArgsConstructor
public class Sound {

    private org.bukkit.Sound sound;
    private double volume;
    private double pitch;

    public static Sound of(org.bukkit.Sound sound, float volume, float pitch) {
        return new Sound(sound, volume, pitch);
    }

    public void play(Player player) {
        player.getWorld().playSound(player.getLocation(), this.sound, (float) this.volume, (float) this.pitch);
    }

    public void play(Location location) {
        location.getWorld().playSound(location, this.sound, (float) this.volume, (float) this.pitch);
    }

}
