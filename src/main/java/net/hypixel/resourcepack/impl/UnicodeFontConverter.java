package net.hypixel.resourcepack.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.MinecraftVersion;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;
import net.hypixel.resourcepack.pack.Pack;

public class UnicodeFontConverter extends Converter {

    private static final String LEGACY_UNICODE_TEXTURE_FILE_NAME_PREFIX = "unicode_page_";

    public UnicodeFontConverter(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public MinecraftVersion getVersion() {
        return MinecraftVersion.v1_20;
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path fontTextureDir = pack.getWorkingPath().resolve("assets/minecraft/textures/font/");
        if (Files.notExists(fontTextureDir)) {
            return;
        }

        String[] legacyUnicodeTextureFileNames = Files.list(fontTextureDir)
            .map(path -> path.getFileName().toString())
            .filter(fileName -> fileName.startsWith(LEGACY_UNICODE_TEXTURE_FILE_NAME_PREFIX))
            .toArray(String[]::new);
        if (legacyUnicodeTextureFileNames.length == 0) {
            return;
        }

        Path fontDefinitionDir = pack.getWorkingPath().resolve("assets/minecraft/font/");
        Files.createDirectories(fontDefinitionDir); // Silently fails if already exists

        Path defaultFontDefinition = fontDefinitionDir.resolve("default.json");

        JsonObject defaultFontDefinitionJson;
        if (Files.exists(defaultFontDefinition)) {
            System.out.println("      Default font definition already exists... appending to it instead");
            defaultFontDefinitionJson = Util.readJson(packConverter.getGson(), defaultFontDefinition);
        } else {
            System.out.println("      Creating default font definition.");
            Files.createFile(defaultFontDefinition);
            defaultFontDefinitionJson = createDefaultFontDefinitionJson();
        }

        JsonObject[] legacyFontDefinitions = convertToReferenceFontProviders(legacyUnicodeTextureFileNames);
        this.appendProviders(defaultFontDefinitionJson, legacyFontDefinitions);

        Util.writeJson(packConverter.getGson(), defaultFontDefinition, defaultFontDefinitionJson);

        // Create reference providers
        Path fontDefinitionIncludeDir = fontDefinitionDir.resolve("include");
        Files.createDirectories(fontDefinitionIncludeDir); // Silently fails if already exists

        for (String textureFileName : legacyUnicodeTextureFileNames) {
            String jsonFileName = textureFileName.replace(".png", ".json");
            System.out.println("      Creating include font definition " + jsonFileName);
            JsonObject fontDefinition = createFontDefinitionJson(createLegacyUnicodeBitmapProvider(textureFileName));

            Path providerPath = fontDefinitionIncludeDir.resolve(jsonFileName);
            Files.createFile(providerPath);
            Util.writeJson(packConverter.getGson(), providerPath, fontDefinition);
        }
    }

    private JsonObject[] convertToReferenceFontProviders(String[] legacyUnicodeTextureFileNames) {
        JsonObject[] fontProviders = new JsonObject[legacyUnicodeTextureFileNames.length];

        for (int i = 0; i < legacyUnicodeTextureFileNames.length; i++) {
            String textureFileName = legacyUnicodeTextureFileNames[i];
            fontProviders[i] = createReferenceProvider("minecraft:include/" + textureFileName.substring(0, textureFileName.indexOf('.')));
        }

        return fontProviders;
    }

    private void appendProviders(JsonObject fontDefinitionRoot, JsonObject... providersToAdd) {
        JsonArray providers = null;
        if (fontDefinitionRoot.has("providers")) {
            providers = fontDefinitionRoot.getAsJsonArray("providers");
        } else {
            fontDefinitionRoot.add("providers", providers = new JsonArray());
        }

        if (providers == null) {
            throw new IllegalStateException("Could not fetch \"providers\" array from font definition \"" + fontDefinitionRoot + "\"");
        }

        for (JsonObject provider : providersToAdd) {
            providers.add(provider);
        }
    }

    private JsonObject createFontDefinitionJson(JsonObject... providersToAdd) {
        JsonObject root = new JsonObject();

        JsonArray providers = new JsonArray();
        for (JsonObject provider : providersToAdd) {
            providers.add(provider);
        }
        root.add("providers", providers);

        return root;
    }

    private JsonObject createDefaultFontDefinitionJson() {
        return createFontDefinitionJson(
            createReferenceProvider("minecraft:include/space"),
            createReferenceProvider("minecraft:include/default", true)
        );
    }

    private JsonObject createLegacyUnicodeBitmapProvider(String textureFileName) {
        // Essentially going from "unicode_page_xx.png" to "xx", which is the unicode identifier
        String pageString = textureFileName.substring(LEGACY_UNICODE_TEXTURE_FILE_NAME_PREFIX.length(), textureFileName.indexOf('.', LEGACY_UNICODE_TEXTURE_FILE_NAME_PREFIX.length() + 1));
        int startingUnicode = Integer.parseInt(pageString, 16); // Parsing it as a hex number
        startingUnicode <<= 8;

        JsonArray chars = new JsonArray(16);
        for (int i = 0; i < 16; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < 16; j++) {
                int offset = (i * 16) + j;
                line.append(String.format("\\u%04x", startingUnicode + offset));
            }
            chars.add(line.toString());
        }

        JsonObject provider = new JsonObject();
        provider.addProperty("type", "bitmap");
        provider.addProperty("file", "minecraft:font/" + textureFileName);
        provider.addProperty("ascent", 7);
        provider.add("chars", chars);

        return provider;
    }

    private JsonObject createReferenceProvider(String id, boolean filterUniform) {
        JsonObject provider = new JsonObject();
        provider.addProperty("type", "reference");
        provider.addProperty("id", id);

        if (filterUniform) {
            JsonObject filter = new JsonObject();
            filter.addProperty("uniform", false);
            provider.add("filter", filter);
        }

        return provider;
    }

    private JsonObject createReferenceProvider(String id) {
        return createReferenceProvider(id, false);
    }

}
