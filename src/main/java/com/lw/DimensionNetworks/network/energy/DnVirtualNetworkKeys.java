package com.lw.DimensionNetworks.network.energy;

import java.lang.reflect.Method;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class DnVirtualNetworkKeys {

    private static final String[] UNIVERSE_CLASSES = new String[] {
            "com.feed_the_beast.ftblib.lib.data.Universe",
            "com.feed_the_beast.ftblib.lib.data.FTBLibAPI"
    };

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
        for (String className : UNIVERSE_CLASSES) {
            String key = tryUniverseClass(className, world, player);
            if (key != null) {
                return key;
            }
        }
        return null;
    }

    private static String tryUniverseClass(String className, World world, EntityPlayer player) {
        try {
            Class<?> universeClass = Class.forName(className);
            Object universe = invokeStaticNoArgs(universeClass, "get");
            if (universe == null) {
                universe = invokeStaticNoArgs(universeClass, "getUniverse");
            }
            if (universe == null) {
                return null;
            }

            Object team = invokeTeamLookup(universe, player);
            if (team == null) {
                return null;
            }
            String id = firstStringResult(team, "getId", "getUID", "getUniqueID", "getName", "getTitle");
            return id == null || id.isEmpty() ? "ftbu:" + team.toString() : "ftbu:" + id;
        } catch (ReflectiveOperationException ignored) {
            return null;
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static Object invokeStaticNoArgs(Class<?> type, String methodName) throws ReflectiveOperationException {
        try {
            Method method = type.getMethod(methodName);
            return method.invoke(null);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    private static Object invokeTeamLookup(Object universe, EntityPlayer player) throws ReflectiveOperationException {
        for (String methodName : new String[] { "getTeam", "getPlayerTeam" }) {
            for (Method method : universe.getClass().getMethods()) {
                if (!method.getName().equals(methodName) || method.getParameterTypes().length != 1) {
                    continue;
                }
                Class<?> parameter = method.getParameterTypes()[0];
                Object argument = null;
                if (parameter.isAssignableFrom(EntityPlayer.class)) {
                    argument = player;
                } else if (parameter.isAssignableFrom(GameProfile.class)) {
                    argument = player.getGameProfile();
                } else if (parameter == String.class) {
                    argument = player.getName();
                }
                if (argument != null) {
                    return method.invoke(universe, argument);
                }
            }
        }
        return null;
    }

    private static String firstStringResult(Object target, String... methodNames) throws ReflectiveOperationException {
        for (String methodName : methodNames) {
            try {
                Method method = target.getClass().getMethod(methodName);
                Object result = method.invoke(target);
                if (result != null) {
                    return result.toString();
                }
            } catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }
}
