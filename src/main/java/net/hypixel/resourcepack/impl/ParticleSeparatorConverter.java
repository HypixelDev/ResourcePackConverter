package net.hypixel.resourcepack.impl;

import net.hypixel.resourcepack.Converter;
import net.hypixel.resourcepack.MinecraftVersion;
import net.hypixel.resourcepack.PackConverter;
import net.hypixel.resourcepack.pack.Pack;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

public class ParticleSeparatorConverter extends Converter {

    private static final List<ExtractableParticle> PARTICLES = Arrays.asList(
        new HorizontalParticleSequence("generic", 0, 0, 8),
        // new HorizontalParticleSequence("unknown_particle_sequence", 0, 8, 3), // Maybe "big splash"? Couldn't find it in modern packs
        new HorizontalParticleSequence("splash", 24, 8, 4),
        new Particle("bubble", 0, 16),
        // new Particle("unknown_particle", 8, 16), // Fishing bobber. Likely just replaced with an entity texture
        new Particle("flash", 32, 16, 32, 32),
        new Particle("flame", 0, 24),
        // new Particle("unknown_particle", 8, 24), // Looks like flame but zoomed in and centered. Ember? Does that exist?
        new Particle("note", 0, 32),
        new Particle("critical_hit", 8, 32),
        new Particle("enchanted_hit", 16, 32),
        new Particle("heart", 0, 40),
        new Particle("angry", 8, 40),
        new Particle("glint", 16, 40),
        // new Particle("unknown_particle", 24, 40) // An angry villager? Was this unused?
        // new Particle("unknown_particle", 0, 48) // A blue drip?
        // new Particle("glint", 8, 48) // A blue sphere/orb thing
        new Particle("drip_hang", 0, 56),
        new Particle("drip_fall", 8, 56),
        new Particle("drip_land", 16, 56),
        new HorizontalParticleSequence("spell", 0, 64, 8),
        // new HorizontalParticleSequence("unknown_particle_sequence", 0, 72, 8), // Looks VERY similar to spark, but it's not the same
        new HorizontalParticleSequence("spark", 0, 80, 8)
    );

    // 1.14 separated particles into their own individual files
    public ParticleSeparatorConverter(PackConverter packConverter) {
        super(packConverter);
    }

    @Override
    public MinecraftVersion getVersion() {
        return MinecraftVersion.v1_14;
    }

    @Override
    public void convert(Pack pack) throws IOException {
        Path particleDirectoryPath = pack.getWorkingPath().resolve("assets/minecraft/textures/particle/");
        Path particlesImagePath = particleDirectoryPath.resolve("particles.png");
        if (Files.notExists(particlesImagePath)) {
            return;
        }

        BufferedImage textureAtlas = ImageIO.read(particlesImagePath.toFile());
        for (ExtractableParticle particle : PARTICLES) {
            particle.extract(particleDirectoryPath, textureAtlas);
        }

        Files.deleteIfExists(particlesImagePath); // No longer necessary, we can delete it
        System.out.println("      Deleted particles.png");
    }

    private static interface ExtractableParticle {

        public void extract(Path particlesDirectory, BufferedImage textureAtlas) throws IOException;

    }

    private static class Particle implements ExtractableParticle {

        private static final int DEFAULT_PARTICLE_SIZE = 8;

        private final String modernTextureName;
        private final int textureX, textureY;
        private final int textureWidth, textureHeight;

        public Particle(String modernTextureName, int textureX, int textureY, int textureWidth, int textureHeight) {
            this.modernTextureName = modernTextureName;
            this.textureX = textureX;
            this.textureY = textureY;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }

        public Particle(String modernTextureName, int textureX, int textureY) {
            this(modernTextureName, textureX, textureY, DEFAULT_PARTICLE_SIZE, DEFAULT_PARTICLE_SIZE);
        }

        @Override
        public void extract(Path particlesDirectory, BufferedImage textureAtlas) throws IOException {
            BufferedImage image = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);

            Graphics graphics = image.getGraphics();
            graphics.drawImage(textureAtlas, 0, 0, textureWidth, textureHeight, textureX, textureY, textureX + textureWidth, textureY + textureHeight, null);

            String fileName = modernTextureName + ".png";
            Path filePath = particlesDirectory.resolve(fileName);
            ImageIO.write(image, "png", Files.newOutputStream(filePath, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE));

            System.out.println("      Extracted " + fileName + " from particles.png");
        }

    }

    private static class HorizontalParticleSequence implements ExtractableParticle {

        private final List<Particle> particles;

        public HorizontalParticleSequence(String modernTextureName, int textureStartX, int textureStartY, int textureWidth, int textureHeight, int count) {
            List<Particle> particles = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                int frameTextureStartX = textureStartX + (textureWidth * i);
                particles.add(new Particle(modernTextureName + "_" + i, frameTextureStartX, textureStartY, textureWidth, textureHeight));
            }
            this.particles = particles;
        }

        public HorizontalParticleSequence(String modernTextureName, int textureStartX, int textureStartY, int count) {
            this(modernTextureName, textureStartX, textureStartY, Particle.DEFAULT_PARTICLE_SIZE, Particle.DEFAULT_PARTICLE_SIZE, count);
        }

        @Override
        public void extract(Path particlesDirectory, BufferedImage textureAtlas) throws IOException {
            for (Particle particle : particles) {
                particle.extract(particlesDirectory, textureAtlas);
            }
        }

    }

}
