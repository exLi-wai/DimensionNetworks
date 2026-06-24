package com.lw.DimensionNetworks.api.energy;

import java.math.BigInteger;

public interface IDnBigEnergyStorage {

    BigInteger receiveEnergyBig(BigInteger maxReceive, boolean simulate);

    BigInteger extractEnergyBig(BigInteger maxExtract, boolean simulate);

    BigInteger getEnergyStoredBig();

    BigInteger getMaxEnergyStoredBig();

    BigInteger getReceiveLimitBig();

    BigInteger getExtractLimitBig();

    boolean isEmpty();

    boolean isFull();
}
