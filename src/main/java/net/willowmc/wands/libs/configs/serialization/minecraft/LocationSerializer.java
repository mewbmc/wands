package net.willowmc.wands.libs.configs.serialization.minecraft;

import net.willowmc.wands.libs.configs.serialization.Serializer.Specific;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationSerializer implements Specific<Location, Map<String, Object>> {

    @Override
    public Location deserialize(Class<?> fieldClass, Map<String, Object> map) {
        final Location location = new Location(null, 0, 0, 0);

        location.setWorld(Bukkit.getWorld((String) map.get("world")));
        location.setX(((Number) map.get("x")).floatValue());
        location.setY(((Number) map.get("y")).floatValue());
        location.setZ(((Number) map.get("z")).floatValue());

        if (map.containsKey("yaw")) {
            location.setYaw(((Number) map.get("yaw")).floatValue());
        }
        if (map.containsKey("pitch")) {
            location.setPitch(((Number) map.get("pitch")).floatValue());
        }

        return location;
    }

    @Override
    public Map<String, Object> serialize(Location location) {
        final Map<String, Object> map = new HashMap<>();

        map.put("world", location.getWorld().getName());
        map.put("x", location.getX());
        map.put("y", location.getY());
        map.put("z", location.getZ());

        if (location.getYaw() != 0) {
            map.put("yaw", location.getYaw());
        }
        if (location.getPitch() != 0) {
            map.put("pitch", location.getPitch());
        }

        return map;
    }

}
