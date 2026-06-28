package com.lw.DimensionNetworks.common.energy;

import com.lw.DimensionNetworks.common.integration.ftbu.DnFtbTeamCompat;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public final class DnVirtualNetworkKeys {

    private DnVirtualNetworkKeys() {
    }

    public static String forPlayer(World world, EntityPlayer player) {
        String ftbTeam = findFtbTeamKey(world, player);
        if (ftbTeam != null) {
            return ftbTeam;
        }

        GameProfile profile = player.getGameProfile();
        if (profile != null && profile.getId() != null) {
            return "player:" + profile.getId().toString();
        }
        return "player:" + player.getName();
    }

    public static String forBlock(World world, BlockPos pos) {
        return "block:" + world.provider.getDimension() + ":" + pos.toLong();
    }

    private static String findFtbTeamKey(World world, EntityPlayer player) {
        if (!isFtbLoaded()) {
            return null;
        }

        try {
            return DnFtbTeamCompat.getTeamKey(player);
        } catch (RuntimeException | LinkageError ignored) {
            return null;
        }
    }

    private static boolean isFtbLoaded() {
        return Loader.isModLoaded("ftblib") || Loader.isModLoaded("ftbutilities");
    }
}
