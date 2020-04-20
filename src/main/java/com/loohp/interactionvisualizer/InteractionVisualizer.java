package com.loohp.interactionvisualizer;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.loohp.interactionvisualizer.Database.Database;
import com.loohp.interactionvisualizer.Manager.*;
import com.loohp.interactionvisualizer.Metrics.Charts;
import com.loohp.interactionvisualizer.Metrics.Metrics;
import com.loohp.interactionvisualizer.PlaceholderAPI.PlaceholderAPI;
import com.loohp.interactionvisualizer.Updater.Updater;
import com.loohp.interactionvisualizer.Utils.EntityCreator;
import com.loohp.interactionvisualizer.Utils.LegacyRecordsUtils;
import com.loohp.interactionvisualizer.Utils.MaterialUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InteractionVisualizer extends JavaPlugin {

    public static Plugin plugin = null;
    public static ProtocolManager protocolManager;
    public static FileConfiguration config;

    public static String version = "";

    public static boolean openinv = false;
    public static boolean vanish = false;
    public static boolean cmi = false;
    public static boolean ess3 = false;

    // Don't use Player.class directly, use UUID.class, as its smaller that the player object
    public static List<UUID> itemStand = new ArrayList<>(); //Never to use CopyOnWriteArrayList on huge that if you don't know what it does, THE HELL???
    public static List<UUID> itemDrop = new ArrayList<>(); //Explanation, when you add a new value, the array inside the list is copied to a new array just to make space for it.
    public static List<UUID> holograms = new ArrayList<>(); //Same shit applies when you remove a value (PS: thats why its called "COPY ON WRITE")

    public static List<String> exemptBlocks = new ArrayList<>();

    public static World defaultworld;
    public static Location defaultlocation;

    public static boolean itemStandEnabled = true;
    public static boolean itemDropEnabled = true;
    public static boolean hologramsEnabled = true;

    public static boolean UpdaterEnabled = true;
    public static int UpdaterTaskID = -1;

    @Override
    public void onEnable() {
        plugin = getServer().getPluginManager().getPlugin("InteractionVisualizer");

        protocolManager = ProtocolLibrary.getProtocolManager();

        if (getServer().getPluginManager().getPlugin("OpenInv") != null) {
            hookMessage("OpenInv");
            openinv = true;
        }
        if (getServer().getPluginManager().getPlugin("SuperVanish") != null || getServer().getPluginManager().getPlugin("PremiumVanish") != null) {
            hookMessage("SuperVanish/PremiumVanish");
            vanish = true;
        }
        if (getServer().getPluginManager().getPlugin("CMI") != null) {
            hookMessage("CMI");
            cmi = true;
        }
        if (getServer().getPluginManager().getPlugin("Essentials") != null) {
            hookMessage("Essentials");
            ess3 = true;
        }
        LangManager.generate();
        EntityCreator.setup();

        int pluginId = 7024;

        Metrics metrics = new Metrics(this, pluginId);

        String packageName = getServer().getClass().getPackage().getName();

        if (packageName.contains("1_15_R1")) { // Don't check again if value is true or not
            version = "1.15";
        } else if (packageName.contains("1_14_R1")) {
            version = "1.14";
        } else if (packageName.contains("1_13_R2")) {
            version = "1.13.1";
        } else if (packageName.contains("1_13_R1")) {
            version = "1.13";
        } else if (packageName.contains("1_12_R1")) {
            version = "legacy1.12";
        } else if (packageName.contains("1_11_R1")) {
            version = "legacy1.11";
        } else if (packageName.contains("1_10_R1")) {
            version = "legacy1.10";
        } else if (packageName.contains("1_9_R2")) {
            version = "legacy1.9.4";
            unsupportedMessage();
        } else if (packageName.contains("1_9_R1")) {
            version = "legacy1.9";
            unsupportedMessage();
        } else if (packageName.contains("1_8_R3")) {
            version = "OLDlegacy1.8.4";
            unsupportedMessage();
        } else if (packageName.contains("1_8_R2")) {
            version = "OLDlegacy1.8.3";
            unsupportedMessage();
        } else if (packageName.contains("1_8_R1")) {
            version = "OLDlegacy1.8";
            unsupportedMessage();
        } else {
            unsupportedMessage();
        }

        plugin.getConfig().options().copyDefaults(true);
        config = plugin.getConfig();
        plugin.saveConfig();
        loadConfig();

        defaultworld = Bukkit.getWorlds().get(0);
        defaultlocation = new Location(defaultworld, 0, 0, 0);
        if (!version.contains("legacy")) {
            defaultworld.setChunkForceLoaded(0, 0, true);
        }

        EnchantmentManager.setup();
        EffectManager.setup();
        MusicManager.setup();
        Database.setup();
        CustomBlockDataManager.setup();
        TaskManager.run();
        TileEntityManager.run();
        PlayerRangeManager.run();
        CustomBlockDataManager.intervalSaveToFile();
        PacketManager.run();

        MaterialUtils.setup();

        if (version.contains("legacy")) {
            LegacyRecordsUtils.setup();
        }

        getCommand("interactionvisualizer").setExecutor(new Commands());

        TaskManager.setup();

        Charts.registerCharts(metrics);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI().register();
        }

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getNearbyEntities(new Location(world, 0, 0, 0), 2, 2, 2)) {
                if (entity.getScoreboardTags().contains("isInteractionVisualizer")) {
                    entity.remove();
                }
            }
        }

        exemptBlocks.add("CRAFTING_TABLE");
        exemptBlocks.add("WORKBENCH");
        exemptBlocks.add("LOOM");

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "InteractionVisualizer has been enabled!");
    }

    @Override
    public void onDisable() {
        CustomBlockDataManager.save();
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "InteractionVisualizer has been disabled!");
    }

    public static void loadConfig() {
        itemStandEnabled = config.getBoolean("Modules.ItemStand.Enabled");
        itemDropEnabled = config.getBoolean("Modules.ItemDrop.Enabled");
        hologramsEnabled = config.getBoolean("Modules.Hologram.Enabled");

        if (UpdaterTaskID >= 0) {
            Bukkit.getScheduler().cancelTask(UpdaterTaskID);
        }
        UpdaterEnabled = plugin.getConfig().getBoolean("Options.Updater");
        if (UpdaterEnabled) {
            Updater.updaterInterval();
        }
    }

    public static List<Player> getOnlinePlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    private static void unsupportedMessage() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "This version of minecraft is unsupported!");
    }

    private static void hookMessage(String pluginName) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[InteractionVisualizer] InteractionVisualizer has hooked into " + pluginName + "!");
    }
}
