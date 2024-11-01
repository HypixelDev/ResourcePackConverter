package net.hypixel.resourcepack.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.MinecraftVersion;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;
import net.hypixel.resourcepack.pack.Pack;

public class ArmorModelConverter extends Converter {

    private static final Map<String, String> ARMOR_TEXTURES = Util.createMap(HashMap::new, map -> {
            map.put("chainmail_layer_1", "chainmail");
            map.put("diamond_layer_1", "diamond");
            map.put("gold_layer_1", "gold");
            map.put("iron_layer_1", "iron");
            map.put("leather_layer_1", "leather");
            map.put("leather_layer_1_overlay", "leather_overlay");
    });

    private static final Map<String, String> LEGGING_TEXTURES = Util.createMap(HashMap::new, map -> {
            map.put("chainmail_layer_2", "chainmail");
            map.put("diamond_layer_2", "diamond");
            map.put("gold_layer_2", "gold");
            map.put("iron_layer_2", "iron");
            map.put("leather_layer_2", "leather");
            map.put("leather_layer_2_overlay", "leather_overlay");
    });

    public ArmorModelConverter(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public MinecraftVersion getVersion() {
        return MinecraftVersion.v1_21_2;
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path legacyArmorModelDirectory = pack.getWorkingPath().resolve("assets/minecraft/textures/models/armor/");
        if (Files.notExists(legacyArmorModelDirectory)) {
            return;
        }

        Path texturesEquipmentDirectory = pack.getWorkingPath().resolve("assets/minecraft/textures/entity/equipment/");
        Path texturesHumanoidDirectory = texturesEquipmentDirectory.resolve("humanoid/");
        Path texturesHumanoidLeggingsDirectory = texturesEquipmentDirectory.resolve("humanoid_leggings/");

        this.convertTextures(pack, legacyArmorModelDirectory, ARMOR_TEXTURES, texturesHumanoidDirectory);
        this.convertTextures(pack, legacyArmorModelDirectory, LEGGING_TEXTURES, texturesHumanoidLeggingsDirectory);
    }

    private void convertTextures(Pack pack, Path legacyArmorModelDirectory, Map<String, String> textureMapping, Path targetDirectory) throws IOException {
        if (Files.notExists(legacyArmorModelDirectory)) {
            return;
        }

        boolean createdDirectory = false;

        for (Map.Entry<String, String> textureEntry : textureMapping.entrySet()) {
            String legacyTextureName = textureEntry.getKey() + ".png";
            Path legacyTexturePath = legacyArmorModelDirectory.resolve(legacyTextureName);
            if (Files.notExists(legacyTexturePath)) {
                continue;
            }

            if (!createdDirectory) {
                createdDirectory = true;
                Files.createDirectories(targetDirectory);
                System.out.println("      Created directory " + pack.getWorkingPath().relativize(targetDirectory));
            }

            String modernTextureName = textureEntry.getValue() + ".png";
            Path modernTexturePath = targetDirectory.resolve(modernTextureName);

            Path relativeLegacyDirectory = pack.getWorkingPath().relativize(legacyTexturePath);
            Path relativeModernDirectory = pack.getWorkingPath().relativize(modernTexturePath);
            if (Files.exists(modernTexturePath)) {
                System.err.println("      Would have copied " + relativeLegacyDirectory + " to " + relativeModernDirectory + " but a file already exists!");
                continue;
            }

            Files.copy(legacyTexturePath, modernTexturePath);
            System.out.println("      Copied " + relativeLegacyDirectory + " to " + relativeModernDirectory);
        }

        if (Files.list(legacyArmorModelDirectory).count() == 0) {
            Files.deleteIfExists(legacyArmorModelDirectory);
            System.out.println("      Deleting now empty directory " + pack.getWorkingPath().relativize(legacyArmorModelDirectory));
        }
    }

}
