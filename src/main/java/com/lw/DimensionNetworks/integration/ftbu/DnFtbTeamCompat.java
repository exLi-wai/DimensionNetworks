package com.lw.DimensionNetworks.integration.ftbu;

import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;

public final class DnFtbTeamCompat {

    private DnFtbTeamCompat() {
    }

    public static String getTeamKey(EntityPlayer player) {
        GameProfile profile = player.getGameProfile();
        if (profile == null || profile.getId() == null) {
            return null;
        }

        try {
            ForgeTeam team = getCurrentTeam(profile);
            String key = getTeamKey(team);
            if (key != null) {
                return key;
            }

            String teamId = FTBLibAPI.getTeam(profile.getId());
            return isBlank(teamId) ? null : "ftbu:" + teamId;
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public static String getTeamDisplayName(EntityPlayer player) {
        GameProfile profile = player.getGameProfile();
        if (profile == null) {
            return null;
        }

        try {
            return getTeamDisplayName(getCurrentTeam(profile));
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static ForgeTeam getCurrentTeam(GameProfile profile) {
        if (!Universe.loaded()) {
            return null;
        }

        Universe universe = Universe.get();
        ForgePlayer forgePlayer = universe == null ? null : universe.getPlayer(profile);
        return forgePlayer == null ? null : forgePlayer.team;
    }

    private static String getTeamKey(ForgeTeam team) {
        if (team == null) {
            return null;
        }

        String id = team.getId();
        if (isBlank(id)) {
            id = team.getUIDCode();
        }
        return isBlank(id) ? null : "ftbu:" + id;
    }

    private static String getTeamDisplayName(ForgeTeam team) {
        if (team == null) {
            return null;
        }

        ITextComponent title = team.getTitle();
        if (title != null && !isBlank(title.getUnformattedText())) {
            return title.getUnformattedText();
        }

        String id = team.getId();
        if (!isBlank(id)) {
            return id;
        }

        return team.getUIDCode();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
