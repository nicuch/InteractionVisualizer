package com.loohp.interactionvisualizer.Manager;

import com.loohp.interactionvisualizer.InteractionVisualizer;
import com.loohp.interactionvisualizer.godplskillme.ChunkKey;
import io.netty.util.internal.ConcurrentSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayerRangeManager {

    private static final Plugin plugin = InteractionVisualizer.plugin;
    private static ConcurrentSet<ChunkKey> current = new ConcurrentSet<>();
    private static ConcurrentSet<ChunkKey> upcomming = new ConcurrentSet<>();

    public static boolean hasPlayerNearby(Location location) {
        String world = location.getWorld().getUID().toString();
        int x = (int) Math.floor((double) location.getBlockX() / 16.0);
        int z = (int) Math.floor((double) location.getBlockZ() / 16.0);
        ChunkKey chunkKey = new ChunkKey(x, z, world); //short lived object
        if (current.contains(chunkKey)) {
            return location.getWorld().isChunkLoaded(x, z);
        }
        return false;
    }

    public static void run() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.isOnline()) {
                        continue;
                    }
                    Location location = player.getLocation().clone();
                    String world = location.getWorld().getUID().toString();
                    int chunkX = (int) Math.floor((double) location.getBlockX() / 16.0);
                    int chunkZ = (int) Math.floor((double) location.getBlockZ() / 16.0);

                    upcomming.add(new ChunkKey(chunkX + 1, chunkZ + 1, world));
                    upcomming.add(new ChunkKey(chunkX + 1, chunkZ, world));
                    upcomming.add(new ChunkKey(chunkX + 1, chunkZ - 1, world));
                    upcomming.add(new ChunkKey(chunkX, chunkZ + 1, world));
                    upcomming.add(new ChunkKey(chunkX, chunkZ, world));
                    upcomming.add(new ChunkKey(chunkX, chunkZ - 1, world));
                    upcomming.add(new ChunkKey(chunkX - 1, chunkZ + 1, world));
                    upcomming.add(new ChunkKey(chunkX - 1, chunkZ, world));
                    upcomming.add(new ChunkKey(chunkX - 1, chunkZ - 1, world));
                }
                current = upcomming;
                upcomming = new ConcurrentSet<>();
            } catch (Exception e) {
                upcomming = new ConcurrentSet<>();
            } finally {
                if (plugin.isEnabled()) {
                    Bukkit.getScheduler().runTaskLater(plugin, PlayerRangeManager::run, 1);
                }
            }
        });
    }

}
