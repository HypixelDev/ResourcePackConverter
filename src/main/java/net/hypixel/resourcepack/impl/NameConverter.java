package net.hypixel.resourcepack.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;
import net.hypixel.resourcepack.pack.Pack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class NameConverter extends Converter {

    protected final Mapping blockMapping = new BlockMapping();
    protected final Mapping itemMapping = new ItemMapping();

    public NameConverter(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public void convert(Pack pack) throws IOException {
        { // Giving them their own scopes to reuse variable names
            Path modelsPath = pack.getMinecraftPath().resolve("models");
            Path blocksPath = modelsPath.resolve("blocks");
            Path blockPath = modelsPath.resolve("block");
            if (Files.exists(blocksPath)) {
                Files.move(blocksPath, blockPath);
            }
            renameAll(blockMapping, ".json", blockPath);
            Path itemsPath = modelsPath.resolve("items");
            Path itemPath = modelsPath.resolve("item");
            if (Files.exists(itemsPath)) {
                Files.move(itemsPath, itemPath);
            }
            renameAll(itemMapping, ".json", itemPath);

            Path blockStates = pack.getMinecraftPath().resolve("blockstates");
            renameAll(itemMapping, ".json", blockStates);
        }
        {
            Path texturesPath = pack.getMinecraftPath().resolve("textures");
            Path blocksPath = texturesPath.resolve("blocks");
            Path blockPath = texturesPath.resolve("block");
            if (Files.exists(blocksPath)) {
                Files.move(blocksPath, blockPath);
            }
            renameAll(blockMapping, ".png", blockPath);
            renameAll(blockMapping, ".png.mcmeta", blockPath);
            Path itemsPath = texturesPath.resolve("items");
            Path itemPath = texturesPath.resolve("item");
            if (Files.exists(itemsPath)) {
                Files.move(itemsPath, itemPath);
            }
            renameAll(itemMapping, ".png", itemPath);
            renameAll(itemMapping, ".png.mcmeta", itemPath);
        }
    }

    protected void renameAll(Mapping mapping, String extension, Path path) throws IOException {
        if (Files.exists(path)) {
            Files.list(path).forEach(file -> {
                if (!file.toString().endsWith(extension)) {
                    return;
                }
                String fileName = file.getFileName().toString();
                String baseName = fileName.substring(0, fileName.length() - extension.length());
                String newName = mapping.remap(baseName);
                if (newName != null && !newName.equals(baseName)) {
                    try {
                        if (Files.exists(file)) {
                            Files.move(file, file.getParent().resolve(newName + extension));
                            if(PackConverter.DEBUG) {
                                System.out.println("      Renamed: " + fileName + "->" + newName + extension);
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("      Failed to rename: " + fileName + "->" + newName + extension);
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

    protected class BlockMapping extends Mapping {

        @Override
        protected void load() {
            JsonObject blocks = Util.readJsonResource(packConverter.getGson(), "/blocks.json");
            if (blocks != null) {
                for (Map.Entry<String, JsonElement> entry : blocks.entrySet()) {
                    this.mapping.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
        }

    }

    protected class ItemMapping extends Mapping {

        @Override
        protected void load() {
            JsonObject items = Util.readJsonResource(packConverter.getGson(), "/items.json");
            if (items != null) {
                for (Map.Entry<String, JsonElement> entry : items.entrySet()) {
                    this.mapping.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
        }

    }
}
