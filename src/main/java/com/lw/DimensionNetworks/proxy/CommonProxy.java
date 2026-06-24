package com.lw.DimensionNetworks.proxy;

import com.lw.DimensionNetworks.Tags;
import com.lw.DimensionNetworks.block.BlockDnBattery;
import com.lw.DimensionNetworks.capability.DnCapabilities;
import com.lw.DimensionNetworks.compat.flux.DnFluxNetworksIntegration;
import com.lw.DimensionNetworks.item.ItemBlockDnBattery;
import com.lw.DimensionNetworks.item.ItemDnBattery;
import com.lw.DimensionNetworks.tile.TileDnBattery;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@EventBusSubscriber(modid = Tags.MOD_ID)
public class CommonProxy implements IProxy {

    public static final BlockDnBattery DN_BATTERY_BLOCK = new BlockDnBattery();
    public static final ItemDnBattery DN_BATTERY_ITEM = new ItemDnBattery();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerCapabilities();
        registerTileEntities();
        registerCompat();
    }

    protected void registerCapabilities() {
        DnCapabilities.register();
    }

    protected void registerTileEntities() {
        GameRegistry.registerTileEntity(TileDnBattery.class, new ResourceLocation(Tags.MOD_ID, BlockDnBattery.NAME));
    }

    protected void registerCompat() {
        if (Loader.isModLoaded("fluxnetworks")) {
            DnFluxNetworksIntegration.preInit();
        }
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(DN_BATTERY_BLOCK);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlockDnBattery(DN_BATTERY_BLOCK));
        event.getRegistry().register(DN_BATTERY_ITEM);
    }
}
