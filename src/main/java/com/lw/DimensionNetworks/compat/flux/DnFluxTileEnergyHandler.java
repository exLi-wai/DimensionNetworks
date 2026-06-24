package com.lw.DimensionNetworks.compat.flux;

import javax.annotation.Nonnull;

import com.lw.DimensionNetworks.api.energy.IDnEnergyStorage;
import com.lw.DimensionNetworks.tile.TileDnBattery;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;

public class DnFluxTileEnergyHandler implements ITileEnergyHandler {

    public static final DnFluxTileEnergyHandler INSTANCE = new DnFluxTileEnergyHandler();

    @Override
    public boolean hasCapability(@Nonnull TileEntity tile, EnumFacing dir) {
        return !tile.isInvalid() && tile instanceof TileDnBattery;
    }

    @Override
    public boolean canAddEnergy(TileEntity tile, EnumFacing dir) {
        IDnEnergyStorage storage = getStorage(tile);
        return storage != null && storage.canReceive();
    }

    @Override
    public boolean canRemoveEnergy(TileEntity tile, EnumFacing dir) {
        IDnEnergyStorage storage = getStorage(tile);
        return storage != null && storage.canExtract();
    }

    @Override
    public long addEnergy(long add, TileEntity tile, EnumFacing dir, boolean simulate) {
        IDnEnergyStorage storage = getStorage(tile);
        return storage == null ? 0L : storage.receiveEnergy(Math.max(0L, add), simulate);
    }

    @Override
    public long removeEnergy(long remove, TileEntity tile, EnumFacing dir) {
        IDnEnergyStorage storage = getStorage(tile);
        return storage == null ? 0L : storage.extractEnergy(Math.max(0L, remove), false);
    }

    private static IDnEnergyStorage getStorage(TileEntity tile) {
        return tile instanceof TileDnBattery ? ((TileDnBattery) tile).getDnStorage() : null;
    }
}
