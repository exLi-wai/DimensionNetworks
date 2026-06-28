package com.lw.DimensionNetworks.api.energy;

import java.math.BigInteger;

import com.lw.DimensionNetworks.common.capability.DnCapabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public final class DnEnergyApi {

    private DnEnergyApi() {
    }

    public static boolean hasDnEnergyCapability(TileEntity tile, EnumFacing facing) {
        return tile != null && tile.hasCapability(DnCapabilities.DN_ENERGY, facing);
    }

    public static IDnEnergyStorage getDnEnergyStorage(TileEntity tile, EnumFacing facing) {
        return tile == null ? null : tile.getCapability(DnCapabilities.DN_ENERGY, facing);
    }

    public static IDnEnergyStorage getDnEnergyStorage(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.getCapability(DnCapabilities.DN_ENERGY, null);
    }

    public static BigInteger getEnergyStored(Object target) {
        IDnEnergyStorage storage = getStorage(target);
        return storage == null ? BigInteger.ZERO : storage.getEnergyStoredBig();
    }

    public static BigInteger receive(Object target, BigInteger amount, boolean simulate) {
        IDnEnergyStorage storage = getStorage(target);
        return storage == null ? BigInteger.ZERO : storage.receiveEnergyBig(amount, simulate);
    }

    public static BigInteger extract(Object target, BigInteger amount, boolean simulate) {
        IDnEnergyStorage storage = getStorage(target);
        return storage == null ? BigInteger.ZERO : storage.extractEnergyBig(amount, simulate);
    }

    private static IDnEnergyStorage getStorage(Object target) {
        if (target instanceof IDnEnergyStorage) {
            return (IDnEnergyStorage) target;
        }
        if (target instanceof TileEntity) {
            return getDnEnergyStorage((TileEntity) target, null);
        }
        if (target instanceof ItemStack) {
            return getDnEnergyStorage((ItemStack) target);
        }
        return null;
    }
}
