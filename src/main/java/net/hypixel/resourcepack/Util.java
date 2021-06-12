package net.hypixel.resourcepack;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        if (Files.exists(dirPath)) {
            Files.walk(dirPath).sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    public static boolean fileExistsCorrectCasing(Path path) throws IOException {
        if (Files.exists(path)) {
            return path.toAbsolutePath().equals(path.toRealPath());
        }
        return false;
    }

    public static JsonObject readJsonResource(Gson gson, String path) {
        try (InputStream stream = PackConverter.class.getResourceAsStream(path)) {
            if (stream == null) return null;
            try (InputStreamReader streamReader = new InputStreamReader(stream)) {
                return gson.fromJson(streamReader, JsonObject.class);
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

    public static JsonObject readJson(Gson gson, Path path) throws IOException {
        return Util.readJson(gson, path, JsonObject.class);
    }

    public static <T> T readJson(Gson gson, Path path, Class<T> clazz) throws IOException {
        // TODO Improvement: this will fail if there is a BOM in the file
        return gson.fromJson(new JsonReader(Files.newBufferedReader(path)), clazz);
    }

    public static RuntimeException propagate(Throwable t) {
        throw new RuntimeException(t);
    }

}
