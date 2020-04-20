package com.loohp.interactionvisualizer.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtils {

    public static boolean stillHaveSpace(Inventory inv, Material material) {
        int size = inv.getSize();
        if (inv instanceof PlayerInventory) {
            size = 36;
        }
        for (int i = 0; i < size; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) {
                return true;
            }
            if (item.getType() == Material.AIR) { //Enum types have the same memory location in Heap, so calling "==" might be faster that "equals()"
                return true;
            }
            if (item.getType() == material) {
                if (item.getAmount() < item.getType().getMaxStackSize()) {
                    return true;
                }
            }
        }
        return false;
    }

}
