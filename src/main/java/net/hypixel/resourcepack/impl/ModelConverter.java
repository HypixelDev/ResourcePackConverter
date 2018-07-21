package net.hypixel.resourcepack.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.pack.Pack;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public class ModelConverter extends Converter {

    @Override
    public void convert(PackConverter main, Pack pack) throws IOException {
        Path models = pack.getWorkingPath().resolve("assets\\minecraft\\models");

        remapModelJson(main, models.resolve("block"));
        remapModelJson(main, models.resolve("item"));
    }

    protected void remapModelJson(PackConverter main, Path path) throws IOException {
        if (!path.toFile().exists()) return;

        Files.list(path)
                .filter(path1 -> path1.toString().endsWith(".json"))
                .forEach(model -> {
                    try {
                        JsonObject jsonObject = Util.readJson(model);

                        // minify the json so we can replace spaces in paths easily
                        // TODO Improvement: handle this in a cleaner way?
                        String content = jsonObject.toString();
                        content = content.replaceAll("items/", "item/");
                        content = content.replaceAll("blocks/", "block/");
                        content = content.replaceAll(" ", "_");

                        Files.write(model, Collections.singleton(content), Charset.forName("UTF-8"));

                        // handle the remapping of textures, for models that use default texture names
                        jsonObject = Util.readJson(model);
                        if (jsonObject.has("textures")) {
                            NameConverter nameConverter = main.getConverter(NameConverter.class);

                            JsonObject textureObject = jsonObject.getAsJsonObject("textures");
                            for (Map.Entry<String, JsonElement> entry : textureObject.entrySet()) {
                                String value = entry.getValue().getAsString();
                                if (value.startsWith("block/")) {
                                    textureObject.addProperty(entry.getKey(), "block/" + nameConverter.getBlockMapping().remap(value.substring("block/".length())));
                                } else if (value.startsWith("item/")) {
                                    textureObject.addProperty(entry.getKey(), "item/" + nameConverter.getItemMapping().remap(value.substring("item/".length())));
                                }
                            }
                        }

                        Files.write(model, Collections.singleton(jsonObject.toString()), Charset.forName("UTF-8"));
                    } catch (IOException e) {
                        throw Util.propagate(e);
                    }
                });
    }
}