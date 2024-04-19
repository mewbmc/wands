package net.willowmc.wands.libs.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;

public class MinecraftUtils {


    public static BlockDisplay spawnDisplayBlock(Location location, Material material) {
        return location.getWorld().spawn(location, BlockDisplay.class, display -> display.setBlock(material.createBlockData()));
    }


}
