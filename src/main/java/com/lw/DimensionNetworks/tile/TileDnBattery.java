package com.lw.DimensionNetworks.tile;

import java.math.BigInteger;

import com.lw.DimensionNetworks.api.energy.IDnEnergyStorage;
import com.lw.DimensionNetworks.capability.DnCapabilities;
import com.lw.DimensionNetworks.capability.DnEnergyStorage;
import com.lw.DimensionNetworks.capability.FeEnergyAdapter;
import com.lw.DimensionNetworks.integration.flux.DnFluxNetworksIntegration;
import com.lw.DimensionNetworks.network.energy.DnVirtualEnergyNetwork;
import com.lw.DimensionNetworks.network.energy.DnVirtualEnergyWorldData;
import com.lw.DimensionNetworks.network.energy.DnVirtualNetworkKeys;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileDnBattery extends TileEntity implements ITickable {

    private final IDnEnergyStorage networkStorage = new NetworkStorageProxy();
    private final IEnergyStorage feAdapter = new FeEnergyAdapter(networkStorage);

    private String networkKey;

    public void setNetworkKey(String networkKey) {
        this.networkKey = networkKey;
        markDirty();
    }

    public String getNetworkKey() {
        if (networkKey == null || networkKey.isEmpty()) {
            if (world == null || pos == null) {
                return "global";
            }
            networkKey = DnVirtualNetworkKeys.forBlock(world, pos);
        }
        return networkKey;
    }

    public IDnEnergyStorage getDnStorage() {
        return networkStorage;
    }

    public IEnergyStorage getFeAdapter() {
        return feAdapter;
    }

    @Override
    public void update() {
        if (world == null || world.isRemote || networkStorage.isEmpty()) {
            return;
        }
        outputEnergyToAdjacentTiles();
    }

    private void outputEnergyToAdjacentTiles() {
        long remaining = networkStorage.getExtractLimitLong();
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (remaining <= 0L || networkStorage.isEmpty()) {
                return;
            }
            remaining -= pushEnergy(facing, remaining);
        }
    }

    private long pushEnergy(EnumFacing facing, long maxAmount) {
        TileEntity target = world.getTileEntity(pos.offset(facing));
        if (target == null || target.isInvalid()) {
            return 0L;
        }

        long pushedToFlux = pushEnergyToFluxPlug(target, facing, maxAmount);
        return pushedToFlux > 0L ? pushedToFlux : pushEnergyToForgeReceiver(target, facing, maxAmount);
    }

    private long pushEnergyToFluxPlug(TileEntity target, EnumFacing facing, long maxAmount) {
        if (!Loader.isModLoaded("fluxnetworks")) {
            return 0L;
        }
        return DnFluxNetworksIntegration.pushToFluxPlug(this, target, facing, maxAmount);
    }

    private long pushEnergyToForgeReceiver(TileEntity target, EnumFacing facing, long maxAmount) {
        EnumFacing targetSide = facing.getOpposite();
        if (!target.hasCapability(CapabilityEnergy.ENERGY, targetSide)) {
            return 0L;
        }

        IEnergyStorage receiver = target.getCapability(CapabilityEnergy.ENERGY, targetSide);
        if (receiver == null || !receiver.canReceive()) {
            return 0L;
        }

        int request = receiver.receiveEnergy((int) Math.min(Integer.MAX_VALUE, maxAmount), true);
        if (request <= 0) {
            return 0L;
        }

        long extractable = networkStorage.extractEnergy(request, true);
        if (extractable <= 0L) {
            return 0L;
        }

        long extracted = networkStorage.extractEnergy(extractable, false);
        if (extracted <= 0L) {
            return 0L;
        }

        int inserted = receiver.receiveEnergy((int) Math.min(Integer.MAX_VALUE, extracted), false);
        if (inserted < extracted) {
            networkStorage.receiveEnergy(extracted - inserted, false);
        }
        return inserted;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == DnCapabilities.DN_ENERGY
                || capability == CapabilityEnergy.ENERGY
                || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == DnCapabilities.DN_ENERGY) {
            return DnCapabilities.DN_ENERGY.cast(networkStorage);
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(feAdapter);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("DnNetworkKey", getNetworkKey());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        networkKey = compound.getString("DnNetworkKey");
    }

    private DnVirtualEnergyNetwork getNetwork() {
        if (world == null) {
            return null;
        }
        return DnVirtualEnergyWorldData.get(world).getNetwork(getNetworkKey());
    }

    private final class NetworkStorageProxy implements IDnEnergyStorage {

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
            DnVirtualEnergyNetwork network = getNetwork();
            return network == null ? BigInteger.ZERO : network.receiveEnergyBig(maxReceive, simulate);
        }

        @Override
        public BigInteger extractEnergyBig(BigInteger maxExtract, boolean simulate) {
            DnVirtualEnergyNetwork network = getNetwork();
            return network == null ? BigInteger.ZERO : network.extractEnergyBig(maxExtract, simulate);
        }

        @Override
        public long getEnergyStoredLong() {
            return DnEnergyStorage.clampToLong(getEnergyStoredBig());
        }

        @Override
        public long getMaxEnergyStoredLong() {
            return DnEnergyStorage.clampToLong(getMaxEnergyStoredBig());
        }

        @Override
        public long getReceiveLimitLong() {
            return DnEnergyStorage.clampToLong(getReceiveLimitBig());
        }

        @Override
        public long getExtractLimitLong() {
            return DnEnergyStorage.clampToLong(getExtractLimitBig());
        }

        @Override
        public BigInteger getEnergyStoredBig() {
            DnVirtualEnergyNetwork network = getNetwork();
            return network == null ? BigInteger.ZERO : network.getEnergyStoredBig();
        }

        @Override
        public BigInteger getMaxEnergyStoredBig() {
            DnVirtualEnergyNetwork network = getNetwork();
            return network == null ? BigInteger.ZERO : network.getMaxEnergyStoredBig();
        }

        @Override
        public BigInteger getReceiveLimitBig() {
            DnVirtualEnergyNetwork network = getNetwork();
            return network == null ? BigInteger.ZERO : network.getReceiveLimitBig();
        }

        @Override
        public BigInteger getExtractLimitBig() {
            DnVirtualEnergyNetwork network = getNetwork();
            return network == null ? BigInteger.ZERO : network.getExtractLimitBig();
        }

        @Override
        public boolean canReceive() {
            DnVirtualEnergyNetwork network = getNetwork();
            return network != null && network.canReceive();
        }

        @Override
        public boolean canExtract() {
            DnVirtualEnergyNetwork network = getNetwork();
            return network != null && network.canExtract();
        }

        @Override
        public boolean isEmpty() {
            DnVirtualEnergyNetwork network = getNetwork();
            return network == null || network.isEmpty();
        }

        @Override
        public boolean isFull() {
            DnVirtualEnergyNetwork network = getNetwork();
            return network != null && network.isFull();
        }
    }
}
