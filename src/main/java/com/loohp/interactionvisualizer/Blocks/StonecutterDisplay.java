package com.loohp.interactionvisualizer.Blocks;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecutterInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.loohp.interactionvisualizer.InteractionVisualizer;
import com.loohp.interactionvisualizer.Holder.Item;
import com.loohp.interactionvisualizer.Manager.PacketManager;
import com.loohp.interactionvisualizer.Utils.InventoryUtils;
import com.loohp.interactionvisualizer.Utils.VanishUtils;

public class StonecutterDisplay implements Listener {
	
	public static HashMap<Block, HashMap<String, Object>> openedStonecutter = new HashMap<Block, HashMap<String, Object>>();
	public static HashMap<Player, Block> playermap = new HashMap<Player, Block>();
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onStonecutter(InventoryClickEvent event) {
		if (VanishUtils.isVanished((Player) event.getWhoClicked())) {
			return;
		}
		if (event.isCancelled()) {
			return;
		}
		if (event.getRawSlot() != 1) {
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
			if (!InventoryUtils.stillHaveSpace(event.getWhoClicked().getInventory(), event.getView().getItem(1).getType())) {
				return;
			}
		}
		
		if (!playermap.containsKey((Player) event.getWhoClicked())) {
			return;
		}
		
		Block block = playermap.get((Player) event.getWhoClicked());
		
		if (!openedStonecutter.containsKey(block)) {
			return;
		}
		
		HashMap<String, Object> map = openedStonecutter.get(block);
		if (!map.get("Player").equals((Player) event.getWhoClicked())) {
			return;
		}
		
		ItemStack itemstack = event.getCurrentItem();
		Location loc = block.getLocation();
		
		Player player = (Player) event.getWhoClicked();
		if (map.get("Item") instanceof String) {
			map.put("Item", new Item(block.getLocation().clone().add(0.5, 1.2, 0.5)));
		}
		Item item = (Item) map.get("Item");
		
		openedStonecutter.remove(block);
		
		item.setItemStack(itemstack);
		item.setLocked(true);
		
		Vector lift = new Vector(0.0, 0.15, 0.0);
		Vector pickup = player.getEyeLocation().add(0.0, -0.5, 0.0).toVector().subtract(loc.clone().add(0.5, 1.2, 0.5).toVector()).multiply(0.15).add(lift);
		item.setVelocity(pickup);
		item.setGravity(true);
		item.setPickupDelay(32767);
		PacketManager.updateItem(InteractionVisualizer.getOnlinePlayers(), item);
		
		new BukkitRunnable() {
			public void run() {
				PacketManager.removeItem(InteractionVisualizer.getOnlinePlayers(), item);
			}
		}.runTaskLater(InteractionVisualizer.plugin, 8);
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onUseStonecutter(InventoryClickEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!playermap.containsKey((Player) event.getWhoClicked())) {
			return;
		}
		
		if (event.getRawSlot() >= 0 && event.getRawSlot() <= 1) {
			PacketManager.sendHandMovement(InteractionVisualizer.getOnlinePlayers(), (Player) event.getWhoClicked());
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onDragStonecutter(InventoryDragEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!playermap.containsKey((Player) event.getWhoClicked())) {
			return;
		}
		
		for (int slot : event.getRawSlots()) {
			if (slot >= 0 && slot <= 1) {
				PacketManager.sendHandMovement(InteractionVisualizer.getOnlinePlayers(), (Player) event.getWhoClicked());
				break;
			}
		}
	}
	
	@EventHandler
	public void onCloseStonecutter(InventoryCloseEvent event) {
		if (!playermap.containsKey((Player) event.getPlayer())) {
			return;
		}
		
		Block block = playermap.get((Player) event.getPlayer());
		
		if (!openedStonecutter.containsKey(block)) {
			return;
		}
		
		HashMap<String, Object> map = openedStonecutter.get(block);
		if (!map.get("Player").equals((Player) event.getPlayer())) {
			return;
		}
		
		if (map.get("Item") instanceof Item) {
			Item entity = (Item) map.get("Item");
			PacketManager.removeItem(InteractionVisualizer.getOnlinePlayers(), entity);
			entity.remove();
		}
		openedStonecutter.remove(block);
		playermap.remove((Player) event.getPlayer());
	}
	
	public static int run() {		
		return new BukkitRunnable() {
			public void run() {
				
				Iterator<Block> itr = openedStonecutter.keySet().iterator();
				int count = 0;
				int maxper = (int) Math.ceil((double) openedStonecutter.size() / (double) 5);
				int delay = 1;
				while (itr.hasNext()) {
					count++;
					if (count > maxper) {
						count = 0;
						delay++;
					}
					Block block = itr.next();					
					new BukkitRunnable() {
						public void run() {
							if (!openedStonecutter.containsKey(block)) {
								return;
							}
							HashMap<String, Object> map = openedStonecutter.get(block);
							if (block.getType().equals(Material.STONECUTTER)) {
								Player player = (Player) map.get("Player");
								if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
									if (player.getOpenInventory() != null) {
										if (player.getOpenInventory().getTopInventory() != null) {
											if (player.getOpenInventory().getTopInventory() instanceof StonecutterInventory) {
												return;
											}
										}
									}
								}
							}
							
							if (map.get("Item") instanceof Item) {
								Item entity = (Item) map.get("Item");
								PacketManager.removeItem(InteractionVisualizer.getOnlinePlayers(), (Item) entity);
							}
							openedStonecutter.remove(block);
						}
					}.runTaskLater(InteractionVisualizer.plugin, delay);
				}				
			}
		}.runTaskTimer(InteractionVisualizer.plugin, 0, 5).getTaskId();
	}
	
