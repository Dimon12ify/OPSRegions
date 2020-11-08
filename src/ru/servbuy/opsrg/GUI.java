package ru.servbuy.opsrg;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import ru.servbuy.protectedrg.ProtectedRG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GUI {
    private static Inventory i;
    private static Plugin plugin;

    public GUI(Plugin plugin)
    {
        GUI.plugin = plugin;
    }

    public static Inventory init(){
        i = Bukkit.createInventory(null,5 * 9, "§lProtected Regions");
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (int index = 18; index < 27; index++)
            i.setItem(index, item);
        List<List<String>> splitRegions = ProtectedRG.getSplitedNames();
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        FillRange(splitRegions, i, head, 0);
        return i;
    }


    private static void FillRange(List<List<String>> regions, Inventory inv, ItemStack itemStack,Integer page){
        for (int i = page * 2; i < regions.size() && i <= page * 2 + 1; i++)
            for (int j = 0; j < regions.get(i).size(); j++) {
                ItemMeta meta = itemStack.getItemMeta();
                String region = regions.get(i).get(j);
                meta.setDisplayName(region);
                String[] description = new String[]{"World: " + plugin.getConfig().getString("regions." + region + ".world"),
                        "AddedBy: " + region + plugin.getConfig().getString("regions." + region + ".addedBy")};
                meta.setLore(Arrays.asList(description));
                itemStack.setItemMeta(meta);
                inv.setItem(i*9 + j + 1, itemStack);
            }
    }
    public static Inventory getInventory() {return i;}



}
