package net.willowmc.wands.libs.utils;

import io.lumine.mythic.api.adapters.AbstractLocation;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

@Getter
@Setter
@With
@Accessors(fluent = true)
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Coordinate implements Serializable {

    private int x;
    private int y;
    private int z;
    public static final Coordinate ZERO = Coordinate.of(0, 0, 0);

    public static Coordinate of(Location location) {
        return Coordinate.of(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static Coordinate of(String string) {
        string = string.replaceAll("\\s+", "");
        final String[] split = string.split(",");
        return Coordinate.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    public static Coordinate of(Vector vector) {
        return Coordinate.of(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    @Override
    public String toString() {
        return this.x + "," + this.y + "," + this.z;
    }


    public boolean equals(Coordinate coordinate) {
        return coordinate.x() == this.x() && coordinate.y() == this.y() && coordinate.z() == this.z();
    }

    public Location toLocation(String worldName) {
        return this.toLocation(Bukkit.getWorld(worldName));
    }

    public Location toLocation(World world) {
        return new Location(world, this.x, this.y, this.z);
    }

    public Vector toWorldEditVector() {
        return new Vector(this.x, this.y, this.z);
    }

    public Coordinate add(Coordinate coordinate) {
        return Coordinate.of(this.x + coordinate.x(), this.y + coordinate.y(), this.z + coordinate.z());
    }

    public Coordinate subtract(Coordinate coordinate) {
        return Coordinate.of(this.x - coordinate.x(), this.y - coordinate.y(), this.z - coordinate.z());
    }

    public Coordinate multiply(Coordinate coordinate) {
        return Coordinate.of(this.x * coordinate.x(), this.y * coordinate.y(), this.z * coordinate.z());
    }

    public Coordinate divide(Coordinate coordinate) {
        return Coordinate.of(this.x / coordinate.x(), this.y / coordinate.y(), this.z / coordinate.z());
    }

    public Coordinate divide(int i) {
        return Coordinate.of(this.x / i, this.y / i, this.z / i);
    }


    public Vector toVector() {
        return new Vector(this.x, this.y, this.z);
    }

    public int distance(Coordinate coordinate) {
        return (int) Math.sqrt(Math.pow(this.x - coordinate.x(), 2) + Math.pow(this.y - coordinate.y(), 2) + Math.pow(this.z - coordinate.z(), 2));
    }

    public Coordinate abs() {
        return Coordinate.of(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
    }

    public BlockPos toBlockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public Coordinate middle() {
        return Coordinate.of(this.x / 2, this.y / 2, this.z / 2);
    }

    public Coordinate invert() {
        return Coordinate.of(-this.x, -this.y, -this.z);
    }

    public int length(Coordinate coordinate) {
        return (int) Math.sqrt(Math.pow(coordinate.x() - this.x, 2) + Math.pow(coordinate.y() - this.y, 2) + Math.pow(coordinate.z() - this.z, 2));
    }

    public Coordinate rotate(int degrees) {
        return switch (degrees) {
            case 90 -> Coordinate.of(-this.z, this.y, this.x);
            case 180 -> Coordinate.of(this.x, this.y, this.z);
            case 270 -> Coordinate.of(this.z, this.y, -this.x);
            default -> Coordinate.of(-this.x, this.y, -this.z);
        };
    }

    public Coordinate rotate(Direction direction) {
        return this.rotate(direction.getDegrees());
    }

    public Coordinate multiply(int i) {
        return Coordinate.of(this.x * i, this.y * i, this.z * i);
    }

    public AbstractLocation toMythicLocation() {
        return new AbstractLocation(this.x, this.y, this.z);
    }
}