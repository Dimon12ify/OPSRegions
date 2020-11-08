package ru.servbuy.opsrg;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.servbuy.protectedrg.ProtectedMine;
import ru.servbuy.protectedrg.ProtectedRG;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {
    private Plugin plugin;
    private String prefix = Main.prefix;

    public CommandExecutor(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("opsrg") && args.length > 0) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("opsrg.admin")) {
                onReload();
                sender.sendMessage(prefix + " §aPlugin was successfully reloaded.");
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("add") && sender.hasPermission("opsrg.admin")) {
                Main.functions.add(args[1], sender, false);
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("del") && sender.hasPermission("opsrg.admin")) {
                Main.functions.rem(args[1], sender, false);
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("addmine") && sender.hasPermission("opsrg.admin")) {
                Main.functions.add(args[1], sender, true);
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("delmine") && sender.hasPermission("opsrg.admin")) {
                Main.functions.rem(args[1], sender, true);
            }
            if (args.length <= 2 && args[0].equalsIgnoreCase("list") && sender.hasPermission("opsrg.admin")) {
                paginate(sender, args, false);
                return true;
            }
            if (args.length <= 2 && args[0].equalsIgnoreCase("listmine") && sender.hasPermission("opsrg.admin")) {
                paginate(sender, args, true);
                return true;
            }
        }
        else if (cmd.getName().equalsIgnoreCase("opsrg") && args.length == 0){
            Player p = (Player) sender;
            p.openInventory(GUI.init());
        }
        return false;
    }

    private void paginate(CommandSender sender, String[] args, boolean mine) {
        int page = 1;
        final List<List<String>> lists = mine
                ? ProtectedMine.getSplitedNames()
                : ProtectedRG.getSplitedNames();
        if (args.length == 2 && args[1].matches("^[0-9]{1,3}$") && Integer.parseInt(args[1]) <= lists.size()) {
            page = Integer.parseInt(args[1]);
        }
        sender.sendMessage(prefix + " §3Page " + page + "/" + lists.size());
        for (final String item : lists.get(page - 1)) {
            sender.sendMessage(prefix + " §2" + item);
        }
        sender.sendMessage("");
    }

    public void onReload() {
        plugin.reloadConfig();
        ProtectedRG.clear();
        ProtectedMine.clear();
        plugin.getConfig().getConfigurationSection("regions").getKeys(false)
                .forEach(name -> ProtectedRG.add(new ProtectedRG(name,
                        plugin.getConfig().getString(ProtectedRG.getPath() + "." + name + ".world"),
                        plugin.getConfig().getString(ProtectedRG.getPath() + "." + name + ".addedBy"))));
        plugin.getConfig().getConfigurationSection("mines").getKeys(false)
                .forEach(name -> ProtectedMine.add(new ProtectedMine(name,
                        plugin.getConfig().getString(ProtectedMine.getPath() + "." + name + ".world"),
                        plugin.getConfig().getString(ProtectedMine.getPath() + "." + name + ".addedBy"))));
    }
}
