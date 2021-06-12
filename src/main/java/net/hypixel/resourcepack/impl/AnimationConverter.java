package net.hypixel.resourcepack.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;
import net.hypixel.resourcepack.pack.Pack;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class AnimationConverter extends Converter {

    public AnimationConverter(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path texturesPath = pack.getMinecraftPath().resolve("textures");
        fixAnimations(texturesPath.resolve("block"));
        fixAnimations(texturesPath.resolve("item"));
    }

    protected void fixAnimations(Path animations) throws IOException {
        if (!Files.exists(animations)) {
            return;
        }
        Files.list(animations)
                .filter(file -> file.toString().endsWith(".png.mcmeta"))
                .forEach(file -> {
                    try {
                        JsonObject json = Util.readJson(packConverter.getGson(), file);

                        boolean anyChanges = false;
                        JsonElement animationElement = json.get("animation");
                        if (animationElement instanceof JsonObject) {
                            JsonObject animationObject = (JsonObject) animationElement;

                            // TODO: Confirm this doesn't break any packs
                            animationObject.remove("width");
                            animationObject.remove("height");

                            anyChanges = true;
                        }

                        if (anyChanges) {
                            Files.write(file, packConverter.getGson().toJson(json).getBytes(StandardCharsets.UTF_8));
                            if (PackConverter.DEBUG) {
                                System.out.println("      Converted " + file.getFileName());
                            }
                        }
                    } catch (IOException e) {
                        Util.propagate(e);
                    }
                });

    }
}