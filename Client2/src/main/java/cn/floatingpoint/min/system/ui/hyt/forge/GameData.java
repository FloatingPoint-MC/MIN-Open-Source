package cn.floatingpoint.min.system.ui.hyt.forge;

import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;

public record GameData(GameType gameType, boolean hardcoreMode, WorldType worldType, int dimension, EnumDifficulty difficulty, int playerId, int maxPlayers, boolean reducedDebugInfo) {
}
