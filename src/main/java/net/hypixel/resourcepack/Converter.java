package net.hypixel.resourcepack;

import net.hypixel.resourcepack.pack.Pack;

import java.io.IOException;

public abstract class Converter {

    public abstract void convert(PackConverter main, Pack pack) throws IOException;

}