package net.hypixel.resourcepack.impl;

import com.google.gson.JsonObject;
import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.Pack;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class PackMetaConverter extends Converter {

    @Override
    public void convert(PackConverter main, Pack pack) throws IOException {
        Path file = pack.getPath().resolve("pack.mcmeta");
        if (!file.toFile().exists()) return;

        JsonObject json = Util.readJson(file);
        {
            JsonObject meta = json.getAsJsonObject("meta");
            if (meta == null) meta = new JsonObject();
            meta.addProperty("game_version", "1.13");
            json.add("meta", meta);
        }
        {
            JsonObject packObject = json.getAsJsonObject("pack");
            if (packObject == null) packObject = new JsonObject();
            packObject.addProperty("pack_format", 4);
            json.add("pack", packObject);
        }

        Files.write(file, Collections.singleton(json.toString()), Charset.forName("UTF-8"));
    }
}