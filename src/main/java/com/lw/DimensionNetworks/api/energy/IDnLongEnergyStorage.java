package com.lw.DimensionNetworks.api.energy;

public interface IDnLongEnergyStorage {

    long receiveEnergy(long maxReceive, boolean simulate);

    long extractEnergy(long maxExtract, boolean simulate);

    long getEnergyStoredLong();

    long getMaxEnergyStoredLong();

    long getReceiveLimitLong();

    long getExtractLimitLong();

    boolean canReceive();

    boolean canExtract();
}
