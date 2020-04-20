package com.loohp.interactionvisualizer.Manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loohp.interactionvisualizer.InteractionVisualizer;

public class CustomBlockDataManager {

    private static File file;
    private static JSONObject json;
    private static final JSONParser parser = new JSONParser();
    private static final Plugin plugin = InteractionVisualizer.plugin;

    public static void intervalSaveToFile() {
        new BukkitRunnable() {
            public void run() {
                save();
            }
        }.runTaskTimerAsynchronously(plugin, 200, 600);
    }

    public static void setup() {
        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdir();
            }
            file = new File(plugin.getDataFolder(), "blockdata.json");
            if (!file.exists()) {
                PrintWriter pw = new PrintWriter(file, "UTF-8");
                pw.print("{");
                pw.print("}");
                pw.flush();
                pw.close();
            }
            json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)); //Use StandardCharsets
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean save() {
        try {
            JSONObject toSave = json;

            TreeMap<String, Object> treeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            treeMap.putAll(toSave);

            Gson g = new GsonBuilder().setPrettyPrinting().create();
            String prettyJsonString = g.toJson(treeMap);

            PrintWriter clear = new PrintWriter(file);
            clear.print("");
            clear.close();

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(prettyJsonString);
            writer.flush();
            writer.close();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static JSONObject getJsonObject() {
        return json;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> getBlock(String key) {
        Object obj = json.get(key);
        if (obj == null) {
            return null;
        }
        JSONObject value = (JSONObject) obj;
        HashMap<String, Object> map = new HashMap<>();
        for (String keykey : (Iterable<String>) value.keySet()) {
            map.put(keykey, value.get(keykey));
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static void setBlock(String key, HashMap<String, Object> map) {
        Object obj = json.get(key);
        JSONObject value = (obj != null) ? (JSONObject) obj : new JSONObject();
        for (Entry<String, Object> entry : map.entrySet()) {
            value.put(entry.getKey(), entry.getValue());
        }
        json.put(key, value);
    }

    public static void removeBlock(String key) {
        json.remove(key);
    }

    public static boolean contains(String key) {
        return json.containsKey(key);
    }

    public static String locKey(Location loc) {
        return loc.getWorld().getName() + "_" + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
    }

    public static Location keyLoc(String key) {
        String[] breakdown = key.split("_");
        String worldString;
        List<String> list = Arrays.asList(breakdown).subList(0, (breakdown.length - 3));
        worldString = String.join("_", list);
        World world = Bukkit.getWorld(worldString);
        int x = Integer.parseInt(breakdown[breakdown.length - 3]);
        int y = Integer.parseInt(breakdown[breakdown.length - 2]);
        int z = Integer.parseInt(breakdown[breakdown.length - 1]);
        return new Location(world, x, y, z); //initialize value
    }

}
