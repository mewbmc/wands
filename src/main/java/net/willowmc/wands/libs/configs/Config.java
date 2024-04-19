package net.willowmc.wands.libs.configs;

import net.willowmc.wands.libs.configs.annotations.Ignore;
import net.willowmc.wands.libs.configs.formats.Format;
import net.willowmc.wands.libs.configs.formats.JSON;
import net.willowmc.wands.libs.configs.formats.YAML;
import net.willowmc.wands.libs.configs.serialization.Serializer;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;

@Getter
@NoArgsConstructor
public abstract class Config {

    @Ignore
    private File file;
    @Ignore
    private Format format;
    @Ignore
    private LinkedHashMap<String, Object> map;

    public Config(String fileName, String path) {
        this.file = new File(path, fileName);
        this.format = fileName.endsWith(".yaml") || fileName.endsWith(".yml") ? new YAML() : new JSON();
        this.map = new LinkedHashMap<>(this.format.readFile(this.file));

        this.reload();
    }

    public void reload() {
        if (!this.file.exists()) {
            this.createFile();
        }

        this.fillMissingFields();
        final Config newConfig = (Config) Serializer.OBJECT.deserialize(this.getClass(), this.map);
        for (final Field field : this.getFields()) {
            try {
                field.set(this, field.get(newConfig));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void createFile() {
        if (!this.file.getParentFile().exists()) {
            this.file.getParentFile().mkdirs();
        }

        try {
            this.file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillMissingFields() {
        this.map = new LinkedHashMap<>(this.format.readFile(this.file));
        final Map<String, Object> serialized = Serializer.OBJECT.serialize(this.getTemplate());
        serialized.forEach((key, value) -> this.map.putIfAbsent(key, value));
        this.format.writeFile(this.file, this.map);
    }

    private Config getTemplate() {
        try {
            return this.getClass().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            System.err.println("Cannot create an instance of " + this.getClass().getName() + " class, please check if it has a default no args constructor");
            throw new RuntimeException(e);
        }
    }

    private List<Field> getFields() {
        return Arrays.stream(FieldUtils.getAllFields(this.getClass()))
            .filter(field -> !field.isAnnotationPresent(Ignore.class))
            .peek(field -> field.setAccessible(true))
            .collect(Collectors.toList());
    }

}
