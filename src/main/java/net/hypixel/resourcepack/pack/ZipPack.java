package net.hypixel.resourcepack.pack;

import net.hypixel.resourcepack.Util;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ZipPack extends Pack {

    public ZipPack(Path path) {
        super(path);
    }

    @Override
    public ZipPack.Handler createHandler() {
        return new ZipPack.Handler(this);
    }

    @Override
    public String getFileName() {
        return path.getFileName().toString().substring(0, path.getFileName().toString().length() - 4);
    }

    public static class Handler extends Pack.Handler {

        public Handler(Pack pack) {
            super(pack);
        }

        public Path getConvertedZipPath() {
            return pack.getWorkingPath().getParent().resolve(pack.getWorkingPath().getFileName() + ".zip");
        }

        @Override
        public void setup() throws IOException {
            Path workingPath = pack.getWorkingPath();
            if (Files.exists(workingPath)) {
                System.out.println("  Deleting existing conversion");
                Util.deleteDirectoryAndContents(workingPath);
            }

            Path convertedZipPath = getConvertedZipPath();
            if (Files.exists(convertedZipPath)) {
                System.out.println("  Deleting existing conversion zip");
                try {
                    Files.delete(convertedZipPath);
                } catch (IOException e) {
                    System.err.println("  Failed to delete existing conversion zip: " + e.getMessage());
                }
            }
            try {
                Files.createDirectory(workingPath);
            }catch (IOException e) {
                System.err.println("  Failed to create working path: " + e.getMessage());
            }
            try {
                ZipFile zipFile = new ZipFile(pack.getOriginalPath().toFile());
                zipFile.extractAll(workingPath.toString());
            } catch (ZipException e) {
                Util.propagate(e);
            }
        }

        @Override
        public void finish() throws IOException {
            try {
                System.out.println("  Zipping working directory");
                ZipFile zipFile = new ZipFile(getConvertedZipPath().toFile());
                ZipParameters parameters = new ZipParameters();
                parameters.setIncludeRootFolder(false);
                zipFile.createZipFileFromFolder(pack.getWorkingPath().toFile(), parameters, false, 65536);
            } catch (ZipException e) {
                Util.propagate(e);
            }

            System.out.println("  Deleting working directory");
            Util.deleteDirectoryAndContents(pack.getWorkingPath());
        }

        @Override
        public String toString() {
            return "Handler{} " + super.toString();
        }
    }
}
