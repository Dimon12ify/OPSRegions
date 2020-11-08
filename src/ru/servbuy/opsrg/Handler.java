package ru.servbuy.opsrg;

import com.sk89q.worldedit.IncompleteRegionException;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.hanging.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.world.*;
import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Handler implements Listener
{
    private static Main plugin;
    private static Functions f = Main.functions;
    private static WG6 wg6 = Main.WorldGuard6;
    private static WG7 wg7 = Main.WorldGuard7;

    public Handler(final Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onBlockBreak(final BlockBreakEvent e) {
        final Player p = e.getPlayer();
        if (p.hasPermission("OPSRegion.admin") || isWGMineProtection(p.getWorld(), e.getBlock().getLocation())) {
            e.setCancelled(false);
            return;
        }
        e.setCancelled(isWGProtection(p.getWorld(), e.getBlock().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onBlockPlace(final BlockPlaceEvent e) {
        final Player p = e.getPlayer();
        if (!p.hasPermission("OPSRegion.admin") && isWGProtection(p.getWorld(), e.getBlock().getLocation())
        && !isWGMineProtection(p.getWorld(), e.getBlock().getLocation())) {
            p.sendMessage("§cInteraction stopped");
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onBucketEmpty(final PlayerBucketEmptyEvent e) {
        final Player p = e.getPlayer();
        if (!p.hasPermission("OPSRegion.admin") && this.isWGProtection(p.getWorld(), e.getBlockClicked().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    void onBucketFill(final PlayerBucketFillEvent e) {
        final Player p = e.getPlayer();
        if (!p.hasPermission("OPSRegion.admin") && this.isWGProtection(p.getWorld(), e.getBlockClicked().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    void onHangingPlace(final HangingPlaceEvent e) {
        final Player p = e.getPlayer();
        if (!p.hasPermission("OPSRegion.admin") && this.isWGProtection(p.getWorld(), e.getBlock().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onHangingBreak(final HangingBreakByEntityEvent e) {
        if (e.getRemover() instanceof Player) {
            final Player p = (Player)e.getRemover();
            if (!p.hasPermission("OPSRegion.admin") && this.isWGProtection(p.getWorld(), p.getLocation())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onFrameInteract(final PlayerInteractEntityEvent e) {
        final Player p = e.getPlayer();
        if (!p.hasPermission("OPSRegion.admin") && e.getRightClicked().getType() == EntityType.ITEM_FRAME
                && this.isWGProtection(p.getWorld(), p.getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onFrameDamage(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntityType() == EntityType.ITEM_FRAME) {
            final Player p = (Player)e.getDamager();
            if (!p.hasPermission("OPSRegion.admin") && this.isWGProtection(p.getWorld(), p.getLocation())) {
                e.setCancelled(true);
            }
        }
        else if (e.getEntityType() == EntityType.ITEM_FRAME) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onStructureGrow(final StructureGrowEvent e) {
        if (this.isWGProtection(e.getWorld(), e.getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onPlayerCommand(final PlayerCommandPreprocessEvent e) {
        final Player p = e.getPlayer();
        final String[] s = e.getMessage().toLowerCase().split(" ");
        if (!p.hasPermission("OPSRegion.admin")) {
            if (this.isBlockedWorldEditCommand(s[0]) && this.isWEIntersection(p)) {
                e.setCancelled(true);
            }
        }
    }


    boolean isWGProtection(final World w, final Location l) {
        if (Main.isNewVersion){
            return wg7.isProtectedRegion(w,l);
        }
        else
            return wg6.isProtectedRegion(w, l);
    }

    boolean isWGMineProtection (final World w, final Location l) {
        if (Main.isNewVersion) {
            return wg7.isProtectedMine(w,l);
        }
        else
            return wg6.isProtectedMine(w, l);
    }

    boolean isWEIntersection(final Player p) {
        if (Main.isNewVersion){
            try {
                return wg7.checkIntersection(p);
            } catch (IncompleteRegionException e) {
                e.printStackTrace();
            }
        }
        else
            return wg6.checkIntersection(p);
        return false;
    }

    boolean isBlockedWorldEditCommand(String s) {
        s = s.replace("worldedit:", "");
        final String[] cmds = { "//set", "//replace", "//overlay", "//walls", "//outline", "//smooth", "//deform", "//hollow", "//regen", "//cut", "//hcyl", "//cyl", "//sphere", "//hsphere", "//pyramid", "//hpyramid" };
        String[] array;
        for (int length = (array = cmds).length, i = 0; i < length; ++i) {
            final String cmd = array[i];
            if (cmd.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}
