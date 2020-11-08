package ru.servbuy.protectedrg;

import java.util.ArrayList;

import java.util.IdentityHashMap;
import java.util.Map;

public class ProtectedRG implements ProtectedEntity{
    private final static String path = "regions";
    private final String name;
    private final String world;
    private final String owner;
    private final String addedBy;
    private static final Map<String, ProtectedRG> regions = new IdentityHashMap<>();

    public ProtectedRG(String name, String owner, String world, String addedBy){
        this.world = world;
        this.owner = owner;
        this.addedBy = addedBy;
        this.name = name;
        regions.put(name, this);
    }

    public String getPath() {
        return path;
    }

    public static void add(ProtectedRG region) {
        regions.put(region.name, region);
    }

    public static void remove(ProtectedRG region) {
        regions.remove(region.name);
    }

    public void remove() {
        regions.remove(this.name);
    }

    public static Map<String,ProtectedRG> getRegions() {
        return regions;
    }
}
