package com.modernwarfare.network;

import com.modernwarfare.ModernWarfare;
import com.modernwarfare.item.FirearmItem;
import com.modernwarfare.item.ModItems;
import com.modernwarfare.state.PlayerCombatState;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public final class ModPackets {
    public static final net.minecraft.util.Identifier ACTION = ModernWarfare.id("action");
    private ModPackets() {}
    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(ACTION, (server, player, handler, buf, responseSender) -> {
            int action = buf.readVarInt();
            server.execute(() -> handle(player, action));
        });
    }
    private static void handle(ServerPlayerEntity player, int action) {
        if (PlayerCombatState.isDowned(player) && action != 3 && action != 4) return;
        switch (action) {
            case 1 -> {
                boolean prone = !PlayerCombatState.isProne(player);
                PlayerCombatState.setProne(player, prone);
                player.setPose(prone ? EntityPose.SWIMMING : EntityPose.STANDING);
                player.sendMessage(Text.literal(prone ? "Лежачи" : "Стоячи"), true);
            }
            case 2 -> reload(player);
            case 3 -> interactWithDowned(player, false);
            case 4 -> interactWithDowned(player, true);
            case 5 -> slide(player);
            default -> { }
        }
    }
    private static void reload(ServerPlayerEntity player) {
        ItemStack held = player.getMainHandStack();
        if (!(held.getItem() instanceof FirearmItem)) { player.sendMessage(Text.literal("Тримай зброю в основній руці."), true); return; }
        // FirearmItem reads this NBT value. Reserve ammo is stored in the player's inventory.
        int ammo = held.getOrCreateNbt().getInt("Ammo");
        int magazine = held.isOf(ModItems.ASSAULT_RIFLE) ? 30 : 12;
        int needed = magazine - ammo;
        for (int i = 0; i < player.getInventory().size() && needed > 0; i++) {
            ItemStack inv = player.getInventory().getStack(i);
            if (inv.isOf(ModItems.RIFLE_AMMO)) {
                int take = Math.min(needed, inv.getCount()); inv.decrement(take); ammo += take; needed -= take;
            }
        }
        held.getOrCreateNbt().putInt("Ammo", ammo);
        player.sendMessage(Text.literal("Магазин: " + ammo + "/" + magazine), true);
    }
    private static void interactWithDowned(ServerPlayerEntity player, boolean finish) {
        ServerPlayerEntity target = player.getServerWorld().getPlayers().stream()
            .filter(p -> p != player && PlayerCombatState.isDowned(p) && p.squaredDistanceTo(player) <= 9.0)
            .min((a,b) -> Double.compare(a.squaredDistanceTo(player), b.squaredDistanceTo(player))).orElse(null);
        if (target == null) { player.sendMessage(Text.literal("Поруч немає пораненого гравця."), true); return; }
        if (finish) {
            PlayerCombatState.remove(target);
            target.kill();
            player.sendMessage(Text.literal("Супротивника добито."), true);
        } else {
            PlayerCombatState.setDowned(target, false);
            target.setHealth(Math.max(6.0f, target.getMaxHealth() * 0.35f));
            target.setPose(EntityPose.STANDING);
            target.sendMessage(Text.literal("Тебе підняли! Знайди укриття."), true);
            player.sendMessage(Text.literal("Союзника піднято."), true);
        }
    }
    private static void slide(ServerPlayerEntity player) {
        if (!player.isSprinting() || !player.isOnGround()) return;
        Vec3d look = player.getRotationVec(1.0f);
        player.addVelocity(look.x * 0.8, 0.08, look.z * 0.8);
        player.velocityModified = true;
        player.setPose(EntityPose.SWIMMING);
    }
}
