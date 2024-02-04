package cn.floatingpoint.min.system.irc.packet;

import cn.floatingpoint.min.system.irc.packet.impl.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class EnumConnectionState {
    private static final Map<EnumPacketDirection, BiMap<Integer, Class<? extends Packet<?>>>> directionMaps;

    static {
        directionMaps = Maps.newEnumMap(EnumPacketDirection.class);
        registerPacket(EnumPacketDirection.SERVERBOUND, CPacketAdmin.class);
        registerPacket(EnumPacketDirection.SERVERBOUND, CPacketChat.class);
        registerPacket(EnumPacketDirection.SERVERBOUND, CPacketDisconnect.class);
        registerPacket(EnumPacketDirection.SERVERBOUND, CPacketHandshake.class);
        registerPacket(EnumPacketDirection.SERVERBOUND, CPacketJoinServer.class);
        registerPacket(EnumPacketDirection.SERVERBOUND, CPacketKey.class);
        registerPacket(EnumPacketDirection.SERVERBOUND, CPacketLogin.class);
        registerPacket(EnumPacketDirection.SERVERBOUND, CPacketLogout.class);
        registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayer.class);
        registerPacket(EnumPacketDirection.SERVERBOUND, CPacketRegister.class);
        registerPacket(EnumPacketDirection.SERVERBOUND, CPacketSkin.class);
        registerPacket(EnumPacketDirection.SERVERBOUND, CPacketTabComplete.class);
        registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketAccount.class);
        registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketChat.class);
        registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketDisconnect.class);
        registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketHandshake.class);
        registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketKey.class);
        registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketMuted.class);
        registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayer.class);
        registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSkin.class);
        registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTabComplete.class);
        registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketVersion.class);
    }

    private static void registerPacket(EnumPacketDirection direction, Class<? extends Packet<?>> packetClass) {
        BiMap<Integer, Class<? extends Packet<?>>> bimap = directionMaps.computeIfAbsent(direction, k -> HashBiMap.create());

        if (bimap.containsValue(packetClass)) {
            String s = direction + " packet " + packetClass + " is already known to ID " + bimap.inverse().get(packetClass);
            LogManager.getLogger().fatal(s);
            throw new IllegalArgumentException(s);
        } else {
            bimap.put(bimap.size(), packetClass);
        }
    }

    public static Integer getPacketId(EnumPacketDirection direction, Packet<?> packetIn) {
        return directionMaps.get(direction).inverse().get(packetIn.getClass());
    }

    public static Packet<?> getPacket(EnumPacketDirection direction, int packetId) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<? extends Packet<?>> oclass = directionMaps.get(direction).get(packetId);
        return oclass == null ? null : oclass.getDeclaredConstructor().newInstance();
    }
}
