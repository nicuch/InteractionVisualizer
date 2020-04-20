package com.loohp.interactionvisualizer.godplskillme;

import com.loohp.interactionvisualizer.Holder.ArmorStand;
import com.loohp.interactionvisualizer.Holder.Item;
import com.loohp.interactionvisualizer.Holder.ItemFrame;

public class EntityKeyHolder {
    private final ArmorStand armorStand;
    private final Item item;
    private final ItemFrame itemFrame;

    public EntityKeyHolder(ArmorStand armorStand) {
        this.armorStand = armorStand;
        this.item = null;
        this.itemFrame = null;
    }

    public EntityKeyHolder(Item item) {
        this.item = item;
        this.armorStand = null;
        this.itemFrame = null;
    }

    public EntityKeyHolder(ItemFrame itemFrame) {
        this.itemFrame = itemFrame;
        this.item = null;
        this.armorStand = null;
    }

    public boolean isArmorStand() {
        return this.armorStand != null;
    }

    public boolean isItem() {
        return this.item != null;
    }

    public boolean isItemFrame() {
        return this.itemFrame != null;
    }

    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    public Item getItem() {
        return this.item;
    }

    public ItemFrame getItemFrame() {
        return this.itemFrame;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityKeyHolder)) return false;
        EntityKeyHolder that = (EntityKeyHolder) o;
        if (this.isArmorStand() && that.isArmorStand()) {
            return this.armorStand.getUniqueId().equals(that.armorStand.getUniqueId());
        } else if (this.isItem() && that.isItem()) {
            return this.item.getUniqueId().equals(that.item.getUniqueId());
        } else if (this.isItemFrame() && that.isItemFrame()) {
            return this.itemFrame.getUniqueId().equals(that.itemFrame.getUniqueId());
        }
        // else
        return false;
    }

    @Override
    public int hashCode() {
        if (this.item != null)
            return item.getUniqueId().hashCode();
        if (this.itemFrame != null)
            return itemFrame.getUniqueId().hashCode();
        return this.armorStand.hashCode();
    }
}
