package ru.servbuy.opsrg;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.*;
import org.bukkit.permissions.*;
import org.bukkit.plugin.*;
import ru.servbuy.protectedrg.ProtectedMine;
import ru.servbuy.protectedrg.ProtectedRG;

import java.util.*;

public class Main extends JavaPlugin
{
    public static Permission permission;
    public static String prefix;
    public static Functions functions;
    public static WG6 WorldGuard6;
    public static WG7 WorldGuard7;
    public static String version;
    
    static {
        Main.permission = null;
        version = Bukkit.getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf(".") + 1);
    }

    public static boolean isNewVersion = version.substring(3).compareTo("13") > 0 && !version.startsWith("v1_8_") && !version.startsWith("v1_9_");
    
    public Main() {
        prefix = "§8§l[§c§lOPS§f§lRegion§8§l]";
    }
    
    private void setupPermissions() {
        final RegisteredServiceProvider<Permission> permissionProvider = (RegisteredServiceProvider<Permission>)getServer().getServicesManager().getRegistration((Class)Permission.class);
        if (permissionProvider != null) {
            Main.permission = permissionProvider.getProvider();
        }
    }
    
    public void onEnable() {
        functions = new Functions(this);
        WorldGuard6 = new WG6(this);
        WorldGuard7 = new WG7(this);
        getConfig().options().copyDefaults(true);
        saveConfig();
        getConfig().getConfigurationSection("regions").getKeys(false)
                .forEach(name -> ProtectedRG.add(new ProtectedRG(name,
                getConfig().getString(ProtectedRG.getPath() + "." + name + ".world"),
                getConfig().getString(ProtectedRG.getPath() + "." + name + ".addedBy"))));
        getConfig().getConfigurationSection("mines").getKeys(false)
                .forEach(name -> ProtectedMine.add(new ProtectedMine(name,
                getConfig().getString(ProtectedMine.getPath() + "." + name + ".world"),
                getConfig().getString(ProtectedMine.getPath() + "." + name + ".addedBy"))));
        setupPermissions();
        GUI gui = new GUI(this);
        final PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new Handler(this), this);
        getCommand("opsrg").setExecutor(new CommandExecutor(this));
    }

    public Set<String> getMines() {
        return getConfig().getConfigurationSection("mines").getKeys(false);
    }

    public Set<String> getRegions() {
        return getConfig().getConfigurationSection("regions").getKeys(false);
    }
}
