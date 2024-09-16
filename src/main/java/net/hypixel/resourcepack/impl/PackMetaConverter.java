package net.hypixel.resourcepack.impl;

import com.google.gson.JsonObject;
import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.MinecraftVersion;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;
import net.hypixel.resourcepack.pack.Pack;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class PackMetaConverter extends Converter {

    private final MinecraftVersion version;

    public PackMetaConverter(PackConverter packConverter, MinecraftVersion version) {
        super(packConverter);
        this.version = version;
    }

    @Override
    public MinecraftVersion getVersion() {
        return version;
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path file = pack.getWorkingPath().resolve("pack.mcmeta");
        if (!file.toFile().exists()) {
            return;
        }

        JsonObject json = Util.readJson(packConverter.getGson(), file);
        {
            JsonObject meta = json.getAsJsonObject("meta");
            if (meta == null) meta = new JsonObject();
            meta.addProperty("game_version", version.getGameVersionName());
            json.add("meta", meta);
        }
        {
            JsonObject packObject = json.getAsJsonObject("pack");
            if (packObject == null) packObject = new JsonObject();
            packObject.addProperty("pack_format", version.getPackFormat());
            json.add("pack", packObject);
        }

        Files.write(file, Collections.singleton(packConverter.getGson().toJson(json)), Charset.forName("UTF-8"));
    }

}
