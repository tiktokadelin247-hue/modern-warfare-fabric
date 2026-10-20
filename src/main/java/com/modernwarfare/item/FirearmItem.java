package com.modernwarfare.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/** Simple server-side hitscan firearm. Uses original assets and vanilla rendering as a first playable prototype. */
public class FirearmItem extends Item {
    private final int magazineSize;
    private final float damage;
    private final float spread;
    private final int cooldownTicks;
    public FirearmItem(Settings settings, int magazineSize, float damage, float spread, int cooldownTicks) {
        super(settings); this.magazineSize = magazineSize; this.damage = damage; this.spread = spread; this.cooldownTicks = cooldownTicks;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            NbtCompound nbt = stack.getOrCreateNbt();
            int ammo = nbt.contains("Ammo") ? nbt.getInt("Ammo") : magazineSize;
            if (player.isSneaking()) {
                int needed = magazineSize - ammo;
                if (needed > 0 && player.getInventory().contains(new ItemStack(ModItems.RIFLE_AMMO))) {
                    for (int i = 0; i < player.getInventory().size(); i++) {
                        ItemStack inv = player.getInventory().getStack(i);
                        if (inv.isOf(ModItems.RIFLE_AMMO)) {
                            int taken = Math.min(needed, inv.getCount());
                            inv.decrement(taken); ammo += taken; needed -= taken;
                            if (needed <= 0) break;
                        }
                    }
                    nbt.putInt("Ammo", ammo);
                    player.sendMessage(Text.literal("Перезаряджено: " + ammo + "/" + magazineSize), true);
                } else player.sendMessage(Text.literal("Немає боєприпасів або магазин повний."), true);
                return TypedActionResult.success(stack);
            }
            if (player.getItemCooldownManager().isCoolingDown(this)) return TypedActionResult.success(stack);
            if (ammo <= 0) {
                player.sendMessage(Text.literal("Порожній магазин. Присядь і використай зброю для перезаряджання."), true);
                player.getItemCooldownManager().set(this, 8);
                return TypedActionResult.success(stack);
            }
            nbt.putInt("Ammo", ammo - 1);
            player.getItemCooldownManager().set(this, cooldownTicks);
            Vec3d start = player.getCameraPosVec(1.0f);
            Vec3d look = player.getRotationVec(1.0f);
            // Small deterministic spread based on tick count; server owns damage calculation.
            double jitter = ((player.age % 7) - 3) * spread * 0.01;
            Vec3d end = start.add(look.x + jitter, look.y - jitter * 0.4, look.z + jitter).multiply(100).add(start.multiply(-99));
            Box searchBox = player.getBoundingBox().stretch(look.multiply(100)).expand(2.0);
            Entity target = null; double nearest = 10000.0;
            for (Entity candidate : world.getOtherEntities(player, searchBox, e -> e instanceof LivingEntity && e.isAlive())) {
                var hit = candidate.getBoundingBox().expand(0.25).raycast(start, end);
                if (hit.isPresent()) {
                    double d = start.squaredDistanceTo(hit.get());
                    if (d < nearest) { nearest = d; target = candidate; }
                }
            }
            if (target instanceof LivingEntity living) {
                living.damage(player.getDamageSources().playerAttack(player), damage);
                living.takeKnockback(0.2, -look.x, -look.z);
            }
        }
        return TypedActionResult.success(stack, world.isClient());
    }
}
