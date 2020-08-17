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
    private static Functions f;
    private static WG6 wg6;
    private static WG7 wg7;
    static String version;

    public Handler(final Main instance, final Functions func, final WG6 wg6, final WG7 wg7) {
        this.plugin = instance;
        this.f = func;
        this.wg6 = wg6;
        this.wg7 = wg7;
        version = Bukkit.getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf(".") + 1);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onBlockBreak(final BlockBreakEvent e) {
        final Player p = e.getPlayer();
        if (!p.hasPermission("OPSRegion.admin") && isWGProtection(p.getWorld(), e.getBlock().getLocation())
                && !isWGMineProtection(p.getWorld(), e.getBlock().getLocation()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onBlockPlace(final BlockPlaceEvent e) {
        final Player p = e.getPlayer();
        if (!p.hasPermission("OPSRegion.admin") && this.isWGProtection(p.getWorld(), e.getBlock().getLocation())
        && !isWGMineProtection(p.getWorld(), e.getBlock().getLocation())) {
            p.sendMessage("§cInterraction stopped");
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


    @EventHandler
    void onInventoryMoveItem(InventoryMoveItemEvent e){
        if (e.getDestination().equals(GUI.getInventory())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    void onInventoryClick(InventoryClickEvent e){
        if (!e.getInventory().equals(GUI.getInventory())) return;
        Inventory c = e.getClickedInventory();
        if (c == null) return;
        if (c.equals(GUI.getInventory()) || e.getCursor() != null || e.getCursor() != new ItemStack(Material.AIR))
            e.setCancelled(true);
    }


    boolean isWGProtection(final World w, final Location l) {
        if (version.substring(3).compareTo("13") > 0 && !version.startsWith("v1_8_") && !version.startsWith("v1_9_")){
            return wg7.isProtectedRegion(w,l);
        }
        else
            return this.wg6.isProtectedRegion(w, l);
    }

    boolean isWGMineProtection (final World w, final Location l) {
        if (version.substring(3).compareTo("13") > 0 && !version.startsWith("v1_8_") && !version.startsWith("v1_9_")) {
            return wg7.isProtectedMine(w,l);
        }
        else
            return this.wg6.isProtectedMine(w, l);
    }

    boolean isWEIntersection(final Player p) {
        if (version.substring(3).compareTo("13") > 0 && !version.startsWith("v1_8_") && !version.startsWith("v1_9_")){
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
