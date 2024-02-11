package cn.floatingpoint.min.system.anticheat.check.impl.packet.impl;

import cn.floatingpoint.min.system.anticheat.check.impl.packet.PacketCheck;
import cn.floatingpoint.min.system.irc.IRCClient;
import cn.floatingpoint.min.system.irc.packet.impl.CPacketAntiCheatData;
import cn.floatingpoint.min.utils.client.Pair;
import net.minecraft.block.BlockIce;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class SpeedCheck extends PacketCheck {
    private boolean ignore;
    private boolean ignoreFlying;
    private double lastFlightDistance;
    private double lastOnGroundPosX, lastOnGroundPosY, lastOnGroundPosZ;
    private double distanceToGround;

    @Override
    protected void onPacket(Packet<?> packetIn) {
        // The player hasn't spawned yet
        if (!mc.player.connection.isDoneLoadingTerrain()) return;

        // Fix water issue
        if (mc.player.isInWater() || mc.player.isInLava()) return;

        if (packetIn instanceof SPacketEntityVelocity packet) {
            if (packet.getEntityID() == mc.player.getEntityId()) {
                if (packet.getMotionX() != 0.0 || packet.getMotionY() != 0.0 || packet.getMotionZ() != 0.0) {
                    ignore = true;
                }
            }
        } else if (packetIn instanceof SPacketExplosion) {
            ignore = true;
        } else if (packetIn instanceof SPacketEntityTeleport) {
            ignore = true;
        } else if (packetIn instanceof SPacketPlayerPosLook packet) {
            if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X) || packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y) || packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z)) {
                ignore = true;
            }
        } else if (packetIn instanceof CPacketPlayer packet) {
            if (!mc.player.capabilities.isFlying) {
                if (packet.isMoving()) {
                    double yDist = packet.getY() - lastOnGroundPosY;
                    distanceToGround = Math.max(yDist, distanceToGround);
                    if (packet.isOnGround() || mc.player.onGround) {
                        // Fix stop timing
                        if (mc.player.ticksExisted > 300) {
                            double threshold = 0.01;
                            if (mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById(1)))) {
                                Optional<PotionEffect> effect = mc.player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getPotion() == Potion.getPotionById(1)).findFirst();
                                if (effect.isPresent()) {
                                    threshold += 0.202 * (effect.get().getAmplifier() + 1);
                                } else {
                                    ignore = true; // IDK when the potion expires, player doesn't slow down immediately
                                }
                            }
                            ArrayList<Pair<CPacketAntiCheatData.Type, Object>> data = new ArrayList<>();
                            // Fix Jump on Ice
                            // Maybe due to this, we have to run a tick to check
                            if (mc.world.getBlockState(new BlockPos((int) lastOnGroundPosX, (int) (lastOnGroundPosY - 1), (int) lastOnGroundPosZ)).getBlock() instanceof BlockIce) {
                                threshold *= 1.6384;
                            }
                            data.add(new Pair<>(CPacketAntiCheatData.Type.DOUBLE, threshold));
                            if (distanceToGround > 1.0 && distanceToGround < 1.4) {
                                if (!ignore) {
                                    double xDist = packet.getX() - lastOnGroundPosX;
                                    double zDist = packet.getZ() - lastOnGroundPosZ;
                                    double horizontalDistance = Math.hypot(xDist, zDist);
                                    if (mc.player.onGround) {
                                        // Jump out of max distance
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.INT, 0));
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.DOUBLE, horizontalDistance));
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.BOOLEAN, horizontalDistance > threshold * 4.276060125693277));
                                    } else if (yDist == 0.5) {
                                        // Jump out of max slab distance
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.INT, 1));
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.DOUBLE, horizontalDistance));
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.BOOLEAN, horizontalDistance > threshold * 3.6472108649170423));
                                    } else if (yDist <= -0.5 && yDist >= -1.0) {
                                        // Jump out of max down-distance
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.INT, 2));
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.DOUBLE, horizontalDistance));
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.BOOLEAN, horizontalDistance > threshold * 5.00911488315166));
                                    } else if (yDist == 1.0) {
                                        // Jump out of max up-distance
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.INT, 3));
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.DOUBLE, horizontalDistance));
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.BOOLEAN, horizontalDistance > threshold * 3.54));
                                    }
                                }
                                if (mc.player.onGround) {
                                    ignore = false;
                                    double xDist = packet.getX() - lastOnGroundPosX;
                                    double zDist = packet.getZ() - lastOnGroundPosZ;
                                    distanceToGround = Math.hypot(xDist, zDist);
                                    lastOnGroundPosX = packet.getX();
                                    lastOnGroundPosY = packet.getY();
                                    lastOnGroundPosZ = packet.getZ();
                                }
                            } else if (distanceToGround > 0.0) {
                                data.add(new Pair<>(CPacketAntiCheatData.Type.INT, 4));
                                double xDist = packet.getX() - lastOnGroundPosX;
                                double zDist = packet.getZ() - lastOnGroundPosZ;
                                double horizontalDistance = Math.hypot(xDist, zDist);
                                if (horizontalDistance <= threshold * 0.525 && mc.player.onGround) {
                                    if (horizontalDistance < threshold * 0.29634358849938395) {
                                        ignore = false;
                                        distanceToGround = 0.0;
                                    } else {
                                        distanceToGround = yDist >= 0.0 ? horizontalDistance : Math.min(horizontalDistance, 0.99999999); // yDist >= 0.0 to make sure player stop flying
                                    }
                                    lastOnGroundPosX = packet.getX();
                                    lastOnGroundPosY = packet.getY();
                                    lastOnGroundPosZ = packet.getZ();
                                }
                            } else if (distanceToGround == 0.0) {
                                double xDist = packet.getX() - lastOnGroundPosX;
                                double zDist = packet.getZ() - lastOnGroundPosZ;
                                double horizontalDistance = Math.hypot(xDist, zDist);
                                // Add ignore to avoid lagging players that stop high-speed flying(knock-back)
                                if (ignore && ignoreFlying) {
                                    lastFlightDistance = horizontalDistance - 1.0;
                                }
                                data.add(new Pair<>(CPacketAntiCheatData.Type.INT, 5));
                                data.add(new Pair<>(CPacketAntiCheatData.Type.DOUBLE, horizontalDistance));
                                data.add(new Pair<>(CPacketAntiCheatData.Type.BOOLEAN, ignore));
                                data.add(new Pair<>(CPacketAntiCheatData.Type.DOUBLE, yDist));
                                if (!ignore && horizontalDistance > threshold * 0.29634358840938395 && yDist == 0.0) { // yDist 0.0 to make sure player falls
                                    if (!ignoreFlying) {
                                        // OnGround out of max distance
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.INT, 0));
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.BOOLEAN, true));
                                    } else {
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.INT, 1));
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.DOUBLE, lastFlightDistance));
                                        data.add(new Pair<>(CPacketAntiCheatData.Type.BOOLEAN, horizontalDistance >= lastFlightDistance));
                                        lastFlightDistance = horizontalDistance;
                                    }
                                } else if (mc.player.onGround) {
                                    lastOnGroundPosX = packet.getX();
                                    lastOnGroundPosY = packet.getY();
                                    lastOnGroundPosZ = packet.getZ();
                                    ignoreFlying = false;
                                    data.add(new Pair<>(CPacketAntiCheatData.Type.INT, 2));
                                    data.add(new Pair<>(CPacketAntiCheatData.Type.BOOLEAN, false));
                                }
                                ignore = false;
                            }
                            IRCClient.getInstance().addToSendQueue(new CPacketAntiCheatData(4, data));
                        }
                    }
                }
            } else {
                ignore = true;
                ignoreFlying = true;
            }
        }
    }
}
