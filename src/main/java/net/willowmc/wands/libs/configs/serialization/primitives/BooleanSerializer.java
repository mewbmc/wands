package net.willowmc.wands.libs.configs.serialization.primitives;


import net.willowmc.wands.libs.configs.serialization.Serializer;

public class BooleanSerializer implements Serializer.Specific<Boolean, Boolean> {

    @Override
    public Boolean deserialize(Class fieldClass, Boolean serialized) {
        return serialized;
    }

    @Override
    public Boolean serialize(Boolean object) {
        return object;
    }

    @Override
    public boolean isCompatibleWith(Class clazz) {
        return clazz.equals(Boolean.class) || clazz.equals(boolean.class);
    }
}

