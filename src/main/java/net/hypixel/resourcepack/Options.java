package net.hypixel.resourcepack;

import joptsimple.OptionParser;
import joptsimple.OptionSpec;
import joptsimple.ValueConverter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Options {

    public static final OptionParser PARSER = new OptionParser();

    public static final OptionSpec<Void> HELP = PARSER.acceptsAll(Arrays.asList("?", "h", "help"), "Print this message.").forHelp();

    public static final OptionSpec<Path> INPUT_DIR = PARSER.acceptsAll(Arrays.asList("i", "input", "input-dir"), "Input directory for the packs")
            .withRequiredArg()
            .withValuesConvertedBy(new PathConverter())
            .defaultsTo(Paths.get("./"));

    public static final OptionSpec<Void> MINIFY = PARSER.accepts("minify", "Minify the json files.");

    public static final OptionSpec<MinecraftVersion> VERSION = PARSER.acceptsAll(Arrays.asList("version", "v", "ver"), "The wanted output version for the resource pack.")
            .withRequiredArg()
            .withValuesConvertedBy(new MinecraftVersionConverter())
            .defaultsTo(MinecraftVersion.v1_14);

    private static class PathConverter implements ValueConverter<Path> {

        @Override
        public Path convert(String s) {
            return Paths.get(s);
        }

        @Override
        public Class<? extends Path> valueType() {
            return Path.class;
        }

        @Override
        public String valuePattern() {
            return "*";
        }
    }

    private static class MinecraftVersionConverter implements ValueConverter<MinecraftVersion> {

        @Override
        public MinecraftVersion convert(String s) {
            if (s.equalsIgnoreCase("latest")) {
                MinecraftVersion[] values = MinecraftVersion.values();
                return values[values.length - 1];
            }

            return MinecraftVersion.getByName(s);
        }

        @Override
        public Class<? extends MinecraftVersion> valueType() {
            return MinecraftVersion.class;
        }

        @Override
        public String valuePattern() {
            return "*";
        }
    }

}