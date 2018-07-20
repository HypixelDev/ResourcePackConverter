package net.hypixel.resourcepack.impl;

import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.Pack;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpacesConverter extends Converter {

    @Override
    public void convert(PackConverter main, Pack pack) throws IOException {
        Path assets = pack.getPath().resolve("assets");
        if (!assets.toFile().exists()) return;

        Files.walk(assets).forEach(path -> {
            if (!path.getFileName().toString().contains(" ")) return;

            String noSpaces = path.getFileName().toString().replaceAll(" ", "_");
            Boolean ret = Util.renameFile(path, noSpaces);
            if (ret == null) return;
            if (ret && PackConverter.DEBUG) {
                System.out.println("      Renamed: " + path.getFileName().toString() + "->" + noSpaces);
            } else if (!ret) {
                System.err.println("      Failed to rename: " + path.getFileName().toString() + "->" + noSpaces);
            }
        });
    }

}
