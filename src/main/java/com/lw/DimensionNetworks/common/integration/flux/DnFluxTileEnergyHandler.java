package com.lw.DimensionNetworks.common.integration.flux;

import javax.annotation.Nonnull;

import com.lw.DimensionNetworks.api.energy.IDnEnergyStorage;
import com.lw.DimensionNetworks.common.tile.TileDnBattery;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.jspecify.annotations.NonNull;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;

public class DnFluxTileEnergyHandler implements ITileEnergyHandler {

    public static final DnFluxTileEnergyHandler INSTANCE = new DnFluxTileEnergyHandler();

    @Override
    public boolean hasCapability(@Nonnull TileEntity tile, EnumFacing dir) {
        return !tile.isInvalid() && tile instanceof TileDnBattery;
    }

    @Override
    public boolean canAddEnergy(@NonNull TileEntity tile, EnumFacing dir) {
        IDnEnergyStorage storage = getStorage(tile);
        return storage != null && storage.canReceive();
    }

    @Override
    public boolean canRemoveEnergy(@NonNull TileEntity tile, EnumFacing dir) {
        IDnEnergyStorage storage = getStorage(tile);
        return storage != null && storage.canExtract();
    }

    @Override
    public long addEnergy(long add, @NonNull TileEntity tile, EnumFacing dir, boolean simulate) {
        IDnEnergyStorage storage = getStorage(tile);
        return storage == null ? 0L : storage.receiveEnergy(Math.max(0L, add), simulate);
    }

    @Override
    public long removeEnergy(long remove, @NonNull TileEntity tile, EnumFacing dir) {
        IDnEnergyStorage storage = getStorage(tile);
        return storage == null ? 0L : storage.extractEnergy(Math.max(0L, remove), false);
    }

    private static IDnEnergyStorage getStorage(TileEntity tile) {
        return tile instanceof TileDnBattery ? ((TileDnBattery) tile).getDnStorage() : null;
    }
}
