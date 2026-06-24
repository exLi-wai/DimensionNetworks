package com.lw.DimensionNetworks.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

import java.util.Objects;

public class ItemBlockDnBattery extends ItemBlock {

    public ItemBlockDnBattery(Block block) {
        super(block);
        setRegistryName(Objects.requireNonNull(block.getRegistryName()));
    }
}
