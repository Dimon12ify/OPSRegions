package ru.servbuy.opsrg;

import com.mojang.datafixers.types.Func;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUI {
    private static Inventory i;

    public GUI(Functions functions)
    {

    }

    public static Inventory init(){
        i = Bukkit.createInventory(null, 5 * 9, "§lProtected Regions");
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        for (Integer index = 18; index < 27; index++)
            i.setItem(index, item);
        List<List<String>> splitedRegions = Functions.split(Main.protectedRegions, 7);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        FillRange(splitedRegions, i, head, 0);
        return i;
    }

    private static void FillRange(List<List<String>> regions, Inventory inv, ItemStack itemStack,Integer page){
        for (Integer i  = page * 2; i < regions.size() && i <= page * 2 + 1; i++)
            for (Integer j = 0; j < regions.get(i).size(); j++) {
                ItemMeta meta = itemStack.getItemMeta();
                String region = regions.get(i).get(j);
                meta.setDisplayName(region);
                ArrayList<String> desc = new ArrayList<>();
                itemStack.setItemMeta(meta);
                inv.setItem(i*9 + j + 1, itemStack);
            }
    }
    public static Inventory getInventory() {return i;}
}
