package com.lw.DimensionNetworks.common.integration.flux;

import com.lw.DimensionNetworks.DimensionNetworks;
import com.lw.DimensionNetworks.common.tile.TileDnBattery;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.fluxnetworks.api.network.ITransferHandler;
import sonar.fluxnetworks.api.tiles.IFluxPlug;
import sonar.fluxnetworks.common.handler.TileEntityHandler;

public final class DnFluxNetworksIntegration {

    private DnFluxNetworksIntegration() {
    }

    public static void preInit() {
        TileEntityHandler.tileEnergyHandlers.remove(DnFluxTileEnergyHandler.INSTANCE);
        TileEntityHandler.tileEnergyHandlers.add(0, DnFluxTileEnergyHandler.INSTANCE);
        DimensionNetworks.LOGGER.info("Flux Networks integration enabled: registered DN long energy handler");
    }

    public static long pushToFluxPlug(TileDnBattery source, TileEntity target, EnumFacing sourceToTarget, long maxAmount) {
        if (!(target instanceof IFluxPlug) || maxAmount <= 0L) {
            return 0L;
        }

        IFluxPlug plug = (IFluxPlug) target;
        if (!plug.isActive()) {
            return 0L;
        }

        ITransferHandler transferHandler = plug.getTransferHandler();
        EnumFacing plugSide = sourceToTarget.getOpposite();
        long accepted = transferHandler.receiveFromSupplier(maxAmount, plugSide, true);
        if (accepted <= 0L) {
            return 0L;
        }

        long extractable = source.getDnStorage().extractEnergy(Math.min(maxAmount, accepted), true);
        if (extractable <= 0L) {
            return 0L;
        }

        long extracted = source.getDnStorage().extractEnergy(extractable, false);
        if (extracted <= 0L) {
            return 0L;
        }

        long inserted = transferHandler.receiveFromSupplier(extracted, plugSide, false);
        if (inserted < extracted) {
            source.getDnStorage().receiveEnergy(extracted - inserted, false);
        }
        return inserted;
    }
}
