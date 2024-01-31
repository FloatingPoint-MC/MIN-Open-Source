package cn.floatingpoint.min.system.module.impl.render.impl;

import cn.floatingpoint.min.system.module.impl.render.RenderModule;
import cn.floatingpoint.min.system.module.value.impl.ModeValue;
import cn.floatingpoint.min.system.module.value.impl.OptionValue;
import cn.floatingpoint.min.utils.client.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;

import java.util.Random;
import java.util.regex.Matcher;

public class KillEffect extends RenderModule {
    public static ModeValue mode = new ModeValue(new String[]{"Flame", "Lightning"}, "Flame");
    public static OptionValue sound = new OptionValue(true);

    public KillEffect() {
        addValues(new Pair<>("Mode", mode), new Pair<>("Sound", sound));
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
            String name = matcher.group(2).trim();
            EntityPlayer player = Minecraft.getMinecraft().world.getPlayerEntityByName(name);
            if (player != null) {
                if (KillEffect.mode.isCurrentMode("Flame")) {
                    for (int i = 0; i < 25; i++) {
                        double x = (double) player.getPosition().getX() + 0.5D + ((double) Minecraft.getMinecraft().world.rand.nextFloat() - 0.5D) * 2.0D;
                        double y = (double) player.getPosition().getY() + player.getEyeHeight() + ((double) Minecraft.getMinecraft().world.rand.nextFloat() - 0.5D) * 2.0D;
                        double z = (double) player.getPosition().getZ() + 0.5D + ((double) Minecraft.getMinecraft().world.rand.nextFloat() - 0.5D) * 2.0D;
                        Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
                    }
                    if (sound.getValue()) {
                        Minecraft.getMinecraft().world.playSound(player.posX + 0.5D, player.posY + player.getEyeHeight(), player.posZ + 0.5D, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                    }
                } else if (KillEffect.mode.isCurrentMode("Lightning")) {
                    Minecraft.getMinecraft().world.addWeatherEffect(new EntityLightningBolt(Minecraft.getMinecraft().world, player.posX, player.posY + player.getEyeHeight(), player.posZ, true));
                    if (sound.getValue()) {
                        Random rand = new Random();
                        Minecraft.getMinecraft().world.playSound(player.posX, player.posY + player.getEyeHeight(), player.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + rand.nextFloat() * 0.2F, false);
                        Minecraft.getMinecraft().world.playSound(player.posX, player.posY + player.getEyeHeight(), player.posZ, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + rand.nextFloat() * 0.2F, false);
                    }
                }
            }
        }
    }
}
