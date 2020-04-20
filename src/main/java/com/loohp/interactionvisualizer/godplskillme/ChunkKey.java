package com.loohp.interactionvisualizer.godplskillme;

import java.util.Objects;

public class ChunkKey {
    private final int x;
    private final int z;
    private final String world;

    public ChunkKey(int x, int z, String world) {
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkKey)) return false;
        ChunkKey that = (ChunkKey) o;
        return this.x == that.x &&
                this.z == that.z &&
                this.world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, world);
    }
}
