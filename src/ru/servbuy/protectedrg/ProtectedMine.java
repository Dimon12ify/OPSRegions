package ru.servbuy.protectedrg;

import ru.servbuy.opsrg.Functions;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ProtectedMine implements ProtectedEntity{
    private final static String path = "mines";
    private final String name;
    private final String world;
    private final String addedBy;
    private static final Map<String, ProtectedMine> regions = new IdentityHashMap<>();

    public ProtectedMine(String name, String world, String addedBy){
        this.world = world;
        this.addedBy = addedBy;
        this.name = name;
        regions.put(name, this);
    }

    public static String getPath() {
        return path;
    }

    public static void add(ProtectedMine region) {
        regions.put(region.name, region);
    }

    public static void remove(String name) {
        regions.remove(name);
    }

    public void remove() {
        regions.remove(this.name);
    }

    public static void clear() {
        regions.clear();
    }

    public static boolean atConfig(String name) {
        return regions.keySet().contains(name);
    }

    public static Map<String, ProtectedMine> getRegions() {
        return regions;
    }

    public static List<List<String>> getSplitedNames() {
        return Functions.split(new ArrayList<>(regions.keySet()),7);
    }
}
