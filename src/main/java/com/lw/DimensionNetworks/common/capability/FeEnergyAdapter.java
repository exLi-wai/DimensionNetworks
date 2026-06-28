package com.lw.DimensionNetworks.common.capability;

import java.math.BigInteger;

import com.lw.DimensionNetworks.api.energy.IDnEnergyStorage;

import net.minecraftforge.energy.IEnergyStorage;

public final class FeEnergyAdapter implements IEnergyStorage {

    private final IDnEnergyStorage storage;

    public FeEnergyAdapter(IDnEnergyStorage storage) {
        this.storage = storage;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) {
            return 0;
        }
        return DnEnergyStorage.clampToInt(storage.receiveEnergyBig(BigInteger.valueOf(maxReceive), simulate));
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract <= 0) {
            return 0;
        }
        return DnEnergyStorage.clampToInt(storage.extractEnergyBig(BigInteger.valueOf(maxExtract), simulate));
    }

    @Override
    public int getEnergyStored() {
        return DnEnergyStorage.clampToInt(storage.getEnergyStoredBig());
    }

    @Override
    public int getMaxEnergyStored() {
        return DnEnergyStorage.clampToInt(storage.getMaxEnergyStoredBig());
    }

    @Override
    public boolean canExtract() {
        return storage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return storage.canReceive();
    }
}
