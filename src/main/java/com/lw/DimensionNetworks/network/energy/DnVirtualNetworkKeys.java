package com.lw.DimensionNetworks.network.energy;

import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        GameProfile profile = player.getGameProfile();
        if (profile == null || profile.getId() == null) {
            return null;
        }

        try {
            if (Universe.loaded()) {
                Universe universe = Universe.get();
                ForgePlayer forgePlayer = universe == null ? null : universe.getPlayer(profile);
                ForgeTeam team = forgePlayer == null ? null : forgePlayer.team;
                String key = teamKey(team);
                if (key != null) {
                    return key;
                }
            }

            String teamId = FTBLibAPI.getTeam(profile.getId());
            if (teamId != null && !teamId.isEmpty()) {
                return "ftbu:" + teamId;
            }
        } catch (RuntimeException ignored) {
        }
        return null;
    }

    private static String teamKey(ForgeTeam team) {
        if (team == null) {
            return null;
        }
        String id = team.getId();
        if (id == null || id.isEmpty()) {
            id = team.getUIDCode();
        }
        return id == null || id.isEmpty() ? null : "ftbu:" + id;
    }
}
