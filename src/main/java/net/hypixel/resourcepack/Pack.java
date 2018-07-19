package net.hypixel.resourcepack;

import java.nio.file.Path;

public class Pack {

    protected Path path;

    public Pack(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "ResourcePack{" +
                "path=" + path +
                '}';
    }
}
