package com.modernwarfare.client;

import com.modernwarfare.ModernWarfare;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModernWarfareClient implements ClientModInitializer {
    private static KeyBinding proneKey, reloadKey, reviveKey, finishKey, slideKey;
    @Override public void onInitializeClient() {
        proneKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.modernwarfare.prone", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "category.modernwarfare"));
        reloadKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.modernwarfare.reload", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.modernwarfare"));
        reviveKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.modernwarfare.revive", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F, "category.modernwarfare"));
        finishKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.modernwarfare.finish", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.modernwarfare"));
        slideKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.modernwarfare.slide", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "category.modernwarfare"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (proneKey.wasPressed()) send(1);
            while (reloadKey.wasPressed()) send(2);
            while (reviveKey.wasPressed()) send(3);
            while (finishKey.wasPressed()) send(4);
            while (slideKey.wasPressed()) send(5);
        });
    }
    private static void send(int action) {
        if (ClientPlayNetworking.canSend(ModernWarfare.id("action"))) {
            var buf = PacketByteBufs.create(); buf.writeVarInt(action);
            ClientPlayNetworking.send(ModernWarfare.id("action"), buf);
        }
    }
}
