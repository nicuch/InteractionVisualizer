package com.loohp.interactionvisualizer.Metrics;

import com.loohp.interactionvisualizer.InteractionVisualizer;
import org.bukkit.configuration.file.FileConfiguration;

public class Charts {

    public static FileConfiguration config = InteractionVisualizer.config;

    public static void registerCharts(Metrics metrics) {

        metrics.addCustomChart(new Metrics.SimplePie("anvil_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.Anvil.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("beacon_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.Beacon.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("blastfurnace_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.BlastFurnace.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("brewingstand_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.BrewingStand.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("cartographytable_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.CartographyTable.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("chest_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.Chest.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("craftingtable_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.CraftingTable.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("doublechest_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.DoubleChest.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("enchantmenttable_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.EnchantmentTable.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("enderchest_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.EnderChest.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("furnace_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.Furnace.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("grindstone_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.Grindstone.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("loom_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.Loom.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("smoker_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.Smoker.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("stonecutter_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.Stonecutter.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("noteblock_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.NoteBlock.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("jukebox_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Blocks.JukeBox.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

        metrics.addCustomChart(new Metrics.SimplePie("villager_enabled", () -> {
            String string = "Disabled";
            if (config.getBoolean("Entities.Villager.Enabled")) {
                string = "Enabled";
            }
            return string;
        }));

    }

}
