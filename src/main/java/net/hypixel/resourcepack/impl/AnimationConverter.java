package net.hypixel.resourcepack.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.MinecraftVersion;
import net.hypixel.resourcepack.Util;
import net.hypixel.resourcepack.pack.Pack;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class AnimationConverter extends Converter {

    public AnimationConverter(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public MinecraftVersion getVersion() {
        return MinecraftVersion.v1_13;
    }

    @Override
    public void convert(Pack pack) throws IOException {
        fixAnimations(pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "block"));
        fixAnimations(pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "item"));
    }

    protected void fixAnimations(Path animations) throws IOException {
        if (!animations.toFile().exists()) return;

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
                            Files.write(file, Collections.singleton(packConverter.getGson().toJson(json)), Charset.forName("UTF-8"));

                            if (PackConverter.DEBUG) System.out.println("      Converted " + file.getFileName());
                        }
                    } catch (IOException e) {
                        Util.propagate(e);
                    }
                });

    }
}