package net.hypixel.resourcepack;

import net.hypixel.resourcepack.pack.Pack;

import java.io.IOException;

public abstract class Converter {

    protected PackConverter packConverter;

    public Converter(PackConverter packConverter) {
        this.packConverter = packConverter;
    }

    public abstract MinecraftVersion getVersion();

    public abstract void convert(Pack pack) throws IOException;

}