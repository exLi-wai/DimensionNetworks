package com.lw.DimensionNetworks.common.capability;

import java.math.BigInteger;

import com.lw.DimensionNetworks.api.energy.IDnEnergyStorage;
import com.lw.DimensionNetworks.util.BigIntegerNbt;

import net.minecraft.nbt.NBTTagCompound;

public class DnEnergyStorage implements IDnEnergyStorage {

    public static final BigInteger DEFAULT_LIMIT = BigInteger.valueOf(Long.MAX_VALUE);

    private BigInteger energy;
    private BigInteger capacity;
    private BigInteger receiveLimit;
    private BigInteger extractLimit;

    public DnEnergyStorage() {
        this(DEFAULT_LIMIT, DEFAULT_LIMIT, DEFAULT_LIMIT);
    }

    public DnEnergyStorage(BigInteger capacity, BigInteger receiveLimit, BigInteger extractLimit) {
        this.energy = BigInteger.ZERO;
        this.capacity = positiveOrZero(capacity);
        this.receiveLimit = positiveOrZero(receiveLimit);
        this.extractLimit = positiveOrZero(extractLimit);
    }

    @Override
    public long receiveEnergy(long maxReceive, boolean simulate) {
        return clampToLong(receiveEnergyBig(BigInteger.valueOf(Math.max(0L, maxReceive)), simulate));
    }

    @Override
    public long extractEnergy(long maxExtract, boolean simulate) {
        return clampToLong(extractEnergyBig(BigInteger.valueOf(Math.max(0L, maxExtract)), simulate));
    }

    @Override
    public BigInteger receiveEnergyBig(BigInteger maxReceive, boolean simulate) {
        BigInteger request = positiveOrZero(maxReceive);
        if (request.signum() == 0 || !canReceive()) {
            return BigInteger.ZERO;
        }

        BigInteger space = capacity.subtract(energy).max(BigInteger.ZERO);
        BigInteger received = request.min(receiveLimit).min(space);
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

        BigInteger extracted = request.min(extractLimit).min(energy);
        if (!simulate && extracted.signum() > 0) {
            energy = energy.subtract(extracted);
            onContentsChanged();
        }
        return extracted;
    }

    @Override
    public long getEnergyStoredLong() {
        return clampToLong(energy);
    }

    @Override
    public long getMaxEnergyStoredLong() {
        return clampToLong(capacity);
    }

    @Override
    public long getReceiveLimitLong() {
        return clampToLong(receiveLimit);
    }

    @Override
    public long getExtractLimitLong() {
        return clampToLong(extractLimit);
    }

    @Override
    public BigInteger getEnergyStoredBig() {
        return energy;
    }

    @Override
    public BigInteger getMaxEnergyStoredBig() {
        return capacity;
    }

    @Override
    public BigInteger getReceiveLimitBig() {
        return receiveLimit;
    }

    @Override
    public BigInteger getExtractLimitBig() {
        return extractLimit;
    }

    @Override
    public boolean canReceive() {
        return receiveLimit.signum() > 0 && energy.compareTo(capacity) < 0;
    }

    @Override
    public boolean canExtract() {
        return extractLimit.signum() > 0 && energy.signum() > 0;
    }

    @Override
    public boolean isEmpty() {
        return energy.signum() == 0;
    }

    @Override
    public boolean isFull() {
        return energy.compareTo(capacity) >= 0;
    }

    public void setEnergyStored(BigInteger energy) {
        BigInteger clamped = positiveOrZero(energy).min(capacity);
        if (!this.energy.equals(clamped)) {
            this.energy = clamped;
            onContentsChanged();
        }
    }

    public void setCapacity(BigInteger capacity) {
        this.capacity = positiveOrZero(capacity);
        if (energy.compareTo(this.capacity) > 0) {
            energy = this.capacity;
        }
        onContentsChanged();
    }

    public void setReceiveLimit(BigInteger receiveLimit) {
        this.receiveLimit = positiveOrZero(receiveLimit);
        onContentsChanged();
    }

    public void setExtractLimit(BigInteger extractLimit) {
        this.extractLimit = positiveOrZero(extractLimit);
        onContentsChanged();
    }

    public NBTTagCompound writeToNbt(NBTTagCompound nbt) {
        BigIntegerNbt.write(nbt, "DnEnergy", energy);
        BigIntegerNbt.write(nbt, "DnCapacity", capacity);
        BigIntegerNbt.write(nbt, "DnReceiveLimit", receiveLimit);
        BigIntegerNbt.write(nbt, "DnExtractLimit", extractLimit);
        return nbt;
    }

    public void readFromNbt(NBTTagCompound nbt) {
        capacity = positiveOrZero(BigIntegerNbt.read(nbt, "DnCapacity", capacity));
        receiveLimit = positiveOrZero(BigIntegerNbt.read(nbt, "DnReceiveLimit", receiveLimit));
        extractLimit = positiveOrZero(BigIntegerNbt.read(nbt, "DnExtractLimit", extractLimit));
        energy = positiveOrZero(BigIntegerNbt.read(nbt, "DnEnergy", energy)).min(capacity);
    }

    protected void onContentsChanged() {
    }

    public static long clampToLong(BigInteger value) {
        if (value == null || value.signum() <= 0) {
            return 0L;
        }
        BigInteger max = BigInteger.valueOf(Long.MAX_VALUE);
        return value.compareTo(max) > 0 ? Long.MAX_VALUE : value.longValue();
    }

    public static int clampToInt(BigInteger value) {
        if (value == null || value.signum() <= 0) {
            return 0;
        }
        BigInteger max = BigInteger.valueOf(Integer.MAX_VALUE);
        return value.compareTo(max) > 0 ? Integer.MAX_VALUE : value.intValue();
    }

    private static BigInteger positiveOrZero(BigInteger value) {
        if (value == null || value.signum() < 0) {
            return BigInteger.ZERO;
        }
        return value;
    }
}
