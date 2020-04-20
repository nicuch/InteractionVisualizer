package com.loohp.interactionvisualizer.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.loohp.interactionvisualizer.InteractionVisualizer;
import com.loohp.interactionvisualizer.Blocks.AnvilDisplay;
import com.loohp.interactionvisualizer.Blocks.BeaconDisplay;
import com.loohp.interactionvisualizer.Blocks.BlastFurnaceDisplay;
import com.loohp.interactionvisualizer.Blocks.BrewingStandDisplay;
import com.loohp.interactionvisualizer.Blocks.CartographyTableDisplay;
import com.loohp.interactionvisualizer.Blocks.ChestDisplay;
import com.loohp.interactionvisualizer.Blocks.CraftingTableDisplay;
import com.loohp.interactionvisualizer.Blocks.DoubleChestDisplay;
import com.loohp.interactionvisualizer.Blocks.EnchantmentTableDisplay;
import com.loohp.interactionvisualizer.Blocks.EnderchestDisplay;
import com.loohp.interactionvisualizer.Blocks.FurnaceDisplay;
import com.loohp.interactionvisualizer.Blocks.GrindstoneDisplay;
import com.loohp.interactionvisualizer.Blocks.JukeBoxDisplay;
import com.loohp.interactionvisualizer.Blocks.LoomDisplay;
import com.loohp.interactionvisualizer.Blocks.NoteBlockDisplay;
import com.loohp.interactionvisualizer.Blocks.ShulkerBoxDisplay;
import com.loohp.interactionvisualizer.Blocks.SmokerDisplay;
import com.loohp.interactionvisualizer.Blocks.StonecutterDisplay;
import com.loohp.interactionvisualizer.Debug.Debug;
import com.loohp.interactionvisualizer.Entities.VillagerDisplay;
import com.loohp.interactionvisualizer.Listeners.ChunkEvents;

public class TaskManager {

    public static Plugin plugin = InteractionVisualizer.plugin;
    public static String version;
    public static FileConfiguration config = InteractionVisualizer.config;

    public static boolean anvil;
    public static boolean beacon;
    public static boolean blastfurnace;
    public static boolean brewingstand;
    public static boolean cartographytable;
    public static boolean chest;
    public static boolean craftingtable;
    public static boolean doublechest;
    public static boolean enchantmenttable;
    public static boolean enderchest;
    public static boolean furnace;
    public static boolean grindstone;
    public static boolean jukebox;
    public static boolean loom;
    public static boolean noteblock;
    public static boolean smoker;
    public static boolean stonecutter;
    public static boolean shulkerbox;

    public static boolean villager;

    public static List<Integer> tasks = new ArrayList<>();

