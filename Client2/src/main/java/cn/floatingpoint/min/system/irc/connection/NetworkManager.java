package cn.floatingpoint.min.system.irc.connection;

import cn.floatingpoint.min.system.irc.IRCClient;
import cn.floatingpoint.min.system.irc.handler.INetHandler;
import cn.floatingpoint.min.system.irc.packet.Encoder;
import cn.floatingpoint.min.system.irc.packet.Packet;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

public class NetworkManager {
    private final IRCClient client;
    public INetHandler packetListener;
    public volatile boolean lock;

    public NetworkManager(IRCClient client) {
        this.client = client;
        this.lock = false;
    }

    public void sendPacket(Packet<?> packetIn) {
        if (!lock) {
            try {
                byte[] result = Encoder.encode(packetIn);
                client.send(result);
            } catch (Exception e) {
                if (e instanceof WebsocketNotConnectedException) {
                    client.startReconnection();
                    lock = true;
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
