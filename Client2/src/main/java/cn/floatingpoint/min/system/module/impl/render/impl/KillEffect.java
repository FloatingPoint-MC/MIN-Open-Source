package cn.floatingpoint.min.system.module.impl.render.impl;

import cn.floatingpoint.min.system.module.impl.render.RenderModule;
import cn.floatingpoint.min.system.module.value.impl.ModeValue;
import cn.floatingpoint.min.utils.client.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;

import java.util.regex.Matcher;

public class KillEffect extends RenderModule {
    public static ModeValue mode = new ModeValue(new String[]{"Flame", "Lightning"}, "Flame");

    public KillEffect() {
        addValues(new Pair<>("Mode", mode));
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onRender3D() {

    }

    public static void makeEffect(Matcher matcher) {
        if (matcher.find()) {
            String name = matcher.group(1).trim();
            EntityPlayer player = Minecraft.getMinecraft().world.getPlayerEntityByName(name);
            if (player != null) {
                if (KillEffect.mode.isCurrentMode("Flame")) {
                    for (int i = 0; i < 25; i++) {
                        double x = (double) player.getPosition().getX() + 0.5D + ((double) Minecraft.getMinecraft().world.rand.nextFloat() - 0.5D) * 2.0D;
                        double y = (double) player.getPosition().getY() + player.getEyeHeight() + ((double) Minecraft.getMinecraft().world.rand.nextFloat() - 0.5D) * 2.0D;
                        double z = (double) player.getPosition().getZ() + 0.5D + ((double) Minecraft.getMinecraft().world.rand.nextFloat() - 0.5D) * 2.0D;
                        Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
                    }
                } else if (KillEffect.mode.isCurrentMode("Lightning")) {
                    EntityLightningBolt entity = new EntityLightningBolt(Minecraft.getMinecraft().world, player.posX, player.posY + player.getEyeHeight(), player.posZ, true);
                    Minecraft.getMinecraft().world.addWeatherEffect(entity);
                }
            }
        }
    }
}
