package ru.servbuy.opsrg;


import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.servbuy.protectedrg.ProtectedMine;
import ru.servbuy.protectedrg.ProtectedRG;

import java.util.*;
import java.util.stream.Stream;

public class WG7 {
    private static Plugin plugin;
    private static WorldEditPlugin we;
    private static WorldGuardPlugin wg;
    private static String version;

    public WG7(final Plugin plugin){
        this.plugin = plugin;
        this.we = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        this.wg = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
    }

    public static boolean isWorldGuardRegion(World w, String regionName){
        if (!Main.isNewVersion) {
            try {
                return WG6.isWorldGuardRegion(w, regionName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            Map<String, ProtectedRegion> regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w)).getRegions();
            return regions.containsKey(regionName);
        }
        return false;
    }

    public static String getRegionOwner(World w, String regionName) {
        Set<String> ownerSet = new HashSet<>();
        if (Main.isNewVersion) {
            ownerSet = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w))
                    .getRegion(regionName).getOwners().getPlayers();
        } else {
            try {
                ownerSet = WG6.getRegionOwners(w, regionName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ownerSet.isEmpty())
            return "empty";
        return ownerSet.toArray(new String[ownerSet.size()])[0];
    }

    public boolean isProtectedRegion(final World w, final Location l){
        ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w))
                .getApplicableRegions(BukkitAdapter.asBlockVector(l));
        return regions.getRegions().stream().anyMatch(x -> isRegionInConfig(x, false));
    }

    public boolean isProtectedMine(final World w, final Location l){
        ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w))
                .getApplicableRegions(BukkitAdapter.asBlockVector(l));
        return regions.getRegions().stream().anyMatch(x -> isRegionInConfig(x, true));
    }

    /*public static boolean isRegionInConfig(ApplicableRegionSet regions, boolean checkMine){
        if (regions.size() == 0) return false;
        Object[] a = regions.getRegions().stream().filter(region -> ProtectedRG.atConfig(region.getId())).toArray();
        Object[] b = regions.getRegions().stream().filter(region ->  ProtectedMine.atConfig(region.getId())).toArray();
        return b.length > 0 && checkMine || a.length > 0 && !checkMine;
    }*/

    public static boolean isRegionInConfig(ProtectedRegion region, boolean checkMine){
            if(checkMine)
                return ProtectedMine.atConfig(region.getId());
            else
                return ProtectedRG.atConfig(region.getId());
    }

    public boolean checkIntersection(final Player player) throws IncompleteRegionException {
        final Region selection = we.getSession(player).getSelection(BukkitAdapter.adapt(player.getWorld()));
        if (selection instanceof CuboidRegion){
            final BlockVector3 max = selection.getMaximumPoint();
            final BlockVector3 min = selection.getMinimumPoint();
            final RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(selection.getWorld());
            final ProtectedCuboidRegion __dummy__ = new ProtectedCuboidRegion("__dummy__", min, max);
            for (ProtectedRegion region : regionManager.getApplicableRegions(__dummy__))
                return isRegionInConfig(region, false) || isRegionInConfig(region, true);
        }
        return false;
    }
}
