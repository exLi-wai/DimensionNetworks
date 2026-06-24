package ic2.api.energy.tile;

import net.minecraft.util.EnumFacing;

public interface IEnergyEmitter extends IEnergyTile {
    boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side);
}
