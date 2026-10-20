package com.modernwarfare;

import com.modernwarfare.item.ModItems;
import com.modernwarfare.network.ModPackets;
import com.modernwarfare.state.PlayerCombatState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ModernWarfare implements ModInitializer {
    public static final String MOD_ID = "modernwarfare";
    public static Identifier id(String path) { return new Identifier(MOD_ID, path); }

    @Override
    public void onInitialize() {
        ModItems.register();
        ModPackets.registerServerReceivers();
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
            if (!(entity instanceof ServerPlayerEntity player)) return true;
            if (PlayerCombatState.isDowned(player)) {
                PlayerCombatState.remove(player);
                return true;
            }
            PlayerCombatState.setDowned(player, true);
            player.setHealth(1.0f);
            player.setPose(EntityPose.SWIMMING);
            player.sendMessage(Text.literal("§cТебе завдали критичного поранення! Союзник може підняти тебе."), true);
            return false;
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (PlayerCombatState.isDowned(player)) {
                    player.setPose(EntityPose.SWIMMING);
                    player.setVelocity(Vec3d.ZERO);
                    player.velocityModified = true;
                    if (PlayerCombatState.tickBleedout(player)) {
                        PlayerCombatState.remove(player);
                        player.kill();
                    }
                } else if (PlayerCombatState.isProne(player)) {
                    player.setPose(EntityPose.SWIMMING);
                }
            }
        });
    }
}
