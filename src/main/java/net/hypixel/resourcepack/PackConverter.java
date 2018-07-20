package net.hypixel.resourcepack;

import com.google.gson.Gson;
import joptsimple.OptionSet;
import net.hypixel.resourcepack.impl.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class PackConverter {

    public static final Gson GSON = new Gson();
    public static final boolean DEBUG = true;

    protected static final String CONVERTED_END = "_converted_1_13";

    protected final OptionSet optionSet;
    protected final Map<Class<? extends Converter>, Converter> converters = new LinkedHashMap<>();

    public PackConverter(OptionSet optionSet) {
        this.optionSet = optionSet;

        // this needs to be run first, other converters might reference new directory names
        this.registerConverter(new NameConverter());

        this.registerConverter(new PackMetaConverter());

        this.registerConverter(new ModelConverter());
        this.registerConverter(new SpacesConverter());
        this.registerConverter(new SoundsConverter());
        this.registerConverter(new ParticleConverter());
        this.registerConverter(new BlockStateConverter());
        this.registerConverter(new AnimationConverter());
        this.registerConverter(new MapIconConverter());
    }

    public void registerConverter(Converter converter) {
        converters.put(converter.getClass(), converter);
    }

    public <T extends Converter> T getConverter(Class<T> clazz) {
        //noinspection unchecked
        return (T) converters.get(clazz);
    }

    protected boolean isPack(Path path) {
        // close enough
        return path.toFile().isDirectory() &&
                path.resolve("pack.mcmeta").toFile().exists() &&
                !path.toString().endsWith(CONVERTED_END);
    }

    public void run() throws IOException {
        if (PackConverter.DEBUG) System.out.println(optionSet.asMap());

        Files.list(optionSet.valueOf(Options.INPUT_DIR))
                // TODO Improvement: Support zip files?
                .filter(this::isPack)
                .forEach(path -> {
                    try {
                        Path newPath = path.getParent().resolve(path.getFileName() + CONVERTED_END);
                        Pack pack = new Pack(newPath);

                        System.out.println("Converting " + pack);

                        if (newPath.toFile().exists()) {
                            System.out.println("  Deleting existing conversion");
                            Util.deleteDirectoryAndContents(newPath);
                        }

                        System.out.println("  Copying existing pack");
                        Util.copyDir(path, newPath);

                        try {
                            System.out.println("  Running Converters");

                            for (Converter converter : converters.values()) {
                                if (PackConverter.DEBUG) System.out.println("    Running " + converter.getClass().getSimpleName());
                                converter.convert(this, pack);
                            }
                        } catch (IOException e) {
                            System.err.println("Failed to convert!");
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        Util.propagate(e);
                    }
                });
    }

    @Override
    public String toString() {
        return "PackConverter{" +
                "optionSet=" + optionSet +
                ", converters=" + converters +
                '}';
    }
}