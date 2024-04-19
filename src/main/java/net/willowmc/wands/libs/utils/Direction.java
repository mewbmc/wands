package net.willowmc.wands.libs.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public enum Direction {

    NORTH(0),
    WEST(90),
    SOUTH(180),
    EAST(270);

    private final int degrees;

    public static Direction of(float yaw) {
        if (yaw >= -45 && yaw <= 45) {
            return SOUTH;
        } else if (yaw >= 45 && yaw <= 135) {
            return WEST;
        } else if (yaw >= 135 || yaw <= -135) {
            return NORTH;
        } else {
            return EAST;
        }
    }

    public static Direction of(Player player) {
        return of(player.getEyeLocation().getYaw());
    }

}
