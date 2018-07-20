package net.hypixel.resourcepack.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.Pack;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class NameConverter extends Converter {

    protected final Mapping blockMapping = new BlockMapping();
    protected final Mapping itemMapping = new ItemMapping();

    @Override
    public void convert(PackConverter main, Pack pack) throws IOException {
        Path models = pack.getPath().resolve("assets\\minecraft\\models");
        if (models.resolve("blocks").toFile().exists()) Files.move(models.resolve("blocks"), models.resolve("block"));
        renameAll(blockMapping, ".json", models.resolve("block"));
        if (models.resolve("items").toFile().exists()) Files.move(models.resolve("items"), models.resolve("item"));
        renameAll(itemMapping, ".json", models.resolve("item"));

        Path blockStates = pack.getPath().resolve("assets\\minecraft\\blockstates");
        renameAll(itemMapping, ".json", blockStates);

        Path textures = pack.getPath().resolve("assets\\minecraft\\textures");
        if (textures.resolve("blocks").toFile().exists()) Files.move(textures.resolve("blocks"), textures.resolve("block"));
        renameAll(blockMapping, ".png", textures.resolve("block"));
        renameAll(blockMapping, ".png.mcmeta", textures.resolve("block"));
        if (textures.resolve("items").toFile().exists()) Files.move(textures.resolve("items"), textures.resolve("item"));
        renameAll(itemMapping, ".png", textures.resolve("item"));
        renameAll(itemMapping, ".png.mcmeta", textures.resolve("item"));
    }

    protected void renameAll(Mapping mapping, String extension, Path path) throws IOException {
        if (path.toFile().exists()) {
            Files.list(path).forEach(path1 -> {
                if (!path1.toString().endsWith(extension)) return;

                String baseName = path1.getFileName().toString().substring(0, path1.getFileName().toString().length() - extension.length());
                String newName = mapping.remap(baseName);
                if (newName != null && !newName.equals(baseName)) {
                    Boolean ret = Util.renameFile(path1, newName + extension);
                    if (ret == null) return;
                    if (ret && PackConverter.DEBUG) {
                        System.out.println("      Renamed: " + path1.getFileName().toString() + "->" + newName + extension);
                    } else if (!ret) {
                        System.err.println("      Failed to rename: " + path1.getFileName().toString() + "->" + newName + extension);
                    }
                }
            });
        }
    }

    public Mapping getBlockMapping() {
        return blockMapping;
    }

    public Mapping getItemMapping() {
        return itemMapping;
    }

    protected abstract static class Mapping {

        protected final Map<String, String> mapping = new HashMap<>();

        public Mapping() {
            load();
        }

        protected abstract void load();

        /**
         * @return remapped or in if not present
         */
        public String remap(String in) {
            return mapping.getOrDefault(in, in);
        }

    }

    protected static class BlockMapping extends Mapping {

        @Override
        protected void load() {
            JsonObject blocks = Util.readJsonResource("/blocks.json");
            if (blocks != null) {
                for (Map.Entry<String, JsonElement> entry : blocks.entrySet()) {
                    this.mapping.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
        }

    }

    protected static class ItemMapping extends Mapping {

        @Override
        protected void load() {
            JsonObject items = Util.readJsonResource("/items.json");
            if (items != null) {
                for (Map.Entry<String, JsonElement> entry : items.entrySet()) {
                    this.mapping.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
        }

    }
}
