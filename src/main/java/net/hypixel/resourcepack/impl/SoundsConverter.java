package net.hypixel.resourcepack.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;
import net.hypixel.resourcepack.pack.Pack;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public class SoundsConverter extends Converter {

    @Override
    public void convert(PackConverter main, Pack pack) throws IOException {
        Path soundsJsonPath = pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "sounds.json");
        if (!soundsJsonPath.toFile().exists()) return;

        JsonObject sounds = Util.readJson(soundsJsonPath);
        JsonObject newSoundsObject = new JsonObject();

        for (Map.Entry<String, JsonElement> entry : sounds.entrySet()) {
            if (entry.getValue().isJsonObject()) {
                JsonObject soundObject = entry.getValue().getAsJsonObject();
                if (soundObject.has("sounds") && soundObject.get("sounds").isJsonArray()) {
                    JsonArray soundsArray = soundObject.getAsJsonArray("sounds");

                    JsonArray newSoundsArray = new JsonArray();
                    for (JsonElement jsonElement : soundsArray) {
                        String sound;

                        Path baseSoundsPath = pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "sounds");
                        Path path = baseSoundsPath.resolve(jsonElement.getAsString() + ".ogg");
                        if (!Util.fileExistsCorrectCasing(path)) {
                            String rewrite = path.toFile().getCanonicalPath().substring(baseSoundsPath.toString().length() + 1, path.toFile().getCanonicalPath().length() - 4);
                            if (PackConverter.DEBUG) System.out.println("      Rewriting Sound: '" + jsonElement.getAsString() + "' -> '" + rewrite + "'");
                            sound = rewrite;
                        } else {
                            sound = jsonElement.getAsString();
                        }

                        // windows fix
                        sound = sound.replaceAll("\\\\", "/");
                        newSoundsArray.add(sound);
                    }
                    soundObject.add("sounds", newSoundsArray);
                }

                newSoundsObject.add(entry.getKey().toLowerCase(), soundObject);
            }
        }

        Files.write(soundsJsonPath, Collections.singleton(newSoundsObject.toString()), Charset.forName("UTF-8"));
    }

}
