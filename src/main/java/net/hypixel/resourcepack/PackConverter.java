package net.hypixel.resourcepack;

import com.google.gson.Gson;
import joptsimple.OptionSet;
import net.hypixel.resourcepack.impl.*;
import net.hypixel.resourcepack.pack.Pack;

import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class PackConverter {

    public static final Gson GSON = new Gson();
    public static final boolean DEBUG = true;

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

    public void run() throws IOException {
        Files.list(optionSet.valueOf(Options.INPUT_DIR))
                .map(Pack::parse)
                .filter(Objects::nonNull)
                .forEach(pack -> {
                    try {
                        System.out.println("Converting " + pack);

                        pack.getHandler().setup();

                        System.out.println("  Running Converters");
                        for (Converter converter : converters.values()) {
                            if (PackConverter.DEBUG) System.out.println("    Running " + converter.getClass().getSimpleName());
                            converter.convert(this, pack);
                        }

                        pack.getHandler().finish();
                    } catch (Throwable t) {
                        System.err.println("Failed to convert!");
                        Util.propagate(t);
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