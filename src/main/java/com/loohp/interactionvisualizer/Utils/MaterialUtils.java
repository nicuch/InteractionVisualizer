package com.loohp.interactionvisualizer.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;

import com.loohp.interactionvisualizer.InteractionVisualizer;

public class MaterialUtils {

    private static final String version = InteractionVisualizer.version;
    private static final List<Material> tools = new ArrayList<>();
    private static final List<Material> nonSolid = new ArrayList<>();

    public static void setup() {
        if (!version.contains("legacy")) {
            tools.add(Material.valueOf("WOODEN_AXE"));
            tools.add(Material.valueOf("WOODEN_HOE"));
            tools.add(Material.valueOf("WOODEN_PICKAXE"));
            tools.add(Material.valueOf("WOODEN_SHOVEL"));
            tools.add(Material.valueOf("WOODEN_SWORD"));
            tools.add(Material.valueOf("STONE_AXE"));
            tools.add(Material.valueOf("STONE_HOE"));
            tools.add(Material.valueOf("STONE_PICKAXE"));
            tools.add(Material.valueOf("STONE_SHOVEL"));
            tools.add(Material.valueOf("STONE_SWORD"));
            tools.add(Material.valueOf("IRON_AXE"));
            tools.add(Material.valueOf("IRON_HOE"));
            tools.add(Material.valueOf("IRON_PICKAXE"));
            tools.add(Material.valueOf("IRON_SHOVEL"));
            tools.add(Material.valueOf("IRON_SWORD"));
            tools.add(Material.valueOf("GOLDEN_AXE"));
            tools.add(Material.valueOf("GOLDEN_HOE"));
            tools.add(Material.valueOf("GOLDEN_PICKAXE"));
            tools.add(Material.valueOf("GOLDEN_SHOVEL"));
            tools.add(Material.valueOf("GOLDEN_SWORD"));
            tools.add(Material.valueOf("DIAMOND_AXE"));
            tools.add(Material.valueOf("DIAMOND_HOE"));
            tools.add(Material.valueOf("DIAMOND_PICKAXE"));
            tools.add(Material.valueOf("DIAMOND_SHOVEL"));
            tools.add(Material.valueOf("DIAMOND_SWORD"));
            tools.add(Material.valueOf("BOW"));
            tools.add(Material.valueOf("FISHING_ROD"));
            tools.add(Material.valueOf("STICK"));
            tools.add(Material.valueOf("BLAZE_ROD"));
        } else {
            tools.add(Material.valueOf("WOOD_AXE"));
            tools.add(Material.valueOf("WOOD_HOE"));
            tools.add(Material.valueOf("WOOD_PICKAXE"));
            tools.add(Material.valueOf("WOOD_SPADE"));
            tools.add(Material.valueOf("WOOD_SWORD"));
            tools.add(Material.valueOf("STONE_AXE"));
            tools.add(Material.valueOf("STONE_HOE"));
            tools.add(Material.valueOf("STONE_PICKAXE"));
            tools.add(Material.valueOf("STONE_SPADE"));
            tools.add(Material.valueOf("STONE_SWORD"));
            tools.add(Material.valueOf("IRON_AXE"));
            tools.add(Material.valueOf("IRON_HOE"));
            tools.add(Material.valueOf("IRON_PICKAXE"));
            tools.add(Material.valueOf("IRON_SPADE"));
            tools.add(Material.valueOf("IRON_SWORD"));
            tools.add(Material.valueOf("GOLD_AXE"));
            tools.add(Material.valueOf("GOLD_HOE"));
            tools.add(Material.valueOf("GOLD_PICKAXE"));
            tools.add(Material.valueOf("GOLD_SPADE"));
            tools.add(Material.valueOf("GOLD_SWORD"));
            tools.add(Material.valueOf("DIAMOND_AXE"));
            tools.add(Material.valueOf("DIAMOND_HOE"));
            tools.add(Material.valueOf("DIAMOND_PICKAXE"));
            tools.add(Material.valueOf("DIAMOND_SPADE"));
            tools.add(Material.valueOf("DIAMOND_SWORD"));
            tools.add(Material.valueOf("BOW"));
            tools.add(Material.valueOf("FISHING_ROD"));
            tools.add(Material.valueOf("STICK"));
            tools.add(Material.valueOf("BLAZE_ROD"));
        }

        for (Material material : Material.values()) {
            if (!material.isBlock()) {
                continue;
            }
            if (!material.isSolid()) {
                nonSolid.add(material);
            }
        }
    }

    public static boolean isTool(Material material) {
        return tools.contains(material);
    }

    public static List<Material> getNonSolidList() {
        return nonSolid;
    }

    public static Set<Material> getNonSolidSet() {
        return convertListToSet(nonSolid);
    }

    public static <T> Set<T> convertListToSet(List<T> list) {
        return new HashSet<>(list); // Hashset!
    }

}
