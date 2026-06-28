package com.lw.DimensionNetworks.common.tile;

import com.lw.DimensionNetworks.api.energy.IDnEnergyStorage;
import com.lw.DimensionNetworks.common.capability.DnCapabilities;
import com.lw.DimensionNetworks.common.capability.DnEnergyStorage;
import com.lw.DimensionNetworks.common.capability.FeEnergyAdapter;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

public abstract class TileDnEnergyBase extends TileEntity {

    protected final DnEnergyStorage dnStorage;
    protected final IEnergyStorage feAdapter;

    protected TileDnEnergyBase() {
        this.dnStorage = createEnergyStorage();
        this.feAdapter = new FeEnergyAdapter(dnStorage);
    }

    protected DnEnergyStorage createEnergyStorage() {
        return new DnEnergyStorage() {
            @Override
            protected void onContentsChanged() {
                TileDnEnergyBase.this.markDirty();
            }
        };
    }

    public IDnEnergyStorage getDnStorage() {
        return dnStorage;
    }

    public IEnergyStorage getFeAdapter() {
        return feAdapter;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return capability == DnCapabilities.DN_ENERGY || capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == DnCapabilities.DN_ENERGY) {
            return DnCapabilities.DN_ENERGY.cast(dnStorage);
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(feAdapter);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);
        dnStorage.writeToNbt(compound);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        dnStorage.readFromNbt(compound);
    }
}
