package net.hypixel.resourcepack.impl;

import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.pack.Pack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpacesConverter extends Converter {

    public SpacesConverter(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path assets = pack.getWorkingPath().resolve("assets");
        if (!assets.toFile().exists()) return;

        Files.walk(assets).forEach(file -> {
            String fileName = file.getFileName().toString();
            if (fileName.contains(" ")) {
                String noSpaces = fileName.replaceAll(" ", "_");
                try {
                    if (Files.exists(file)) {
                        Files.move(file, file.getParent().resolve(noSpaces));
                        if (PackConverter.DEBUG) {
                            System.out.println("      Renamed: " + file.getFileName().toString() + "->" + noSpaces);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("      Failed to rename: " + file.getFileName().toString() + "->" + noSpaces);
                }
            }
        });
    }

}
