package net.willowmc.wands.libs.configs.serialization;

import net.kyori.adventure.text.Component;
import net.willowmc.wands.libs.configs.serialization.collections.ArraySerializer;
import net.willowmc.wands.libs.configs.serialization.collections.CollectionSerializer;
import net.willowmc.wands.libs.configs.serialization.collections.MapSerializer;
import net.willowmc.wands.libs.configs.serialization.minecraft.ComponentSerializer;
import net.willowmc.wands.libs.configs.serialization.minecraft.ItemStackSerializer;
import net.willowmc.wands.libs.configs.serialization.minecraft.LocationSerializer;
import net.willowmc.wands.libs.configs.serialization.minecraft.WorldSerializer;
import net.willowmc.wands.libs.configs.serialization.other.EnumSerializer;
import net.willowmc.wands.libs.configs.serialization.other.ObjectSerializer;
import net.willowmc.wands.libs.configs.serialization.primitives.BooleanSerializer;
import net.willowmc.wands.libs.configs.serialization.primitives.DoubleSerializer;
import net.willowmc.wands.libs.configs.serialization.primitives.IntegerSerializer;
import net.willowmc.wands.libs.configs.serialization.primitives.LongSerializer;
import net.willowmc.wands.libs.configs.serialization.primitives.StringSerializer;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

public interface Serializer<T, R> {

    StringSerializer STRING = new StringSerializer();
    IntegerSerializer INTEGER = new IntegerSerializer();
    DoubleSerializer DOUBLE = new DoubleSerializer();
    LongSerializer LONG = new LongSerializer();
    BooleanSerializer BOOLEAN = new BooleanSerializer();
    EnumSerializer ENUM = new EnumSerializer();
    WorldSerializer WORLD = new WorldSerializer();
    ItemStackSerializer ITEMSTACK = new ItemStackSerializer();
    LocationSerializer LOCATION = new LocationSerializer();
    CollectionSerializer COLLECTION = new CollectionSerializer();
    ComponentSerializer COMPONENT = new ComponentSerializer();
    MapSerializer MAP = new MapSerializer();
    ArraySerializer ARRAY = new ArraySerializer();
    ObjectSerializer OBJECT = new ObjectSerializer();


    Set<Serializer> ALL = Set.of(
        STRING, INTEGER, DOUBLE, LONG, BOOLEAN,
        ENUM, WORLD, ITEMSTACK, LOCATION, COMPONENT,
        COLLECTION, MAP, ARRAY, OBJECT
    );

    static Serializer getByClass(Class<?> targetClass) {
        return ALL.stream()
            .filter(type -> type.isCompatibleWith(targetClass))
            .findFirst()
            .orElse(OBJECT);
    }


    /**
     * Serializes the given object. Handles generic types.
     *
     * @param field  The field of the object that needs to be serialized.
     * @param object The object that needs to be serialized.
     * @return The serialized object.
     */
    static Object serialize(Field field, Object object) {
        final Serializer serializer = Serializer.getByClass(field.getType());
        final boolean isGeneric = serializer instanceof Serializer.Generic;

        try {
            if (isGeneric) {
                return ((Generic) serializer).serialize(field, object);
            } else {
                return ((Specific) serializer).serialize(object);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to serialize " + object + " of type " + field.getType(), e);
        }
    }

    /**
     * Deserializes the given object. Handles generic types.
     *
     * @param field  The field of the object that needs to be deserialized.
     * @param object The object that needs to be deserialized.
     * @return The deserialized object.
     */
    static Object deserialize(Field field, Object object) {
        final Serializer serializer = Serializer.getByClass(field.getType());
        final boolean isGeneric = serializer instanceof Serializer.Generic;

        try {
            if (isGeneric) {
                return ((Generic) serializer).deserialize(field, object);
            } else {
                return ((Specific) serializer).deserialize(field.getType(), object);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to deserialize " + object + " of type " + field.getType(), e);
        }

    }

    /**
     * Serializes the given object. IMPORTANT: Doesn't work with generic objects
     *
     * @param clazz  The class of the object that needs to be serialized.
     * @param object The object that needs to be serialized.
     * @return The serialized object.
     */
    static Object serialize(Class<?> clazz, Object object) {
        try {
            return ((Specific) Serializer.getByClass(clazz)).serialize(object);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to serialize " + object + " of type " + clazz, e);
        }
    }

    /**
     * Deserializes the given object. IMPORTANT: Doesn't work with generic objects
     *
     * @param clazz  The class of the object that needs to be deserialized.
     * @param object The object that needs to be deserialized.
     * @return The deserialized object.
     */
    static Object deserialize(Class<?> clazz, Object object) {
        try {
            return ((Specific) Serializer.getByClass(clazz)).deserialize(clazz, object);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to deserialize " + object + " of type " + clazz, e);
        }
    }

    default boolean isCompatibleWith(Class<?> clazz) {
        final ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericInterfaces()[0];
        final Class<?> targetClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        return targetClass.equals(clazz);
    }

    interface Generic<T, R> extends Serializer {
        T deserialize(Field field, R serialized);
        R serialize(Field field, T object);
    }

    interface Specific<T, R> extends Serializer {
        T deserialize(Class<?> fieldClass, R serialized);
        R serialize(T object);
    }

}
