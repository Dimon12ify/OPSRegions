package ru.servbuy.opsrg;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.*;
import org.bukkit.permissions.*;
import org.bukkit.plugin.*;
import org.bukkit.command.*;
import java.util.*;

public class Main extends JavaPlugin
{
    public static Permission permission;
    public static String prefix;
    public static ArrayList<String> protectedRegions;
    public static ArrayList<String> protectedMines;
    public static Functions functions;
    WG6 WorldGuard6;
    WG7 WorldGuard7;
    public static String version;
    
    static {
        Main.permission = null;
        version = Bukkit.getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf(".") + 1);
    }

    public static boolean isNewVersion = version.substring(3).compareTo("13") > 0 && !version.startsWith("v1_8_") && !version.startsWith("v1_9_");
    
    public Main() {
        this.prefix = "§8§l[§c§lOPS§f§lRegion§8§l]";
    }
    
    private boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> permissionProvider = (RegisteredServiceProvider<Permission>)this.getServer().getServicesManager().getRegistration((Class)Permission.class);
        if (permissionProvider != null) {
            Main.permission = permissionProvider.getProvider();
        }
        return Main.permission != null;
    }
    
    public void onEnable() {
        this.functions = new Functions(this);
        this.WorldGuard6 = new WG6(this);
        this.WorldGuard7 = new WG7(this);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        Set<String> rgset = getConfig().getConfigurationSection("regions").getKeys(false);
        if (rgset.size() == 0)
            protectedRegions = new ArrayList<>();
        else
            this.protectedRegions = new ArrayList<>(getConfig().getConfigurationSection("regions").getKeys(false));
        this.protectedMines = (ArrayList<String>)this.getConfig().getStringList("regionsmine");
        final PluginManager manager = this.getServer().getPluginManager();
        this.setupPermissions();
        GUI gui = new GUI(this);
        manager.registerEvents(new Handler(this, this.functions, this.WorldGuard6, this.WorldGuard7), this);
        getCommand("opsrg").setExecutor(new CommandExecutor(this));
    }
    

}
