package ru.servbuy.opsrg;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {
    private Plugin plugin;
    private String prefix;

    public CommandExecutor(Plugin plugin){
        this.plugin = plugin;
        this.prefix = Main.prefix;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("opsrg") && args.length > 0) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("opsrg.admin")) {
                onReload();
                sender.sendMessage(prefix + " §aPlugin was successfully reloaded.");
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("add") && sender.hasPermission("opsrg.admin")) {
                Main.functions.add(args, sender, false);
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("del") && sender.hasPermission("opsrg.admin")) {
                Main.functions.rem(args, sender, false);
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("addmine") && sender.hasPermission("opsrg.admin")) {
                Main.functions.add(args, sender, true);
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("delmine") && sender.hasPermission("opsrg.admin")) {
                Main.functions.rem(args, sender, true);
            }
            if (args.length <= 2 && args[0].equalsIgnoreCase("list") && sender.hasPermission("opsrg.admin")) {
                final List<List<String>> list = Functions.split(Main.protectedRegions, 6);
                int x = 0;
                if (args.length == 2 && args[1].matches("^[0-9]{1,3}$") && Integer.parseInt(args[1]) <= list.size()) {
                    x = Integer.parseInt(args[1]) - 1;
                }
                final int s = 1 + x;
                sender.sendMessage(prefix + " §3Page " + s + "/" + list.size());
                for (final String ls : list.get(x)) {
                    sender.sendMessage(prefix + " §2" + ls);
                }
                sender.sendMessage("");
                return true;
            }
            if (args.length <= 2 && args[0].equalsIgnoreCase("listmine") && sender.hasPermission("opsrg.admin")) {
                final List<List<String>> list = Functions.split(Main.protectedMines, 6);
                int x = 0;
                if (args.length == 2 && args[1].matches("^[0-9]{1,3}$") && Integer.parseInt(args[1]) <= list.size()) {
                    x = Integer.parseInt(args[1]) - 1;
                }
                final int s = 1 + x;
                sender.sendMessage(prefix + " §3Page " + s + "/" + list.size());
                for (final String ls : list.get(x)) {
                    sender.sendMessage(prefix + " §2" + ls);
                }
                sender.sendMessage("");
                return true;
            }
        }
        else if (cmd.getName().equalsIgnoreCase("opsrg") && args.length == 0){
            Player p = (Player) sender;
            p.openInventory(GUI.init());
        }
        return false;
    }

    public void onReload() {
        plugin.reloadConfig();
        Set<String> rgset = plugin.getConfig().getConfigurationSection("regions").getKeys(false);
        if (rgset.size() == 0 || rgset == null)
            Main.protectedRegions = new ArrayList<>();
        else
            Main.protectedRegions = new ArrayList<>(plugin.getConfig().getConfigurationSection("regions").getKeys(false));
        Main.protectedMines = (ArrayList<String>)plugin.getConfig().getStringList("regionsmine");
    }


}
