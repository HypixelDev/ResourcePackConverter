package net.hypixel.resourcepack;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public final class Util {
    
    private Util() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static void copyDir(Path src, Path dest) throws IOException {
        Files.walk(src).forEach(path -> {
            try {
                Files.copy(path, dest.resolve(src.relativize(path)));
            } catch (Throwable e) {
                throw Util.propagate(e);
            }
        });
    }

    public static void deleteDirectoryAndContents(Path dirPath) throws IOException {
        if (!dirPath.toFile().exists()) return;

        //noinspection ResultOfMethodCallIgnored
        Files.walk(dirPath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    public static boolean fileExistsCorrectCasing(Path path) throws IOException {
        if (!path.toFile().exists()) return false;
        return path.toString().equals(path.toFile().getCanonicalPath());
    }

    public static JsonObject readJsonResource(String path) {
        try (InputStream stream = PackConverter.class.getResourceAsStream(path)) {
            if (stream == null) return null;
            try (InputStreamReader streamReader = new InputStreamReader(stream)) {
                return PackConverter.GSON.fromJson(streamReader, JsonObject.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage readImageResource(String path) {
        try (InputStream stream = PackConverter.class.getResourceAsStream(path)) {
            if (stream == null) return null;
            return ImageIO.read(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonObject readJson(Path path) throws IOException {
        return Util.readJson(path, JsonObject.class);
    }

    public static <T> T readJson(Path path, Class<T> clazz) throws IOException {
        // TODO Improvement: this will fail if there is a BOM in the file
        return PackConverter.GSON.fromJson(new JsonReader(new FileReader(path.toFile())), clazz);
    }

    /**
     * @return null if file doesn't exist, {@code true} if successfully renamed, {@code false} if failed
     */
    public static Boolean renameFile(Path file, String newName) {
        if (!file.toFile().exists()) return null;
        return file.toFile().renameTo(new File(file.getParent() + "/" + newName));
    }

    public static RuntimeException propagate(Throwable t) {
        throw new RuntimeException(t);
    }

}