    public static void setup() {
        anvil = false;
        beacon = false;
        blastfurnace = false;
        brewingstand = false;
        cartographytable = false;
        chest = false;
        craftingtable = false;
        doublechest = false;
        enchantmenttable = false;
        enderchest = false;
        furnace = false;
        grindstone = false;
        jukebox = false;
        loom = false;
        noteblock = false;
        smoker = false;
        stonecutter = false;
        shulkerbox = false;

        villager = false;

        version = InteractionVisualizer.version;

        HandlerList.unregisterAll(plugin);
        for (int taskid : tasks) {
            Bukkit.getScheduler().cancelTask(taskid);
        }
        tasks.clear();

        Bukkit.getPluginManager().registerEvents(new Debug(), plugin);
        Bukkit.getPluginManager().registerEvents(new com.loohp.interactionvisualizer.Listeners.Events(), plugin);
        Bukkit.getPluginManager().registerEvents(new PacketManager(), plugin);
        if (version.contains("legacy")) {
            ChunkEvents.setup();
            InteractionVisualizer.defaultworld.getChunkAt(0, 0).load();
            Bukkit.getPluginManager().registerEvents(new ChunkEvents(), plugin);
        }

        if (config.getBoolean("Blocks.CraftingTable.Enabled")) {
            Bukkit.getPluginManager().registerEvents(new CraftingTableDisplay(), plugin);
            CraftingTableDisplay.run();
            craftingtable = true;
        }

        if (config.getBoolean("Blocks.Loom.Enabled") &&
                (version.equals("1.14") || version.equals("1.15"))
        ) {
            Bukkit.getPluginManager().registerEvents(new LoomDisplay(), plugin);
            tasks.add(LoomDisplay.run());
            loom = true;
        }

        if (config.getBoolean("Blocks.EnchantmentTable.Enabled")) {
            Bukkit.getPluginManager().registerEvents(new EnchantmentTableDisplay(), plugin);
            enchantmenttable = true;
        }

        if (config.getBoolean("Blocks.CartographyTable.Enabled") &&
                (version.equals("1.14") || version.equals("1.15"))
        ) {
            Bukkit.getPluginManager().registerEvents(new CartographyTableDisplay(), plugin);
            tasks.add(CartographyTableDisplay.run());
            cartographytable = true;
        }

        if (config.getBoolean("Blocks.Anvil.Enabled")) {
            Bukkit.getPluginManager().registerEvents(new AnvilDisplay(), plugin);
            anvil = true;
        }

        if (config.getBoolean("Blocks.Grindstone.Enabled") &&
                (version.equals("1.14") || version.equals("1.15"))
        ) {
            Bukkit.getPluginManager().registerEvents(new GrindstoneDisplay(), plugin);
            grindstone = true;
        }

        if (config.getBoolean("Blocks.Stonecutter.Enabled") &&
                (version.equals("1.14") || version.equals("1.15"))
        ) {
            Bukkit.getPluginManager().registerEvents(new StonecutterDisplay(), plugin);
            tasks.add(StonecutterDisplay.run());
            stonecutter = true;
        }

        if (config.getBoolean("Blocks.BrewingStand.Enabled")) {
            Bukkit.getPluginManager().registerEvents(new BrewingStandDisplay(), plugin);
            tasks.add(BrewingStandDisplay.run());
            tasks.add(BrewingStandDisplay.gc());
            brewingstand = true;
        }

        if (config.getBoolean("Blocks.Chest.Enabled")) {
            Bukkit.getPluginManager().registerEvents(new ChestDisplay(), plugin);
            chest = true;
        }

        if (config.getBoolean("Blocks.DoubleChest.Enabled")) {
            Bukkit.getPluginManager().registerEvents(new DoubleChestDisplay(), plugin);
            doublechest = true;
        }

        if (config.getBoolean("Blocks.Furnace.Enabled")) {
            Bukkit.getPluginManager().registerEvents(new FurnaceDisplay(), plugin);
            tasks.add(FurnaceDisplay.run());
            tasks.add(FurnaceDisplay.gc());
            furnace = true;
        }

        if (config.getBoolean("Blocks.BlastFurnace.Enabled") &&
                (version.equals("1.14") || version.equals("1.15"))
        ) {
            Bukkit.getPluginManager().registerEvents(new BlastFurnaceDisplay(), plugin);
            tasks.add(BlastFurnaceDisplay.run());
            tasks.add(BlastFurnaceDisplay.gc());
            blastfurnace = true;
        }

        if (config.getBoolean("Blocks.Smoker.Enabled") &&
                (version.equals("1.14") || version.equals("1.15"))
        ) {
            Bukkit.getPluginManager().registerEvents(new SmokerDisplay(), plugin);
            tasks.add(SmokerDisplay.run());
            tasks.add(SmokerDisplay.gc());
            smoker = true;
        }

        if (config.getBoolean("Blocks.EnderChest.Enabled")) {
            Bukkit.getPluginManager().registerEvents(new EnderchestDisplay(), plugin);
            enderchest = true;
        }

        if (config.getBoolean("Blocks.ShulkerBox.Enabled") &&
                (!version.contains("OLD"))
        ) {
            Bukkit.getPluginManager().registerEvents(new ShulkerBoxDisplay(), plugin);
            shulkerbox = true;
        }

        if (config.getBoolean("Blocks.Beacon.Enabled")) {
            Bukkit.getPluginManager().registerEvents(new BeaconDisplay(), plugin);
            tasks.add(BeaconDisplay.run());
            tasks.add(BeaconDisplay.gc());
            beacon = true;
        }

        if (config.getBoolean("Blocks.NoteBlock.Enabled")) {
            Bukkit.getPluginManager().registerEvents(new NoteBlockDisplay(), plugin);
            tasks.add(NoteBlockDisplay.run());
            noteblock = true;
        }

        if (config.getBoolean("Blocks.JukeBox.Enabled")) {
            Bukkit.getPluginManager().registerEvents(new JukeBoxDisplay(), plugin);
            tasks.add(JukeBoxDisplay.run());
            jukebox = true;
        }

        if (config.getBoolean("Entities.Villager.Enabled")) {
            Bukkit.getPluginManager().registerEvents(new VillagerDisplay(), plugin);
            villager = true;
        }

        tasks.add(LightManager.run());
    }

    public static void run() {
        int next = 1;
        int count = 0;
        int size = Bukkit.getOnlinePlayers().size();
        int maxper = (int) Math.ceil((double) size / (double) 5);
        if (maxper > 10) {
            maxper = 10;
        }
        int delay = 1;
        for (Player eachPlayer : Bukkit.getOnlinePlayers()) {
            if (eachPlayer.getOpenInventory().getType() == InventoryType.CRAFTING || eachPlayer.getOpenInventory().getType() == InventoryType.CREATIVE) {
                continue;
            }
            count++;
            if (count > maxper) {
                count = 0;
                delay++;
            }
            UUID uuid = eachPlayer.getUniqueId();
            new BukkitRunnable() {
                public void run() {
                    if (Bukkit.getPlayer(uuid) == null) {
                        return;
                    }
                    Player player = Bukkit.getPlayer(uuid);
                    Inventory inv = player.getOpenInventory().getTopInventory();
                    switch (inv.getType()) {
                        case ANVIL:
                            if (anvil) {
                                AnvilDisplay.process(player);
                            }
                            return;

                        case CARTOGRAPHY:
                            if (cartographytable) {
                                CartographyTableDisplay.process(player);
                            }
                            return;
                        case ENCHANTING:
                            if (enchantmenttable) {
                                EnchantmentTableDisplay.process(player);
                            }
                            return;
                        case GRINDSTONE:
                            if (grindstone) {
                                GrindstoneDisplay.process(player);
                            }
                            return;
                        case LOOM:
                            if (loom) {
                                LoomDisplay.process(player);
                            }
                            return;
                        case STONECUTTER:
                            if (stonecutter) {
                                StonecutterDisplay.process(player);
                            }
                            return;
                        case WORKBENCH:
                            if (craftingtable) {
                                CraftingTableDisplay.process(player);
                            }
                            return;
                        case BARREL:
                        case BEACON:
                        case BREWING:
                        case BLAST_FURNACE:
                        case CHEST:
                        case CRAFTING:
                        case SMOKER:
                        case SHULKER_BOX:
                        case PLAYER:
                        case MERCHANT:
                        case LECTERN:
                        case HOPPER:
                        case FURNACE:
                        case DROPPER:
                        case DISPENSER:
                        case CREATIVE:
                        case ENDER_CHEST:
                        default:
                            //
                    }
                }
            }.runTaskLater(plugin, delay);
        }
        next = next + delay;
        Bukkit.getScheduler().runTaskLater(plugin, TaskManager::run, next);
    }

}
