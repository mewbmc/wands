package net.willowmc.wands.libs.configs.serialization.other;

import net.willowmc.wands.libs.configs.serialization.Serializer.Specific;
import java.util.Arrays;

public class EnumSerializer implements Specific<Enum, String> {

    @Override
    public Enum deserialize(Class<?> fieldClass, String serialized) {
        try {
            serialized = serialized.toUpperCase().replace("-", "_");

            return Enum.valueOf((Class<Enum>) fieldClass, serialized);
        } catch (IllegalArgumentException ignore) {
            final String[] existingValues = Arrays.stream(fieldClass.getEnumConstants())
                .map(en -> ((Enum) en).name())
                .toList()
                .toArray(new String[0]);

            throw new IllegalArgumentException(
                "Failed to deserialize Enum value: " + serialized + ", as it does not exist, "
                + "available values: " + String.join(", ", existingValues));
        }
    }

    @Override
    public String serialize(Enum object) {
        return object.name();
    }

    @Override
    public boolean isCompatibleWith(Class clazz) {
        return clazz.isEnum();
    }
}
