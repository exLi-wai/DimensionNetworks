package com.lw.DimensionNetworks.capability;

import com.lw.DimensionNetworks.api.energy.IDnEnergyStorage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jspecify.annotations.NonNull;

public class DnEnergyProvider implements ICapabilitySerializable<NBTTagCompound> {

    protected final IDnEnergyStorage dnStorage;
    protected final IEnergyStorage feAdapter;

    public DnEnergyProvider(DnEnergyStorage storage) {
        this.dnStorage = storage;
        this.feAdapter = new FeEnergyAdapter(storage);
    }

    @Override
    public boolean hasCapability(@NonNull Capability<?> capability, EnumFacing facing) {
        return capability == DnCapabilities.DN_ENERGY || capability == CapabilityEnergy.ENERGY;
    }

    @Override
    public <T> T getCapability(@NonNull Capability<T> capability, EnumFacing facing) {
        if (capability == DnCapabilities.DN_ENERGY) {
            return DnCapabilities.DN_ENERGY.cast(dnStorage);
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(feAdapter);
        }
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        if (dnStorage instanceof DnEnergyStorage) {
            ((DnEnergyStorage) dnStorage).writeToNbt(nbt);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (dnStorage instanceof DnEnergyStorage) {
            ((DnEnergyStorage) dnStorage).readFromNbt(nbt);
        }
    }
}
