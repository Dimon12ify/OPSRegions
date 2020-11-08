package ru.servbuy.opsrg;

import com.sk89q.worldguard.bukkit.*;
import com.sk89q.worldedit.bukkit.*;
import com.sk89q.worldguard.domains.DefaultDomain;
import org.bukkit.*;
import com.sk89q.worldguard.protection.*;
import org.bukkit.Location;
import org.bukkit.entity.*;
import com.sk89q.worldguard.protection.regions.*;
import com.sk89q.worldguard.protection.managers.*;
import ru.servbuy.protectedrg.ProtectedMine;
import ru.servbuy.protectedrg.ProtectedRG;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class WG6
{
    private Main plugin;
    private WorldGuardPlugin wg;
    private WorldEditPlugin we;

    public WG6(final Main plugin) {
        this.wg = (WorldGuardPlugin)Bukkit.getPluginManager().getPlugin("WorldGuard");
        this.we = (WorldEditPlugin)Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        this.plugin = plugin;
    }

    boolean isProtectedRegion(final World w, final Location l) {
        try {
            ApplicableRegionSet set = getRegions(w, l);
            return set.getRegions().stream().anyMatch(x -> plugin.getRegions().contains(x.getId()));
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    boolean isProtectedMine(final World w, final Location l){
        try {
            ApplicableRegionSet set = getRegions(w, l);
            return set.getRegions().stream().anyMatch(x -> plugin.getMines().contains(x.getId()));
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private ApplicableRegionSet getRegions(World w, Location l) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Class<?> wgBukkitClass = Class.forName("com.sk89q.worldguard.bukkit.WGBukkit");
        final Method getRegionManager = wgBukkitClass.getDeclaredMethod("getRegionManager", World.class);
        final Class<?> c1 = RegionManager.class;
        final Method getApplicableRegions = c1.getDeclaredMethod("getApplicableRegions", Location.class);
        return (ApplicableRegionSet) getApplicableRegions.invoke(getRegionManager.invoke(wgBukkitClass, w), l);
    }

    public static boolean isWorldGuardRegion(World w, String s) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Map<String, ProtectedRegion> regions = getRegions(w);
        return regions.containsKey(s);
    }

    public static Set<String> getRegionOwners(World w, String regionName) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        Map<String, ProtectedRegion> regions = getRegions(w);
        final Class<?> ProtectedRegion = Class.forName("com.sk89q.worldguard.protection.regions.ProtectedRegion");
        final Method getOwners = ProtectedRegion.getDeclaredMethod("getOwners");
        final DefaultDomain owners = (DefaultDomain) getOwners.invoke(regions.get(regionName));
        return owners.getPlayers();
    }

    private static Map<String, ProtectedRegion> getRegions(World w) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Class<?> wgBukkitClass = Class.forName("com.sk89q.worldguard.bukkit.WGBukkit");
        final Method getRegionManager = wgBukkitClass.getDeclaredMethod("getRegionManager", World.class);
        final Class<?> c1 = RegionManager.class;
        final Method getRegions = c1.getDeclaredMethod("getRegions");
        return (Map<String, ProtectedRegion>) getRegions.invoke(getRegionManager.invoke(wgBukkitClass, w));
    }

    public boolean checkIntersection(final Player player) {
        try {
            final Class<?> weClass = com.sk89q.worldedit.bukkit.WorldEditPlugin.class;
            final Method getSelection = weClass.getDeclaredMethod("getSelection", Player.class);
            final Class<?> Selection = Class.forName("com.sk89q.worldedit.bukkit.selections.Selection");
            final Object sel = getSelection.invoke(we, player);
            //final Selection sel = this.we.getSelection(player) <- code above make this;
            if (Class.forName("com.sk89q.worldedit.bukkit.selections.CuboidSelection").isInstance(sel)) { //if(sel instanceof CuboidSelection)
                final Class<?> Vector = Class.forName("com.sk89q.worldedit.Vector");
                final Method getNativeMinimumPoint = Selection.getDeclaredMethod("getNativeMinimumPoint");
                final Method getNativeMaximumPoint = Selection.getDeclaredMethod("getNativeMaximumPoint");
                final Object vec1 = Vector.cast(getNativeMinimumPoint.invoke(sel)); //Vector vec1 = sel.getNativeMinimumPoint();
                final Object vec2 = Vector.cast(getNativeMaximumPoint.invoke(sel)); //Vector vec2 = sel.getNativeMaximumPoint();
                final Class<?> wgBukkitClass = Class.forName("com.sk89q.worldguard.bukkit.WGBukkit");
                final Method getRegionManager = wgBukkitClass.getDeclaredMethod("getRegionManager", World.class);
                final Object regions = getRegionManager.invoke(wg, Selection.getDeclaredMethod("getWorld").invoke(sel));
                //final RegionManager regions = this.wg.getRegionManager(sel.getWorld()) <- Block above make this;
                final Class<?> BlockVector = Class.forName("com.sk89q.worldedit.BlockVector");
                final Object min = BlockVector.getConstructor(Vector).newInstance(vec1); //final BlockVector min = new BlockVector(vec1);
                final Object max = BlockVector.getConstructor(Vector).newInstance(vec2); //final BlockVector max = new BlockVector(vec2);
                final Class<?> ProtectedCuboidRegion = Class.forName("com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion");
                final Object __dummy__ = ProtectedCuboidRegion.getConstructor(String.class, BlockVector, BlockVector)
                        .newInstance("__dummy__", min, max);
                //final ProtectedRegion __dummy__ = new ProtectedCuboidRegion("__dummy__", min, max) <- Block above make this;
                final Class<?> c1 = com.sk89q.worldguard.protection.managers.RegionManager.class;
                final Method getApplicableRegions = c1.getDeclaredMethod("getApplicableRegions",
                        Class.forName("com.sk89q.worldguard.protection.regions.ProtectedRegion"));
                final ApplicableRegionSet set = (ApplicableRegionSet) getApplicableRegions.invoke(regions, __dummy__);
                for (final ProtectedRegion rg : set)
                    return WG7.isRegionInConfig(rg, false) || WG7.isRegionInConfig(rg, true);
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}