package net.hypixel.resourcepack.impl;

import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.MinecraftVersion;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.pack.Pack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Handles the conversion of the singular painting image to split images from 1.14
 */
public class PaintingConverter extends Converter {
    private final Map<Location, String> paintings = new HashMap<>();

    public PaintingConverter(PackConverter packConverter) {
        super(packConverter);
        registerPaintings();
    }

    /**
     * Registers the painting locations, 1 unit here = 16 pixels on the default 256x256 image
     */
    private void registerPaintings() {
        // 1x1 paintings
        this.paintings.put(new Location(0, 0, 1, 1), "kebab");
        this.paintings.put(new Location(1, 0, 1, 1), "aztec");
        this.paintings.put(new Location(2, 0, 1, 1), "alban");
        this.paintings.put(new Location(3, 0, 1, 1), "aztec2");
        this.paintings.put(new Location(4, 0, 1, 1), "bomb");
        this.paintings.put(new Location(5, 0, 1, 1), "plant");
        this.paintings.put(new Location(6, 0, 1, 1), "wasteland");

        // 2x1 paintings
        this.paintings.put(new Location(0, 2, 2, 1), "pool");
        this.paintings.put(new Location(2, 2, 2, 1), "courbet");
        this.paintings.put(new Location(4, 2, 2, 1), "sea");
        this.paintings.put(new Location(6, 2, 2, 1), "sunset");
        this.paintings.put(new Location(8, 2, 2, 1), "creebet");

        // 1x2 paintings
        this.paintings.put(new Location(0, 4, 1, 2), "wanderer");
        this.paintings.put(new Location(1, 4, 1, 2), "graham");

        // 4x2 painting
        this.paintings.put(new Location(0, 6, 4, 2), "fighters");

        // 2x2 paintings
        this.paintings.put(new Location(0, 8, 2, 2), "match");
        this.paintings.put(new Location(2, 8, 2, 2), "bust");
        this.paintings.put(new Location(4, 8, 2, 2), "stage");
        this.paintings.put(new Location(6, 8, 2, 2), "void");
        this.paintings.put(new Location(8, 8, 2, 2), "skull_and_roses");
        this.paintings.put(new Location(10, 8, 2, 2), "wither");

        // 4x4 paintings
        this.paintings.put(new Location(0, 12, 4, 4), "pointer");
        this.paintings.put(new Location(4, 12, 4, 4), "pigscene");
        this.paintings.put(new Location(8, 12, 4, 4), "burning_skull");

        // 4x3 paintings
        this.paintings.put(new Location(12, 4, 4, 3), "skeleton");
        this.paintings.put(new Location(12, 7, 4, 3), "donkey_kong");
    }

    @Override
    public MinecraftVersion getVersion() {
        return MinecraftVersion.v1_14;
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path imagePath = pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "painting" + File.separator + "paintings_kristoffer_zetterstrand.png");
        if (!Files.exists(imagePath)) return;

        BufferedImage image = ImageIO.read(imagePath.toFile());

        Path newDir = pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "paintings");
        if (!Files.exists(newDir)) {
            Files.createDirectory(newDir);
        }

        int multiplier = image.getWidth() / 16;
        this.paintings.forEach((location, output) -> {
            BufferedImage subImage = image.getSubimage(location.x * multiplier, location.y * multiplier, location.width * multiplier, location.height * multiplier);
            Path outputFile = newDir.resolve(output + ".png");
            try {
                ImageIO.write(subImage, "png", outputFile.toFile());
                if (PackConverter.DEBUG) {
                    System.out.println("      Exported painting " + outputFile.getFileName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static class Location {
        int x;
        int y;
        private final int width;
        private final int height;

        Location(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Location location = (Location) o;
            return x == location.x && y == location.y && width == location.width && height == location.height;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, width, height);
        }
    }

}
