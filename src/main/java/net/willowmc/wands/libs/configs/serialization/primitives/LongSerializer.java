package net.willowmc.wands.libs.configs.serialization.primitives;

import net.willowmc.wands.libs.configs.serialization.Serializer;

public class LongSerializer implements Serializer.Specific<Long, Long> {

    @Override
    public Long deserialize(Class fieldClass, Long serialized) {
        return serialized;
    }

    @Override
    public Long serialize(Long object) {
        return object;
    }

    @Override
    public boolean isCompatibleWith(Class clazz) {
        return clazz.equals(Long.class) || clazz.equals(long.class);
    }
}