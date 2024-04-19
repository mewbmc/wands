package net.willowmc.wands.libs.utils;


import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) {
            return this;
        }
        this.total += weight;
        this.map.put(this.total, result);
        return this;
    }

    public boolean contains(E result) {
        return this.map.containsValue(result);
    }

    public E next() {
        final double value = this.random.nextDouble() * this.total;
        return this.map.higherEntry(value).getValue();
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }
}
