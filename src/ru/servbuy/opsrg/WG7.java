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

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

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

    public static ArrayList<String> getRegionOwners(World w, String regionName){
        Set<String> ownerSet = null;
        if (!Main.isNewVersion) {
            try {
                ownerSet = WG6.getRegionOwners(w, regionName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            ownerSet = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w))
                    .getRegion(regionName).getOwners().getPlayers();
        }
        String[] ownerArray = ownerSet.toArray(new String[ownerSet.size()]);
        ArrayList<String> owners = new ArrayList<>();
        for (Integer i = 0; i < ownerArray.length; i++)
            owners.add(Bukkit.getPlayer(ownerArray[i]).getName());
        return owners;
    }

    public boolean isProtectedRegion(final World w, final Location l){
        ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w))
                .getApplicableRegions(BukkitAdapter.asBlockVector(l));
        return isRegionInConfig(regions, false);
    }

    public boolean isProtectedMine(final World w, final Location l){
        ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w))
                .getApplicableRegions(BukkitAdapter.asBlockVector(l));
        return isRegionInConfig(regions, true);
    }

    public static boolean isRegionInConfig(ApplicableRegionSet regions, boolean checkMine){
        if (regions.size() == 0) return false;
        for (final ProtectedRegion rg : regions) {
            if (!checkMine)
                for (final Object region : Main.protectedRegions) {
                    if (rg.getId().equalsIgnoreCase(region.toString())) {
                        return true;
                    }
                }
            else
                for (final Object region : Main.protectedMines) {
                    if (rg.getId().equalsIgnoreCase(region.toString())) {
                        return true;
                    }
                }
        }
        return false;
    }

    public static boolean isRegionInConfig(ProtectedRegion region, boolean checkMine){
        if (!checkMine)
            for (final Object regions : Main.protectedRegions) {
                if (region.getId().equalsIgnoreCase(regions.toString())) {
                    return true;
                }
            }
        else
            for (final Object regions : Main.protectedMines) {
                if (region.getId().equalsIgnoreCase(regions.toString())) {
                    return true;
                }
            }
        return false;
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
