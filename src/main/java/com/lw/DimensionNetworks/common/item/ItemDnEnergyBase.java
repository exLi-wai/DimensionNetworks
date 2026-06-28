package com.lw.DimensionNetworks.common.item;

import java.math.BigInteger;

import com.lw.DimensionNetworks.api.energy.IDnEnergyStorage;
import com.lw.DimensionNetworks.common.capability.DnCapabilities;
import com.lw.DimensionNetworks.common.capability.DnEnergyProvider;
import com.lw.DimensionNetworks.common.capability.DnEnergyStorage;
import com.lw.DimensionNetworks.util.BigIntegerNbt;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;

public abstract class ItemDnEnergyBase extends Item {

    public abstract BigInteger getCapacity(ItemStack stack);

    public BigInteger getStored(ItemStack stack) {
        NBTTagCompound tag = getOrCreateTag(stack);
        return BigIntegerNbt.read(tag, "DnEnergy", BigInteger.ZERO).max(BigInteger.ZERO).min(getCapacity(stack));
    }

    public void setStored(ItemStack stack, BigInteger value) {
        BigInteger stored = value == null ? BigInteger.ZERO : value.max(BigInteger.ZERO).min(getCapacity(stack));
        BigIntegerNbt.write(getOrCreateTag(stack), "DnEnergy", stored);
    }

    public IDnEnergyStorage getEnergyStorage(ItemStack stack) {
        return stack.getCapability(DnCapabilities.DN_ENERGY, null);
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull final ItemStack stack, NBTTagCompound nbt) {
        BigInteger capacity = getCapacity(stack);
        DnEnergyStorage storage = new DnEnergyStorage(capacity, capacity, capacity) {
            @Override
            protected void onContentsChanged() {
                setStored(stack, getEnergyStoredBig());
            }
        };
        storage.setEnergyStored(getStored(stack));
        return new DnEnergyProvider(storage);
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }
}
