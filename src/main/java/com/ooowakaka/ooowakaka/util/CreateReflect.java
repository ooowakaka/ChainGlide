package com.ooowakaka.ooowakaka.util;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class CreateReflect {

    private static volatile boolean resolved;
    private static Constructor<?> packetCtor;
    private static Object network;
    private static Method sendMethod;

    private CreateReflect() {}

    private static void resolve() {
        resolved = true;
        try {
            Class<?> packetClass = Class.forName("com.simibubi.create.content.kinetics.chainConveyor.ServerboundChainConveyorRidingPacket");
            packetCtor = packetClass.getConstructor(BlockPos.class, boolean.class);
            Class<?> servicesClass = Class.forName("net.createmod.catnip.platform.CatnipServices");
            Field netField = servicesClass.getDeclaredField("NETWORK");
            network = netField.get(null);
            sendMethod = network.getClass().getMethod("sendToServer", CustomPacketPayload.class);
        } catch (Exception ignored) {}
    }

    public static void sendRidingPacket(BlockPos pos) {
        if (!resolved) resolve();
        if (packetCtor == null) return;
        try {
            sendMethod.invoke(network, packetCtor.newInstance(pos, false));
        } catch (Exception ignored) {}
    }

    public static void sendStopPacket(BlockPos pos) {
        if (!resolved) resolve();
        if (packetCtor == null) return;
        try {
            sendMethod.invoke(network, packetCtor.newInstance(pos, true));
        } catch (Exception ignored) {}
    }
}
