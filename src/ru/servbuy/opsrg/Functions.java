package ru.servbuy.opsrg;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

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
    
    void rem(final String[] args, final CommandSender sender, final boolean b) {
        for (final String list : this.plugin.protectedRegions) {
            if (list.equalsIgnoreCase(args[1])) {
                this.plugin.protectedRegions.remove(list);
                this.plugin.getConfig().set("regions" + "." + list, null);
                this.plugin.saveConfig();
                sender.sendMessage(this.plugin.prefix + " §2" + args[1].toLowerCase() + " \u0443\u0434\u0430\u043b\u0435\u043d \u0438\u0437 \u043a\u043e\u043d\u0444\u0438\u0433\u0430.");
                return;
            }
        }
        sender.sendMessage(this.plugin.prefix + " §4" + args[1].toLowerCase() + " \u043d\u0435\u0442\u0443 \u0432 \u043a\u043e\u043d\u0444\u0438\u0433\u0435.");
    }

    void add(final String[] args, final CommandSender sender, final boolean b) {
        if (sender instanceof ConsoleCommandSender){
            sender.sendMessage("§4This command is available only for players");
            return;
        }
        ArrayList<String> rg = this.plugin.protectedRegions;
        String mine = "";
        Player p = (Player) sender;
        if (!WG7.isWorldGuardRegion(p.getWorld(), args[1])){
            p.sendMessage("§4There is no region §a" + args[1] + " §4in this world");
            return;
        }
        if (b) {
            rg = this.plugin.protectedMines;
            mine = "mine";
        }
        for (final String list : rg) {
            if (list.equalsIgnoreCase(args[1])) {
                sender.sendMessage(this.plugin.prefix + " §4" + args[1].toLowerCase() + " \u0443\u0436\u0435 \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d.");
                return;
            }
        }
        rg.add(args[1]);
        //this.plugin.getConfig().set("regions" + mine, rg);
        this.plugin.getConfig().set("regions" + mine + "." + args[1] + ".owner", WG7.getRegionOwners(p.getWorld(), args[1]));
        this.plugin.getConfig().set("regions" + mine + "." + args[1] + ".world", p.getWorld().getName());
        this.plugin.getConfig().set("regions" + mine + "." + args[1] + ".addedBy", sender.getName());
        this.plugin.saveConfig();
        sender.sendMessage(this.plugin.prefix + " §2" + args[1].toLowerCase() + " \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d \u0432 \u043a\u043e\u043d\u0444\u0438\u0433.");
    }
}
