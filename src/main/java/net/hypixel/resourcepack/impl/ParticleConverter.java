package net.hypixel.resourcepack.impl;

import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.pack.Pack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ParticleConverter extends Converter {

    public ParticleConverter(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path imagePath = pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "particle" + File.separator + "particles.png");
        if (!imagePath.toFile().exists()) return;

        BufferedImage image = ImageIO.read(imagePath.toFile());

        // TODO check how higher resolution will handle this.
        if (image.getWidth() == 128 && image.getHeight() == 128) {
            // make a new bigger image and just paste the existing on in the top left corner
            BufferedImage newImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D) newImage.getGraphics();
            g2d.drawImage(image, 0, 0, null);

            // save the new one
            ImageIO.write(newImage, "png", imagePath.toFile());
        }
    }

}