	public static void process(Player player) {
		if (VanishUtils.isVanished(player)) {
			return;
		}
		if (!playermap.containsKey(player)) {
			if (player.getGameMode().equals(GameMode.SPECTATOR)) {
				return;
			}
			if (!(player.getOpenInventory().getTopInventory() instanceof StonecutterInventory)) {
				return;
			}
			if (!player.getTargetBlockExact(7, FluidCollisionMode.NEVER).getType().equals(Material.STONECUTTER)) {
				return;
			}
			
			Block block = player.getTargetBlockExact(7, FluidCollisionMode.NEVER);
			
			playermap.put(player, block);
		}
		
		InventoryView view = player.getOpenInventory();
		Block block = playermap.get(player);
		Location loc = block.getLocation();
		if (!openedStonecutter.containsKey(block)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Player", player);
			map.put("Item", "N/A");
			openedStonecutter.put(block, map);
		}
		HashMap<String, Object> map = openedStonecutter.get(block);
		
		if (!map.get("Player").equals(player)) {
			return;
		}
		
		ItemStack input = view.getItem(0);
		if (input != null) {
			if (input.getType().equals(Material.AIR)) {
				input = null;
			}
		}
		ItemStack output = view.getItem(1);
		if (output != null) {
			if (output.getType().equals(Material.AIR)) {
				output = null;
			}
		}
		
		ItemStack itemstack = null;
		if (output == null) {
			if (input != null) {
				itemstack = input;
			}
		} else {
			itemstack = output;
		}
		
		if (itemstack != null) {
			ItemStack itempar = itemstack.clone();
			int taskid = new BukkitRunnable() {
				public void run() {
					player.getWorld().spawnParticle(Particle.ITEM_CRACK, loc.clone().add(0.5, 0.7, 0.5), 25, 0.1, 0.1, 0.1, 0.1, itempar);
				}
			}.runTaskTimer(InteractionVisualizer.plugin, 0, 1).getTaskId();
			new BukkitRunnable() {
				public void run() {
					Bukkit.getScheduler().cancelTask(taskid);
				}
			}.runTaskLater(InteractionVisualizer.plugin, 4);
		}
		
		Item item = null;
		if (map.get("Item") instanceof String) {
			if (itemstack != null) {
				item = new Item(loc.clone().add(0.5, 0.75, 0.5));
				item.setItemStack(itemstack);
				item.setVelocity(new Vector(0, 0, 0));
				item.setPickupDelay(32767);
				item.setGravity(false);
				map.put("Item", item);
				PacketManager.sendItemSpawn(InteractionVisualizer.itemDrop, item);
				PacketManager.updateItem(InteractionVisualizer.getOnlinePlayers(), item);
			} else {
				map.put("Item", "N/A");
			}
		} else {
			item = (Item) map.get("Item");
			if (itemstack != null) {
				if (!item.getItemStack().equals(itemstack)) {
					item.setItemStack(itemstack);
					PacketManager.updateItem(InteractionVisualizer.getOnlinePlayers(), item);
				}
				item.setPickupDelay(32767);
				item.setGravity(false);
			} else {
				map.put("Item", "N/A");
				PacketManager.removeItem(InteractionVisualizer.getOnlinePlayers(), item);
				item.remove();
			}
		}
	}
}
