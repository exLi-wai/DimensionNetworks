package com.lw.DimensionNetworks.item;

import java.math.BigInteger;
import java.util.List;

import com.lw.DimensionNetworks.Tags;
import com.lw.DimensionNetworks.capability.DnEnergyStorage;
import com.lw.DimensionNetworks.util.EnergyFormat;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ItemUltimateBattery extends ItemDnEnergyBase {

    public static final String NAME = "ultimate_battery";
    private static final BigInteger CAPACITY = DnEnergyStorage.DEFAULT_LIMIT;

    public ItemUltimateBattery() {
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
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, World worldIn, List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(EnergyFormat.formatFe(getStored(stack)) + " / " + EnergyFormat.formatFe(getCapacity(stack)));
        tooltip.add(I18n.format("item.DnUltimateBattery.Tooltips"));
    }
}
