package com.loohp.interactionvisualizer.Entities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.loohp.interactionvisualizer.InteractionVisualizer;
import com.loohp.interactionvisualizer.Holder.Item;
import com.loohp.interactionvisualizer.Manager.PacketManager;
import com.loohp.interactionvisualizer.Utils.InventoryUtils;
import com.loohp.interactionvisualizer.Utils.VanishUtils;

public class VillagerDisplay implements Listener {
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onVillageTrade(InventoryClickEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (VanishUtils.isVanished((Player) event.getWhoClicked())) {
			return;
		}
		if (!(event.getView().getTopInventory() instanceof MerchantInventory)) {
			return;
		}
		if (event.getRawSlot() != 2) {
			return;
		}
		if (event.getCurrentItem() == null) {
			return;
		}
		if (event.getCurrentItem().getType().equals(Material.AIR)) {
			return;
		}
		if (event.getCursor() != null) {
			if (!event.getCursor().getType().equals(Material.AIR)) {
				if (event.getCursor().getAmount() >= event.getCursor().getType().getMaxStackSize()) {
					return;
				}
			}
		}
		if (event.isShiftClick()) {
			if (!InventoryUtils.stillHaveSpace(event.getWhoClicked().getInventory(), event.getView().getItem(2).getType())) {
				return;
			}
		}
		
		ItemStack item0 = null;
		if (event.getView().getItem(0) != null) {
			if (!event.getView().getItem(0).getType().equals(Material.AIR)) {
				item0 = event.getView().getItem(0).clone();
			}
		}
		ItemStack item1 = null;
		if (event.getView().getItem(1) != null) {
			if (!event.getView().getItem(1).getType().equals(Material.AIR)) {
				item1 = event.getView().getItem(1).clone();
			}
		}
		ItemStack item2 = event.getCurrentItem().clone();
		Player player = (Player) event.getWhoClicked();
		MerchantInventory tradeinv = (MerchantInventory) event.getView().getTopInventory();
		if (!(tradeinv.getHolder() instanceof Villager)) {
			return;
		}
		Villager villager = (Villager) tradeinv.getHolder();
		Vector lift = new Vector(0.0, 0.20, 0.0);
		if (item0 != null) {
			Item in = new Item(player.getEyeLocation());
			Vector vector = villager.getEyeLocation().add(0.5, -0.5, 0.5).toVector().subtract(player.getEyeLocation().toVector()).multiply(0.12).add(lift);
			in.setItemStack(item0);
			in.setGravity(true);
			in.setVelocity(vector);
			PacketManager.sendItemSpawn(InteractionVisualizer.itemDrop, in);
			PacketManager.updateItem(InteractionVisualizer.getOnlinePlayers(), in);
			
			Bukkit.getScheduler().runTaskLater(InteractionVisualizer.plugin, () -> PacketManager.removeItem(InteractionVisualizer.getOnlinePlayers(), in), 14);
		}
		
		ItemStack item1final = item1;
		new BukkitRunnable() {
			public void run() {
				if (item1final != null) {
					Item in = new Item(player.getEyeLocation());
					Vector vector = villager.getEyeLocation().add(0.5, -0.5, 0.5).toVector().subtract(player.getEyeLocation().toVector()).multiply(0.12).add(lift);
					in.setItemStack(item1final);
					in.setGravity(true);
					in.setVelocity(vector);
					PacketManager.sendItemSpawn(InteractionVisualizer.itemDrop, in);
					PacketManager.updateItem(InteractionVisualizer.getOnlinePlayers(), in);
					
					Bukkit.getScheduler().runTaskLater(InteractionVisualizer.plugin, () -> PacketManager.removeItem(InteractionVisualizer.getOnlinePlayers(), in), 14);
				}
			}
		}.runTaskLater(InteractionVisualizer.plugin, 8);
				
		new BukkitRunnable() {
			public void run() {
				Item out = new Item(villager.getEyeLocation());
				Vector vector = player.getEyeLocation().add(0.5, -0.5, 0.5).toVector().subtract(villager.getEyeLocation().toVector()).multiply(0.10).add(lift);
				out.setItemStack(item2);
				out.setGravity(true);
				out.setVelocity(vector);
				PacketManager.sendItemSpawn(InteractionVisualizer.itemDrop, out);
				PacketManager.updateItem(InteractionVisualizer.getOnlinePlayers(), out);
				
				Bukkit.getScheduler().runTaskLater(InteractionVisualizer.plugin, () -> PacketManager.removeItem(InteractionVisualizer.getOnlinePlayers(), out), 12);
			}
		}.runTaskLater(InteractionVisualizer.plugin, 40);
	}

}
