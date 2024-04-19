package net.willowmc.wands.libs.utils;

import it.unimi.dsi.fastutil.Pair;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class MathUtils {

    public static Location getAdjustedLocation(Location loc1, Location loc2, int maxDistance) {
        final Vector vector1 = loc1.toVector();
        final Vector vector2 = loc2.toVector();
        final Vector normalizedVector;

        if (vector2.distance(vector1) < maxDistance) {
            normalizedVector = vector2.clone().subtract(vector1).normalize();
            while (vector2.distance(vector1) < maxDistance) {
                vector2.subtract(normalizedVector);
            }

        } else {
            normalizedVector = vector1.clone().subtract(vector2).normalize();
            while (vector2.distance(vector1) < maxDistance) {
                vector2.add(normalizedVector);
            }
        }

        return new Location(loc1.getWorld(), vector2.getX(), vector2.getY(), vector2.getZ());
    }

    public static <A> Map<A, Pair<Integer, Integer>> compare(Map<? extends A, Integer> request, Map<? extends A, Integer> fact) {
        final Map<A, Pair<Integer, Integer>> result = new HashMap<>();
        for (final A key : request.keySet()) {
            final int requestValue = request.get(key);
            final int actualValue = Math.min(fact.getOrDefault(key, 0), requestValue);
            result.put(key, Pair.of(requestValue, actualValue));
        }

        for (final A key : request.keySet()) {
            if (result.containsKey(key)) {
                continue;
            }

            result.put(key, Pair.of(request.get(key), 0));
        }
        return result;
    }

    public static float getAngleBetweenVectors(Coordinate coordinate1, Coordinate coordinate2) {
        final double dotProduct = coordinate1.x() * coordinate2.x() + coordinate1.y() * coordinate2.y() + coordinate1.z() * coordinate2.z();
        final double magnitude1 = Math.sqrt(Math.pow(coordinate1.x(), 2) + Math.pow(coordinate1.y(), 2) + Math.pow(coordinate1.z(), 2));
        final double magnitude2 = Math.sqrt(Math.pow(coordinate2.x(), 2) + Math.pow(coordinate2.y(), 2) + Math.pow(coordinate2.z(), 2));
        return (float) Math.toDegrees(Math.acos(dotProduct / (magnitude1 * magnitude2)));
    }

    public static Coordinate getMin(Collection<Coordinate> locations) {
        int x = Integer.MAX_VALUE;
        int y = Integer.MAX_VALUE;
        int z = Integer.MAX_VALUE;

        for (final Coordinate location : locations) {
            if (location.x() < x) {
                x = location.x();
            }
            if (location.y() < y) {
                y = location.y();
            }
            if (location.z() < z) {
                z = location.z();
            }
        }

        return Coordinate.of(x, y, z);
    }

    public static List<Location> getEquidistantLocations(Location centralLocation, int n, double minRadius, double maxRadius) {
        return getEquidistantLocations(centralLocation, n, minRadius, maxRadius, minRadius, maxRadius);
    }

    public static List<Location> getEquidistantLocations(Location centralLocation, int n, double minRadius, double maxRadius, double minHeight, double maxHeight) {
        final List<Location> locations = new ArrayList<>();
        final double angleIncrement = 2 * Math.PI / n;

        for (int i = 0; i < n; i++) {
            final double angle = i * angleIncrement;
            final double radius = minRadius + Math.random() * (maxRadius - minRadius);
            final double height = minHeight + Math.random() * (maxHeight - minHeight);
            final double x = centralLocation.getX() + radius * Math.cos(angle);
            final double y = centralLocation.getY() + height;
            final double z = centralLocation.getZ() + radius * Math.sin(angle);
            locations.add(new Location(centralLocation.getWorld(), x, y, z));
        }

        return locations;
    }

    public static int summarizeAxisLength(Point point, boolean isXAxis, List<Point> points, int depth) {
        int length = 1;

        if (isXAxis) {
            for (int i = 1; i != -depth; i--) {
                if (points.contains(new Point(point.x + i, point.y))) {
                    length++;
                } else {
                    break;
                }
            }

            for (int i = 1; i != depth; i++) {
                if (points.contains(new Point(point.x + i, point.y))) {
                    length++;
                } else {
                    break;
                }
            }

        } else {
            for (int i = 1; i != -depth; i--) {
                if (points.contains(new Point(point.x, point.y + i))) {
                    length++;
                } else {
                    break;
                }
            }

            for (int i = 1; i != -depth; i++) {
                if (points.contains(new Point(point.x, point.y + i))) {
                    length++;
                } else {
                    break;
                }
            }
        }

        return length;
    }

    public static Point getMiddleOfPoints(List<Point> pointsToFindMiddleOf, List<Point> dungeonLayout) {
        final List<Point> farthestPoints = getFarthestPoints(pointsToFindMiddleOf.size(), pointsToFindMiddleOf);

        final Point middlePoint = new Point(0, 0);
        int count = 0;

        for (final Point point : farthestPoints) {
            if (dungeonLayout.contains(point)) {
                middlePoint.x += point.x;
                middlePoint.y += point.y;
                count++;
            }
        }

        if (count == 0 || pointsToFindMiddleOf.stream().anyMatch(point -> getShortestPath(point, middlePoint, dungeonLayout) <= 3) || dungeonLayout.contains(
            middlePoint)) {

            while (true) {
                final Point randomPoint = dungeonLayout.get(ThreadLocalRandom.current().nextInt(dungeonLayout.size()));
                if (pointsToFindMiddleOf.stream().allMatch(point -> getShortestPath(point, randomPoint, dungeonLayout) > 3) && !pointsToFindMiddleOf.contains(
                    randomPoint)) {
                    return randomPoint;
                }
            }
        }

        middlePoint.x /= count;
        middlePoint.y /= count;

        return middlePoint;
    }

    public static List<Point> getFarthestPoints(int n, List<? extends Point> points) {
        final List<Point> farthestPoints = new ArrayList<>();
        n += 1;

        final int numPoints = points.size();
        final double[][] distances = new double[numPoints][numPoints];
        for (int i = 0; i < numPoints; i++) {
            final Point p1 = points.get(i);
            for (int j = i + 1; j < numPoints; j++) {
                final Point p2 = points.get(j);
                final double distance = p1.distance(p2);
                distances[i][j] = distance;
                distances[j][i] = distance;
            }
        }

        while (farthestPoints.size() < n) {
            Point farthestPoint = null;
            double maxMinDistance = Double.MIN_VALUE;
            for (final Point point : points) {
                if (farthestPoints.contains(point)) {
                    continue;
                }

                double minDistance = Double.MAX_VALUE;
                for (final Point fp : farthestPoints) {
                    final double distance = distances[points.indexOf(point)][points.indexOf(fp)];
                    if (distance < minDistance) {
                        minDistance = distance;
                    }

                }
                if (minDistance > maxMinDistance) {
                    maxMinDistance = minDistance;
                    farthestPoint = point;
                }
            }

            farthestPoints.add(farthestPoint);
        }

        farthestPoints.remove(new Point(0, 0));

        return farthestPoints;
    }

    public static int getShortestPath(Point start, Point end, List<Point> points) {
        final Map<Point, Integer> distance = new HashMap<>();
        final PriorityQueue<Point> queue = new PriorityQueue<>(Comparator.comparingInt(p -> distance.getOrDefault(p, Integer.MAX_VALUE)));
        distance.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            final Point current = queue.poll();
            if (current.equals(end)) {
                return distance.get(end);
            }
            for (final Point neighbor : getNeighbors(current, points)) {
                final int tentativeDistance = distance.get(current) + 1;
                if (tentativeDistance < distance.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distance.put(neighbor, tentativeDistance);
                    queue.add(neighbor);
                }
            }
        }
        return -1;
    }

    private static List<Point> getNeighbors(Point p, List<Point> points) {
        final List<Point> neighbors = new ArrayList<>();
        for (final Point point : points) {
            if (p.distance(point) <= 1) {
                neighbors.add(point);
            }
        }
        return neighbors;
    }

    public static Location getLocationBetween(Location from, Location to, double height) {
        final double x = from.getX() + (to.getX() - from.getX()) / 2;
        final double y = from.getY() + (to.getY() - from.getY()) / 2 + height;
        final double z = from.getZ() + (to.getZ() - from.getZ()) / 2;

        return new Location(from.getWorld(), x, y, z);
    }

    public static int getYawForTwoPoints(Location from, Location to) {
        final double x = to.getX() - from.getX();
        final double z = to.getZ() - from.getZ();

        final double yaw = Math.toDegrees(Math.atan2(z, x)) - 90;

        return (int) yaw;
    }

    public static int getYawForTwoPoints(Coordinate from, Coordinate to) {
        final double x = to.x() - from.x();
        final double z = to.z() - from.z();

        final double yaw = Math.toDegrees(Math.atan2(z, x)) - 90;

        return (int) yaw;
    }


    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean ascending) {
        final List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        if (ascending) {
            list.sort(Map.Entry.comparingByValue());
        } else {
            list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        }

        final Map<K, V> result = new LinkedHashMap<>();
        for (final Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static Location getNearbyLocation(Location target, Location sideLoc, double radius, int height) {
        final Vector direction = target.toVector().subtract(sideLoc.toVector());
        direction.normalize();

        final Vector offset = direction.multiply(radius).setY(height);

        return sideLoc.clone().add(offset);
    }

    public static boolean getRandom(double chance) {
        return Math.random() < chance;
    }

    public static boolean getRandom(int chance) {
        return Math.random() < chance / 100.0;
    }

    public static int getPercentFromInteger(double toFind, double from) {
        final double percent = toFind / from;
        return (int) Math.round(percent * 100);
    }

    public static Location getRandomLocation(World world, int minX, int maxX, int minZ, int maxZ) {
        final double x = MathUtils.getRandomInteger(minX, maxX);
        final double y = MathUtils.getRandomInteger(200, 250);
        final double z = MathUtils.getRandomInteger(minZ, maxZ);

        return new Location(world, x, y, z);
    }

    public static int getRandomInteger(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static boolean isInRadius(int radius, Location location, Location playerLocation) {
        return location.distance(playerLocation) <= radius;
    }

    public static int percent(int fact, int required) {
        return (int) Math.round((double) fact / required * 100);
    }

}