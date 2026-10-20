package com.modernwarfare.state;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Server-authoritative temporary state. All players in a multiplayer world share this logic. */
public final class PlayerCombatState {
    private static final Set<UUID> DOWNED = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> PRONE = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, Integer> BLEEDOUT = new ConcurrentHashMap<>();
    private PlayerCombatState() {}

    public static boolean isDowned(ServerPlayerEntity player) { return DOWNED.contains(player.getUuid()); }
    public static boolean isProne(ServerPlayerEntity player) { return PRONE.contains(player.getUuid()); }
    public static void setProne(ServerPlayerEntity player, boolean value) {
        if (value) PRONE.add(player.getUuid()); else PRONE.remove(player.getUuid());
    }
    public static void setDowned(ServerPlayerEntity player, boolean value) {
        if (value) { DOWNED.add(player.getUuid()); BLEEDOUT.put(player.getUuid(), 20 * 30); PRONE.remove(player.getUuid()); }
        else { DOWNED.remove(player.getUuid()); BLEEDOUT.remove(player.getUuid()); }
    }
    public static boolean tickBleedout(ServerPlayerEntity player) {
        int left = BLEEDOUT.getOrDefault(player.getUuid(), 0) - 1;
        BLEEDOUT.put(player.getUuid(), left);
        return left <= 0;
    }
    public static void remove(ServerPlayerEntity player) {
        DOWNED.remove(player.getUuid()); PRONE.remove(player.getUuid()); BLEEDOUT.remove(player.getUuid());
    }
}
