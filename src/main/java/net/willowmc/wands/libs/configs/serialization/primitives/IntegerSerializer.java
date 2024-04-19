package net.willowmc.wands.libs.configs.serialization.primitives;

import net.willowmc.wands.libs.configs.serialization.Serializer;

public class IntegerSerializer implements Serializer.Specific<Integer, Integer> {

    @Override
    public Integer deserialize(Class fieldClass, Integer serialized) {
        return serialized;
    }

    @Override
    public Integer serialize(Integer object) {
        return object;
    }

    @Override
    public boolean isCompatibleWith(Class clazz) {
        return clazz.equals(Integer.class) || clazz.equals(int.class);
    }

}
