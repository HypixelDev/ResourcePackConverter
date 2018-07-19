package net.hypixel.resourcepack;

import java.io.IOException;

public abstract class Converter {

    public abstract void rewrite(PackConverter main, Pack pack) throws IOException;

}