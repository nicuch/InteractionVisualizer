package com.loohp.interactionvisualizer.Blocks;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.CartographyInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.loohp.interactionvisualizer.InteractionVisualizer;
import com.loohp.interactionvisualizer.Holder.ItemFrame;
import com.loohp.interactionvisualizer.Manager.PacketManager;
import com.loohp.interactionvisualizer.Utils.VanishUtils;

public class CartographyTableDisplay implements Listener {
	
	public static HashMap<Block, HashMap<String, Object>> openedCTable = new HashMap<Block, HashMap<String, Object>>();
	public static HashMap<Player, Block> playermap = new HashMap<Player, Block>();

	@EventHandler(priority=EventPriority.MONITOR)
	public void onUseCartographyTable(InventoryClickEvent event) {
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
	public void onDragCartographyTable(InventoryDragEvent event) {
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
	public void onCloseCartographyTable(InventoryCloseEvent event) {
		if (!playermap.containsKey((Player) event.getPlayer())) {
			return;
		}
		
		Block block = playermap.get((Player) event.getPlayer());
		
		if (!openedCTable.containsKey(block)) {
			return;
		}
		
		HashMap<String, Object> map = openedCTable.get(block);
		if (!map.get("Player").equals((Player) event.getPlayer())) {
			return;
		}
		
		if (map.get("Item") instanceof ItemFrame) {
			ItemFrame entity = (ItemFrame) map.get("Item");
			PacketManager.removeItemFrame(InteractionVisualizer.getOnlinePlayers(), entity);
			entity.remove();
		}
		openedCTable.remove(block);
		playermap.remove((Player) event.getPlayer());
	}
	
	public static int run() {		
		return new BukkitRunnable() {
			public void run() {
				
				Iterator<Block> itr = openedCTable.keySet().iterator();
				int count = 0;
				int maxper = (int) Math.ceil((double) openedCTable.size() / (double) 5);
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
							if (!openedCTable.containsKey(block)) {
								return;
							}
							HashMap<String, Object> map = openedCTable.get(block);
							if (block.getType().equals(Material.CARTOGRAPHY_TABLE)) {
								Player player = (Player) map.get("Player");
								if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
									if (player.getOpenInventory() != null) {
										if (player.getOpenInventory().getTopInventory() != null) {
											if (player.getOpenInventory().getTopInventory() instanceof CartographyInventory) {
												return;
											}
										}
									}
								}
							}
							
							if (map.get("Item") instanceof ItemFrame) {
								Entity entity = (Entity) map.get("Item");
								PacketManager.removeItemFrame(InteractionVisualizer.getOnlinePlayers(), (ItemFrame) entity);
							}
							openedCTable.remove(block);
						}
					}.runTaskLater(InteractionVisualizer.plugin, delay);
				}								
			}
		}.runTaskTimer(InteractionVisualizer.plugin, 0, 6).getTaskId();
	}
	
	public static void process(Player player) {
		if (VanishUtils.isVanished(player)) {
			return;
		}
		if (!playermap.containsKey(player)) {
			if (player.getGameMode().equals(GameMode.SPECTATOR)) {
				return;
			}
			if (!(player.getOpenInventory().getTopInventory() instanceof CartographyInventory)) {
				return;
			}
			if (!player.getTargetBlockExact(7, FluidCollisionMode.NEVER).getType().equals(Material.CARTOGRAPHY_TABLE)) {
				return;
			}
			
			Block block = player.getTargetBlockExact(7, FluidCollisionMode.NEVER);
			playermap.put(player, block);
		}
		
		InventoryView view = player.getOpenInventory();
		Block block = playermap.get(player);
		
		if (!openedCTable.containsKey(block)) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Player", player);
			map.put("Item", "N/A");
			openedCTable.put(block, map);
		}
		HashMap<String, Object> map = openedCTable.get(block);
		
		if (!map.get("Player").equals(player)) {
			return;
		}
	
		ItemStack input = view.getItem(0);
		if (input != null) {
			if (input.getType().equals(Material.AIR)) {
				input = null;
			}
		}
		ItemStack output = view.getItem(2);
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
			
		ItemFrame item = null;
		if (!block.getRelative(BlockFace.UP).getType().isSolid()) {
			if (map.get("Item") instanceof String) {
				if (itemstack != null) {
					item = new ItemFrame(block.getRelative(BlockFace.UP).getLocation());
					item.setItem(itemstack);
					item.setFacingDirection(BlockFace.UP);
					map.put("Item", item);
					PacketManager.sendItemFrameSpawn(InteractionVisualizer.itemStand, item);
					PacketManager.updateItemFrame(InteractionVisualizer.getOnlinePlayers(), item);
				} else {
					map.put("Item", "N/A");
				}
			} else {
				item = (ItemFrame) map.get("Item");
				if (itemstack != null) {
					if (!item.getItem().equals(itemstack)) {
						item.setItem(itemstack);
						PacketManager.updateItemFrame(InteractionVisualizer.getOnlinePlayers(), item);
					}
				} else {
					map.put("Item", "N/A");
					PacketManager.removeItemFrame(InteractionVisualizer.getOnlinePlayers(), item);
					item.remove();
				}
			}
		}
	}
}
