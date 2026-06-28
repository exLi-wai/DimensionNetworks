package com.lw.DimensionNetworks.common.capability;

import java.util.concurrent.Callable;

import com.lw.DimensionNetworks.api.energy.IDnEnergyStorage;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public final class DnCapabilities {

    @CapabilityInject(IDnEnergyStorage.class)
    public static Capability<IDnEnergyStorage> DN_ENERGY = null;

    private DnCapabilities() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IDnEnergyStorage.class, new Capability.IStorage<IDnEnergyStorage>() {
            @Override
            public NBTBase writeNBT(Capability<IDnEnergyStorage> capability, IDnEnergyStorage instance, EnumFacing side) {
                if (instance instanceof DnEnergyStorage) {
                    return ((DnEnergyStorage) instance).writeToNbt(new NBTTagCompound());
                }
                return new NBTTagCompound();
            }

            @Override
            public void readNBT(Capability<IDnEnergyStorage> capability, IDnEnergyStorage instance, EnumFacing side, NBTBase nbt) {
                if (instance instanceof DnEnergyStorage && nbt instanceof NBTTagCompound) {
                    ((DnEnergyStorage) instance).readFromNbt((NBTTagCompound) nbt);
                }
            }
        }, new Callable<IDnEnergyStorage>() {
            @Override
            public IDnEnergyStorage call() {
                return new DnEnergyStorage();
            }
        });
    }
}
