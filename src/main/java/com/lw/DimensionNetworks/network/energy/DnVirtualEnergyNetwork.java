package com.lw.DimensionNetworks.network.energy;

import java.math.BigInteger;

import com.lw.DimensionNetworks.capability.DnEnergyStorage;
import com.lw.DimensionNetworks.util.BigIntegerNbt;

import net.minecraft.nbt.NBTTagCompound;

public class DnVirtualEnergyNetwork extends DnEnergyStorage {

    private final DnVirtualEnergyWorldData owner;
    private final String key;
    private final BigInteger transferLimit;
    private BigInteger energy = BigInteger.ZERO;

    public DnVirtualEnergyNetwork(DnVirtualEnergyWorldData owner, String key, BigInteger capacity, BigInteger transferLimit) {
        super(capacity, transferLimit, transferLimit);
        this.owner = owner;
        this.key = key;
        this.transferLimit = transferLimit == null || transferLimit.signum() < 0 ? BigInteger.ZERO : transferLimit;
    }

    public String getKey() {
        return key;
    }

    @Override
    protected void onContentsChanged() {
        owner.markDirty();
    }

    @Override
    public long receiveEnergy(long maxReceive, boolean simulate) {
        return DnEnergyStorage.clampToLong(receiveEnergyBig(BigInteger.valueOf(Math.max(0L, maxReceive)), simulate));
    }

    @Override
    public long extractEnergy(long maxExtract, boolean simulate) {
        return DnEnergyStorage.clampToLong(extractEnergyBig(BigInteger.valueOf(Math.max(0L, maxExtract)), simulate));
    }

    @Override
    public BigInteger receiveEnergyBig(BigInteger maxReceive, boolean simulate) {
        BigInteger request = positiveOrZero(maxReceive);
        if (request.signum() == 0 || !canReceive()) {
            return BigInteger.ZERO;
        }

        BigInteger received = request.min(transferLimit);
        if (!simulate && received.signum() > 0) {
            energy = energy.add(received);
            onContentsChanged();
        }
        return received;
    }

    @Override
    public BigInteger extractEnergyBig(BigInteger maxExtract, boolean simulate) {
        BigInteger request = positiveOrZero(maxExtract);
        if (request.signum() == 0 || !canExtract()) {
            return BigInteger.ZERO;
        }

        BigInteger extracted = request.min(transferLimit).min(energy);
        if (!simulate && extracted.signum() > 0) {
            energy = energy.subtract(extracted);
            onContentsChanged();
        }
        return extracted;
    }

    @Override
    public long getEnergyStoredLong() {
        return DnEnergyStorage.clampToLong(energy);
    }

    @Override
    public long getMaxEnergyStoredLong() {
        return Long.MAX_VALUE;
    }

    @Override
    public long getReceiveLimitLong() {
        return DnEnergyStorage.clampToLong(transferLimit);
    }

    @Override
    public long getExtractLimitLong() {
        return DnEnergyStorage.clampToLong(transferLimit);
    }

    @Override
    public BigInteger getEnergyStoredBig() {
        return energy;
    }

    @Override
    public BigInteger getMaxEnergyStoredBig() {
        return DnEnergyStorage.DEFAULT_LIMIT;
    }

    @Override
    public BigInteger getReceiveLimitBig() {
        return transferLimit;
    }

    @Override
    public BigInteger getExtractLimitBig() {
        return transferLimit;
    }

    @Override
    public boolean canReceive() {
        return transferLimit.signum() > 0;
    }

    @Override
    public boolean canExtract() {
        return transferLimit.signum() > 0 && energy.signum() > 0;
    }

    @Override
    public boolean isEmpty() {
        return energy.signum() == 0;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    public void readNetworkNbt(NBTTagCompound nbt) {
        energy = positiveOrZero(BigIntegerNbt.read(nbt, "DnEnergy", energy));
    }

    public NBTTagCompound writeNetworkNbt() {
        NBTTagCompound nbt = new NBTTagCompound();
        BigIntegerNbt.write(nbt, "DnEnergy", energy);
        BigIntegerNbt.write(nbt, "DnCapacity", DnEnergyStorage.DEFAULT_LIMIT);
        BigIntegerNbt.write(nbt, "DnReceiveLimit", transferLimit);
        BigIntegerNbt.write(nbt, "DnExtractLimit", transferLimit);
        return nbt;
    }

    private static BigInteger positiveOrZero(BigInteger value) {
        return value == null || value.signum() < 0 ? BigInteger.ZERO : value;
    }
}
