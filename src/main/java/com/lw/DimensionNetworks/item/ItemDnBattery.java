package com.lw.DimensionNetworks.item;

import java.math.BigInteger;
import java.util.List;

import com.lw.DimensionNetworks.Tags;
import com.lw.DimensionNetworks.util.EnergyFormat;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemDnBattery extends ItemDnEnergyBase {

    public static final String NAME = "dn_battery_item";
    private static final BigInteger CAPACITY = new BigInteger("1000000000000000000");

    public ItemDnBattery() {
        setRegistryName(Tags.MOD_ID, NAME);
        setTranslationKey(Tags.MOD_ID + "." + NAME);
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public BigInteger getCapacity(ItemStack stack) {
        return CAPACITY;
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(EnergyFormat.formatFe(getStored(stack)) + " / " + EnergyFormat.formatFe(getCapacity(stack)));
    }
}
