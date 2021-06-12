package net.hypixel.resourcepack.pack;

import net.hypixel.resourcepack.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Pack {

    private static final String CONVERTED_SUFFIX = "_converted_1_13";

    protected Path path;
    private final Path workingPath;
    private final Path minecraftPath;
    protected Handler handler;

    protected Pack(Path path) {
        this.path = path;
        this.workingPath = path.getParent().resolve(getFileName() + CONVERTED_SUFFIX);
        this.minecraftPath = workingPath.resolve("assets").resolve("minecraft");
        this.handler = createHandler();
    }

    public Handler createHandler() {
        return new Handler(this);
    }

    public static Pack parse(Path path) {
        if (!path.toString().contains(CONVERTED_SUFFIX)) {
            if (path.toFile().isDirectory() && path.resolve("pack.mcmeta").toFile().exists()) {
                return new Pack(path);
            } else if (path.toString().endsWith(".zip")) {
                return new ZipPack(path);
            }
        }
        return null;
    }


    public Path getOriginalPath() {
        return path;
    }

    public Handler getHandler() {
        return handler;
    }

    public Path getWorkingPath() {
        return workingPath;
    }

    public Path getMinecraftPath() {
        return minecraftPath;
    }

    public String getFileName() {
        return path.getFileName().toString();
    }

    @Override
    public String toString() {
        return "ResourcePack{" +
                "path=" + path +
                '}';
    }

    public static class Handler {

        protected Pack pack;

        public Handler(Pack pack) {
            this.pack = pack;
        }

        public void setup() throws IOException {
            Path workingPath = pack.getWorkingPath();
            if (Files.exists(workingPath)) {
                System.out.println("  Deleting existing conversion");
                Util.deleteDirectoryAndContents(workingPath);
            }
            System.out.println("  Copying existing pack");
            Util.copyDir(pack.getOriginalPath(), workingPath);
        }

        public void finish() throws IOException {
        }

        @Override
        public String toString() {
            return "Handler{" +
                    "pack=" + pack +
                    '}';
        }
    }
}
