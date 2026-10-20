package com.modernwarfare.item;

import com.modernwarfare.ModernWarfare;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

public final class ModItems {
    public static final Item ASSAULT_RIFLE = new FirearmItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON), 30, 7.0f, 0.08f, 32);
    public static final Item COMBAT_PISTOL = new FirearmItem(new FabricItemSettings().maxCount(1), 12, 5.0f, 0.04f, 24);
    public static final Item RIFLE_AMMO = new Item(new FabricItemSettings());
    public static final Item ARMOR_PLATE = new Item(new FabricItemSettings().maxCount(16));
    public static final Item FIELD_MEDKIT = new Item(new FabricItemSettings().maxCount(4));
    private static boolean registered = false;
    private ModItems() {}
    public static void register() {
        if (registered) return;
        registered = true;
        Registry.register(Registries.ITEM, ModernWarfare.id("assault_rifle"), ASSAULT_RIFLE);
        Registry.register(Registries.ITEM, ModernWarfare.id("combat_pistol"), COMBAT_PISTOL);
        Registry.register(Registries.ITEM, ModernWarfare.id("rifle_ammo"), RIFLE_AMMO);
        Registry.register(Registries.ITEM, ModernWarfare.id("armor_plate"), ARMOR_PLATE);
        Registry.register(Registries.ITEM, ModernWarfare.id("field_medkit"), FIELD_MEDKIT);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(ASSAULT_RIFLE);
            entries.add(COMBAT_PISTOL);
            entries.add(RIFLE_AMMO);
            entries.add(ARMOR_PLATE);
            entries.add(FIELD_MEDKIT);
        });
    }
}
