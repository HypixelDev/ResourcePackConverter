package net.hypixel.resourcepack;

import java.util.HashMap;
import java.util.Map;

public enum MinecraftVersion {

    v1_13("1.13", 4),
    v1_14("1.14", 4),
    v1_20("1.20", 15)
    ;

    private static final Map<String, MinecraftVersion> BY_NAME;

    static {
        MinecraftVersion[] versions = values();
        BY_NAME = new HashMap<>(versions.length);

        for (MinecraftVersion version : versions) {
            BY_NAME.put(version.gameVersionName, version);
        }
    }

    private final String gameVersionName;
    private final int packFormat;

    private MinecraftVersion(String gameVersionName, int packFormat) {
        this.gameVersionName = gameVersionName;
        this.packFormat = packFormat;
    }

    public String getGameVersionName() {
        return gameVersionName;
    }

    public int getPackFormat() {
        return packFormat;
    }

    public static MinecraftVersion getByName(String name) {
        return BY_NAME.get(name);
    }

}
