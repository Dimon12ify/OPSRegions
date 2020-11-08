package ru.servbuy.opsrg;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.servbuy.protectedrg.ProtectedEntity;
import ru.servbuy.protectedrg.ProtectedMine;
import ru.servbuy.protectedrg.ProtectedRG;

import java.util.*;

public class Functions
{
    public Main plugin;
    
    public Functions(final Main instance) {
        this.plugin = instance;
    }
    
    public static <T> List<List<T>> split(final List<T> list, final int targetSize) {
        final List<List<T>> lists = new ArrayList<>();
        for (int i = 0; i < list.size(); i += targetSize) {
            lists.add(list.subList(i, Math.min(i + targetSize, list.size())));
        }
        return lists;
    }
    
    void rem(String name, final CommandSender sender, final boolean mine) {
        name = name.toLowerCase();
        if (!ProtectedMine.atConfig(name) && mine || !ProtectedRG.atConfig(name) && !mine) {
            sender.sendMessage(Main.prefix + " §4" + name + " нет в конфиге.");
            return;
        }
        String path = mine? ProtectedMine.getPath() : ProtectedRG.getPath();
        if (mine)
            ProtectedMine.remove(name);
        else
            ProtectedRG.remove(name);
        plugin.getConfig().set(path + "." + name, null);
        plugin.saveConfig();
        sender.sendMessage(Main.prefix + " §2" + name + " удалён из конфига.");
    }

    void add(String name, final CommandSender sender, final boolean mine) {
        name = name.toLowerCase();
        if (sender instanceof ConsoleCommandSender){
            sender.sendMessage(Main.prefix + "§4This command is available only for players");
            return;
        }
        Player p = (Player) sender;
        if (!WG7.isWorldGuardRegion(p.getWorld(), name)) {
            p.sendMessage(Main.prefix + " §4There is no region §a" + name + " §4in this world");
            return;
        }
        String path = mine? ProtectedMine.getPath() : ProtectedRG.getPath();
        String world = p.getWorld().getName();
        String addedBy = sender.getName();
        if (ProtectedRG.getRegions().containsKey(name) && !mine || ProtectedMine.getRegions().containsKey(name) && mine) {
            sender.sendMessage(Main.prefix + " §4" + name + " уже добавлен.");
            return;
        }
        ProtectedEntity e = mine
                ? new ProtectedMine(name, world, addedBy)
                : new ProtectedRG(name, world, addedBy);
        ProtectedEntity.add(e);
        plugin.getConfig().set(path + "." + name + ".world", world);
        plugin.getConfig().set(path + "." + name + ".addedBy", addedBy);
        plugin.saveConfig();
        sender.sendMessage(Main.prefix + " §2" + name.toLowerCase() + " добавлен в конфиг.");
    }
}
