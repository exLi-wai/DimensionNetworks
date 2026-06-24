package ic2.api.energy.event;

import ic2.api.energy.tile.IEnergyTile;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Minimal IC2 API stub for legacy mods that hard-link optional IC2 support.
 */
public class EnergyTileEvent extends Event {
    public final IEnergyTile tile;

    public EnergyTileEvent(IEnergyTile tile) {
        this.tile = tile;
    }
}
