package com.lw.DimensionNetworks.network.energy;

import java.math.BigInteger;

import com.lw.DimensionNetworks.capability.DnEnergyStorage;

import net.minecraft.nbt.NBTTagCompound;

public class DnVirtualEnergyNetwork extends DnEnergyStorage {

    private final DnVirtualEnergyWorldData owner;
    private final String key;

    public DnVirtualEnergyNetwork(DnVirtualEnergyWorldData owner, String key, BigInteger capacity, BigInteger transferLimit) {
        super(capacity, transferLimit, transferLimit);
        this.owner = owner;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    protected void onContentsChanged() {
        owner.markDirty();
    }

    public void readNetworkNbt(NBTTagCompound nbt) {
        readFromNbt(nbt);
    }

    public NBTTagCompound writeNetworkNbt() {
        return writeToNbt(new NBTTagCompound());
    }
}
