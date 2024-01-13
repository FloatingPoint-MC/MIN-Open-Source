package cn.floatingpoint.min.system.irc.packet;

import cn.floatingpoint.min.system.irc.packet.impl.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public enum EnumConnectionState {
    PROTOCOL(0) {
        {
            this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketAdmin.class);
            this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketChat.class);
            this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketDisconnect.class);
            this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketHandshake.class);
            this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketJoinServer.class);
            this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketKey.class);
            this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketLogin.class);
            this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketLogout.class);
            this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayer.class);
            this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketRegister.class);
            this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketTabComplete.class);
            this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketAccount.class);
            this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketChat.class);
            this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketDisconnect.class);
            this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketKey.class);
            this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketMuted.class);
            this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayer.class);
            this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTabComplete.class);
        }
    };

    private static final int field_181136_e = -1;
    private static final int field_181137_f = 2;
    private static final EnumConnectionState[] STATES_BY_ID = new EnumConnectionState[field_181137_f - field_181136_e + 1];
    private static final Map<Class<? extends Packet<?>>, EnumConnectionState> STATES_BY_CLASS = Maps.newHashMap();
    private final int id;
    private final Map<EnumPacketDirection, BiMap<Integer, Class<? extends Packet<?>>>> directionMaps;

    EnumConnectionState(int protocolId) {
        this.directionMaps = Maps.newEnumMap(EnumPacketDirection.class);
        this.id = protocolId;
    }

    protected void registerPacket(EnumPacketDirection direction, Class<? extends Packet<?>> packetClass) {
        BiMap<Integer, Class<? extends Packet<?>>> bimap = this.directionMaps.computeIfAbsent(direction, k -> HashBiMap.create());

        if (bimap.containsValue(packetClass)) {
            String s = direction + " packet " + packetClass + " is already known to ID " + bimap.inverse().get(packetClass);
            LogManager.getLogger().fatal(s);
            throw new IllegalArgumentException(s);
        } else {
            bimap.put(bimap.size(), packetClass);
        }
    }

    public Integer getPacketId(EnumPacketDirection direction, Packet<?> packetIn) {
        return this.directionMaps.get(direction).inverse().get(packetIn.getClass());
    }

    public Packet<?> getPacket(EnumPacketDirection direction, int packetId) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<? extends Packet<?>> oclass = this.directionMaps.get(direction).get(packetId);
        return oclass == null ? null : oclass.getDeclaredConstructor().newInstance();
    }

    public int getId() {
        return this.id;
    }

    public static EnumConnectionState getById(int stateId) {
        return stateId >= field_181136_e && stateId <= field_181137_f ? STATES_BY_ID[stateId - field_181136_e] : null;
    }

    public static EnumConnectionState getFromPacket(Packet<?> packetIn) {
        return STATES_BY_CLASS.get(packetIn.getClass());
    }

    static {
        for (EnumConnectionState enumconnectionstate : values()) {
            int i = enumconnectionstate.getId();

            if (i < field_181136_e || i > field_181137_f) {
                throw new Error("Invalid protocol ID " + i);
            }

            STATES_BY_ID[i - field_181136_e] = enumconnectionstate;

            for (EnumPacketDirection enumpacketdirection : enumconnectionstate.directionMaps.keySet()) {
                for (Class<? extends Packet<?>> oclass : (enumconnectionstate.directionMaps.get(enumpacketdirection)).values()) {
                    if (STATES_BY_CLASS.containsKey(oclass) && STATES_BY_CLASS.get(oclass) != enumconnectionstate) {
                        throw new Error("Packet " + oclass + " is already assigned to protocol " + STATES_BY_CLASS.get(oclass) + " - can't reassign to " + enumconnectionstate);
                    }

                    try {
                        oclass.getDeclaredConstructor().newInstance();
                    } catch (Throwable var10) {
                        throw new Error("Packet " + oclass + " fails instantiation checks! " + oclass);
                    }

                    STATES_BY_CLASS.put(oclass, enumconnectionstate);
                }
            }
        }
    }
}
