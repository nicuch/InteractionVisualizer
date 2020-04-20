package com.loohp.interactionvisualizer.Manager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.loohp.interactionvisualizer.Holder.ArmorStand;
import com.loohp.interactionvisualizer.Holder.Item;
import com.loohp.interactionvisualizer.Holder.ItemFrame;
import com.loohp.interactionvisualizer.InteractionVisualizer;
import com.loohp.interactionvisualizer.godplskillme.EntityKeyHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class PacketManager implements Listener {

    private static final ProtocolManager protocolManager = InteractionVisualizer.protocolManager;
    private static final String version = InteractionVisualizer.version;
    private static final List<String> exemptBlocks = InteractionVisualizer.exemptBlocks;

    public static ConcurrentMap<EntityKeyHolder, List<Player>> active = new ConcurrentHashMap<>();
    public static ConcurrentMap<EntityKeyHolder, Boolean> loaded = new ConcurrentHashMap<>();

    public static void run() {
        if (!InteractionVisualizer.plugin.isEnabled()) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(InteractionVisualizer.plugin, () -> {
            for (Entry<EntityKeyHolder, Boolean> entry : loaded.entrySet()) {
                EntityKeyHolder entityKey = entry.getKey();
                if (entityKey.isArmorStand()) {
                    ArmorStand stand = entityKey.getArmorStand();
                    if (entry.getValue()) {
                        if (!InteractionVisualizer.plugin.isEnabled()) {
                            return;
                        }
                        Bukkit.getScheduler().runTask(InteractionVisualizer.plugin, () -> {
                            if (!PlayerRangeManager.hasPlayerNearby(stand.getLocation())) {
                                return;
                            }
                            List<Player> players = active.get(entityKey);
                            if (players == null) {
                                return;
                            }
                            if (isOccluding(stand.getLocation().getBlock().getType())) {
                                removeArmorStand(InteractionVisualizer.getOnlinePlayers(), stand, false);
                                loaded.put(entityKey, false);
                            }
                        });
                    } else {
                        if (!InteractionVisualizer.plugin.isEnabled()) {
                            return;
                        }
                        Bukkit.getScheduler().runTask(InteractionVisualizer.plugin, () -> {
                            if (!PlayerRangeManager.hasPlayerNearby(stand.getLocation())) {
                                return;
                            }
                            List<Player> players = active.get(entityKey);
                            if (players == null) {
                                return;
                            }
                            if (!isOccluding(stand.getLocation().getBlock().getType())) {
                                sendArmorStandSpawn(players, stand);
                                updateArmorStand(InteractionVisualizer.getOnlinePlayers(), stand);
                                loaded.put(entityKey, true);
                            }
                        });
                    }
                } else if (entityKey.isItem()) {
                    Item item = entityKey.getItem();
                    if (entry.getValue()) {
                        if (!InteractionVisualizer.plugin.isEnabled()) {
                            return;
                        }
                        Bukkit.getScheduler().runTask(InteractionVisualizer.plugin, () -> {
                            if (!PlayerRangeManager.hasPlayerNearby(item.getLocation())) {
                                return;
                            }
                            List<Player> players = active.get(entityKey);
                            if (players == null) {
                                return;
                            }
                            if (isOccluding(item.getLocation().getBlock().getType())) {
                                removeItem(InteractionVisualizer.getOnlinePlayers(), item, false);
                                loaded.put(entityKey, false);
                            }
                        });
                    } else {
                        if (!InteractionVisualizer.plugin.isEnabled()) {
                            return;
                        }
                        Bukkit.getScheduler().runTask(InteractionVisualizer.plugin, () -> {
                            if (!PlayerRangeManager.hasPlayerNearby(item.getLocation())) {
                                return;
                            }
                            List<Player> players = active.get(entityKey);
                            if (players == null) {
                                return;
                            }
                            if (!isOccluding(item.getLocation().getBlock().getType())) {
                                sendItemSpawn(players, item);
                                updateItem(InteractionVisualizer.getOnlinePlayers(), item);
                                loaded.put(entityKey, true);
                            }
                        });
                    }
                } else if (entityKey.isItemFrame()) {
                    ItemFrame frame = entityKey.getItemFrame();
                    if (entry.getValue()) {
                        if (!InteractionVisualizer.plugin.isEnabled()) {
                            return;
                        }
                        Bukkit.getScheduler().runTask(InteractionVisualizer.plugin, () -> {
                            if (!PlayerRangeManager.hasPlayerNearby(frame.getLocation())) {
                                return;
                            }
                            List<Player> players = active.get(entityKey);
                            if (players == null) {
                                return;
                            }
                            if (isOccluding(frame.getLocation().getBlock().getType())) {
                                removeItemFrame(InteractionVisualizer.getOnlinePlayers(), frame, false);
                                loaded.put(entityKey, false);
                            }
                        });
                    } else {
                        if (!InteractionVisualizer.plugin.isEnabled()) {
                            return;
                        }
                        Bukkit.getScheduler().runTask(InteractionVisualizer.plugin, () -> {
                            if (!PlayerRangeManager.hasPlayerNearby(frame.getLocation())) {
                                return;
                            }
                            List<Player> players = active.get(entityKey);
                            if (players == null) {
                                return;
                            }
                            if (!isOccluding(frame.getLocation().getBlock().getType())) {
                                sendItemFrameSpawn(players, frame);
                                updateItemFrame(InteractionVisualizer.getOnlinePlayers(), frame);
                                loaded.put(entityKey, true);
                            }
                        });
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (InteractionVisualizer.plugin.isEnabled()) {
                Bukkit.getScheduler().runTaskLater(InteractionVisualizer.plugin, PacketManager::run, 1);
            }
        });
    }

    private static boolean isOccluding(Material material) {
        if (exemptBlocks.contains(material.toString().toUpperCase())) {
            return false;
        }
        return material.isOccluding();
    }
	
	/*
	public static void sendLightUpdate(List<Player> players, Location location, int lightLevel, int subchunkbitmask, List<byte[]> bytearray) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.LIGHT_UPDATE);
		int chunkX = (int) Math.floor((double) location.getBlockX() / 16.0);
		int chunkZ = (int) Math.floor((double) location.getBlockZ() / 16.0);
		
		packet.getIntegers().write(0, chunkX);
		packet.getIntegers().write(1, chunkZ);
		packet.getIntegers().write(2, 0);
		packet.getIntegers().write(3, subchunkbitmask);
		packet.getIntegers().write(4, 0);
		packet.getIntegers().write(5, ~subchunkbitmask);
		packet.getModifier().write(6, new ArrayList<byte[]>());
		packet.getModifier().write(7, bytearray);
		
		try {
        	for (Player player : players) {
				protocolManager.sendServerPacket(player, packet);
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	*/

    public static void sendHandMovement(List<Player> players, Player entity) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ANIMATION);
        packet.getModifier().writeDefaults();
        packet.getIntegers().write(0, entity.getEntityId());
        packet.getIntegers().write(1, 0);
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void sendArmorStandSpawn(List<Player> players, ArmorStand entity) {
        EntityKeyHolder entityKey = new EntityKeyHolder(entity); //short lived object if active contains key
        if (!active.containsKey(entityKey)) {
            active.put(entityKey, players);
            loaded.put(entityKey, true);
        }

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);

        packet.getIntegers().write(0, entity.getEntityId());
        if (!version.contains("legacy")) {
            packet.getIntegers().write(1, 1);
        } else {
            packet.getIntegers().write(1, 30);
        }
        packet.getIntegers().write(2, 0);
        packet.getIntegers().write(3, 0);
        packet.getIntegers().write(4, 0);

        packet.getDoubles().write(0, entity.getLocation().getX());
        packet.getDoubles().write(1, entity.getLocation().getY());
        packet.getDoubles().write(2, entity.getLocation().getZ());

        packet.getBytes().write(0, (byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F)); //Yaw
        packet.getBytes().write(1, (byte) (int) (entity.getLocation().getPitch() * 256.0F / 360.0F)); //Pitch
        packet.getBytes().write(2, (byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F)); //Head

        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
        packet.getIntegers()
                //Entity ID
                .write(0, entity.getEntityId())
                //Velocity x
                .write(1, (int) (entity.getVelocity().getX() * 8000))
                //Velocity y
                .write(2, (int) (entity.getVelocity().getY() * 8000))
                //Velocity z
                .write(3, (int) (entity.getVelocity().getZ() * 8000));
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        //Entity ID
        packet.getIntegers().write(0, entity.getEntityId());

        WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
        packet.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void updateArmorStand(List<Player> players, ArmorStand entity) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        //Entity ID
        packet.getIntegers().write(0, entity.getEntityId());

        WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
        packet.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packet.getIntegers().write(0, entity.getEntityId());
        packet.getItemSlots().write(0, ItemSlot.MAINHAND);
        packet.getItemModifier().write(0, entity.getItemInMainHand());
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        packet.getIntegers().write(0, entity.getEntityId());
        packet.getItemSlots().write(0, ItemSlot.HEAD);
        packet.getItemModifier().write(0, entity.getHelmet());
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entity.getEntityId());
        packet.getDoubles().write(0, entity.getLocation().getX());
        packet.getDoubles().write(1, entity.getLocation().getY());
        packet.getDoubles().write(2, entity.getLocation().getZ());
        packet.getBytes().write(0, (byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F));
        packet.getBytes().write(1, (byte) (int) (entity.getLocation().getPitch() * 256.0F / 360.0F));
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
        packet.getIntegers()
                //Entity ID
                .write(0, entity.getEntityId())
                //Velocity x
                .write(1, (int) (entity.getVelocity().getX() * 8000))
                //Velocity y
                .write(2, (int) (entity.getVelocity().getY() * 8000))
                //Velocity z
                .write(3, (int) (entity.getVelocity().getZ() * 8000));
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void removeArmorStand(List<Player> players, ArmorStand entity, boolean removeFromActive) {
        EntityKeyHolder entityKey = new EntityKeyHolder(entity); //short lived object as key
        if (removeFromActive) {
            active.remove(entityKey);
            loaded.remove(entityKey);
            //from loaded maybe? as bellow?
        }
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntegerArrays().write(0, new int[]{entity.getEntityId()});
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void removeArmorStand(List<Player> players, ArmorStand entity) {
        removeArmorStand(players, entity, true);
    }

    public static void sendItemSpawn(List<Player> players, Item entity) {
        EntityKeyHolder entityKey = new EntityKeyHolder(entity); //short lived object as key unless active contains key
        if (!active.containsKey(entityKey)) {
            active.put(entityKey, players);
            loaded.put(entityKey, true);
        }

        if (entity.getItemStack().getType().equals(Material.AIR)) {
            return;
        }
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);

        //Location
        Location location = entity.getLocation();

        //and add data based on packet class in NMS  (global scope variable)
        //Reference: https://wiki.vg/Protocol#Spawn_Object
        packet.getIntegers()
                //Entity ID
                .write(0, entity.getEntityId())
                //Velocity x
                .write(1, (int) (entity.getVelocity().getX() * 8000))
                //Velocity y
                .write(2, (int) (entity.getVelocity().getY() * 8000))
                //Velocity z
                .write(3, (int) (entity.getVelocity().getZ() * 8000))
                //Pitch
                .write(4, (int) (entity.getLocation().getPitch() * 256.0F / 360.0F))
                //Yaw
                .write(5, (int) (entity.getLocation().getYaw() * 256.0F / 360.0F));

        if (InteractionVisualizer.version.equals("1.13") || InteractionVisualizer.version.equals("1.13.1") || InteractionVisualizer.version.contains("legacy")) {
            packet.getIntegers().write(6, 2);
            //int data to mark
            packet.getIntegers().write(7, 1);
        } else {
            //EntityType
            packet.getEntityTypeModifier().write(0, entity.getType());
            //int data to mark
            packet.getIntegers().write(6, 1);
        }
        //UUID
        packet.getUUIDs().write(0, entity.getUniqueId());
        //Location
        packet.getDoubles()
                //X
                .write(0, location.getX())
                //Y
                .write(1, location.getY())
                //Z
                .write(2, location.getZ());
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
        packet.getIntegers()
                //Entity ID
                .write(0, entity.getEntityId())
                //Velocity x
                .write(1, (int) (entity.getVelocity().getX() * 8000))
                //Velocity y
                .write(2, (int) (entity.getVelocity().getY() * 8000))
                //Velocity z
                .write(3, (int) (entity.getVelocity().getZ() * 8000));
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void updateItem(List<Player> players, Item entity) {
        if (entity.getItemStack().getType().equals(Material.AIR)) {
            return;
        }
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        //Entity ID
        packet.getIntegers().write(0, entity.getEntityId());

        //List<DataWatcher$Item> Type are more complex
        //Create a DataWatcher
        WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
        packet.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getIntegers().write(0, entity.getEntityId());
        packet.getDoubles().write(0, entity.getLocation().getX());
        packet.getDoubles().write(1, entity.getLocation().getY());
        packet.getDoubles().write(2, entity.getLocation().getZ());
        packet.getBytes().write(0, (byte) (int) (entity.getLocation().getYaw() * 256.0F / 360.0F));
        packet.getBytes().write(1, (byte) (int) (entity.getLocation().getPitch() * 256.0F / 360.0F));
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_VELOCITY);
        packet.getIntegers()
                //Entity ID
                .write(0, entity.getEntityId())
                //Velocity x
                .write(1, (int) (entity.getVelocity().getX() * 8000))
                //Velocity y
                .write(2, (int) (entity.getVelocity().getY() * 8000))
                //Velocity z
                .write(3, (int) (entity.getVelocity().getZ() * 8000));
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void removeItem(List<Player> players, Item entity, boolean removeFromActive) {
        if (entity.getItemStack().getType() == Material.AIR) { // might be fast to check memory location for enums intead of using equals()
            return;
        }
        EntityKeyHolder entityKey = new EntityKeyHolder(entity); // short lived object as key
        if (removeFromActive) {
            active.remove(entityKey);
            loaded.remove(entityKey);
        }
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntegerArrays().write(0, new int[]{entity.getEntityId()});
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void removeItem(List<Player> players, Item entity) {
        removeItem(players, entity, true);
    }

    public static void sendItemFrameSpawn(List<Player> players, ItemFrame entity) {
        EntityKeyHolder entityKey = new EntityKeyHolder(entity); // short lived object as key unless active contains key
        if (!active.containsKey(entityKey)) {
            active.put(entityKey, players);
            loaded.put(entityKey, true);
        }

        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);

        //Location
        Location location = entity.getLocation();

        //and add data based on packet class in NMS  (global scope variable)
        //Reference: https://wiki.vg/Protocol#Spawn_Object
        packet.getIntegers()
                //Entity ID
                .write(0, entity.getEntityId())
                //Velocity x
                .write(1, 0)
                //Velocity y
                .write(2, 0)
                //Velocity z
                .write(3, 0)
                //Pitch
                .write(4, (int) (entity.getPitch() * 256.0F / 360.0F))
                //Yaw
                .write(5, (int) (entity.getYaw() * 256.0F / 360.0F));

        if (InteractionVisualizer.version.equals("1.13") || InteractionVisualizer.version.equals("1.13.1") || InteractionVisualizer.version.contains("legacy")) {
            packet.getIntegers().write(6, 33);
            //int data to mark
            packet.getIntegers().write(7, getItemFrameData(entity));
        } else {
            //EntityType
            packet.getEntityTypeModifier().write(0, entity.getType());
            //int data to mark
            packet.getIntegers().write(6, getItemFrameData(entity));
        }
        //UUID
        packet.getUUIDs().write(0, entity.getUniqueId());
        //Location
        packet.getDoubles()
                //X
                .write(0, location.getX())
                //Y
                .write(1, location.getY())
                //Z
                .write(2, location.getZ());
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static int getItemFrameData(ItemFrame frame) {
        switch (frame.getAttachedFace()) {
            case UP:
                return 1;
            case NORTH:
                return 2;
            case SOUTH:
                return 3;
            case WEST:
                return 4;
            case EAST:
                return 5;
            default:
                return 0;
        }
    }

    public static void updateItemFrame(List<Player> players, ItemFrame entity) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        //Entity ID
        packet.getIntegers().write(0, entity.getEntityId());

        //List<DataWatcher$Item> Type are more complex
        //Create a DataWatcher
        WrappedDataWatcher wpw = entity.getWrappedDataWatcher();
        packet.getWatchableCollectionModifier().write(0, wpw.getWatchableObjects());
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void removeItemFrame(List<Player> players, ItemFrame entity, boolean removeFromActive) {
        EntityKeyHolder entityKey = new EntityKeyHolder(entity);
        if (removeFromActive) {
            active.remove(entityKey);
            loaded.remove(entityKey);
        }
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntegerArrays().write(0, new int[]{entity.getEntityId()});
        try {
            for (Player player : players) {
                protocolManager.sendServerPacket(player, packet);
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void removeItemFrame(List<Player> players, ItemFrame entity) {
        removeItemFrame(players, entity, true);
    }

    public static void removeAll(Player theplayer) {
        List<Player> player = new ArrayList<>();
        player.add(theplayer);
        for (Entry<EntityKeyHolder, List<Player>> entry : active.entrySet()) {
            EntityKeyHolder entityKey = entry.getKey();
            if (entityKey.isArmorStand()) {
                removeArmorStand(player, entityKey.getArmorStand(), false);
            } else if (entityKey.isItem()) {
                removeItem(player, entityKey.getItem(), false);
            } else if (entityKey.isItemFrame()) {
                removeItemFrame(player, entityKey.getItemFrame(), false);
            }
        }
    }

    public static void sendPlayerPackets(Player theplayer) {
        List<Player> player = new ArrayList<>();
        player.add(theplayer);
        for (Entry<EntityKeyHolder, List<Player>> entry : active.entrySet()) {
            EntityKeyHolder entityKey = entry.getKey();
            if (entry.getValue().contains(theplayer)) {
                if (loaded.get(entityKey)) {
                    if (entityKey.isArmorStand()) {
                        sendArmorStandSpawn(player, entityKey.getArmorStand());
                        updateArmorStand(player, entityKey.getArmorStand());
                    } else if (entityKey.isItem()) {
                        sendItemSpawn(player, entityKey.getItem());
                        updateItem(player, entityKey.getItem());
                    } else if (entityKey.isItemFrame()) {
                        sendItemFrameSpawn(player, entityKey.getItemFrame());
                        updateItemFrame(player, entityKey.getItemFrame());
                    }
                }
            }
        }
    }

}
