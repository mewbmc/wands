package net.willowmc.wands.libs.configs.serialization.primitives;


import net.willowmc.wands.libs.configs.serialization.Serializer;

public class StringSerializer implements Serializer.Specific<String, String> {

    @Override
    public String deserialize(Class<?> fieldClass, String serialized) {
        return serialized;
    }

    @Override
    public String serialize(String object) {
        return object;
    }
}
