package com.lw.DimensionNetworks.common.energy;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.lw.DimensionNetworks.common.capability.DnEnergyStorage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class DnVirtualEnergyWorldData extends WorldSavedData {

    private static final String DATA_NAME = "dimensionnetworks_virtual_energy";
    private static final BigInteger DEFAULT_CAPACITY = DnEnergyStorage.DEFAULT_LIMIT;
    private static final BigInteger DEFAULT_TRANSFER_LIMIT = BigInteger.valueOf(Long.MAX_VALUE);

    private final Map<String, DnVirtualEnergyNetwork> networks = new HashMap<String, DnVirtualEnergyNetwork>();

    public DnVirtualEnergyWorldData() {
        super(DATA_NAME);
    }

    public DnVirtualEnergyWorldData(String name) {
        super(name);
    }

    public static DnVirtualEnergyWorldData get(World world) {
        MapStorage storage = world.getMapStorage();
        DnVirtualEnergyWorldData data = (DnVirtualEnergyWorldData) storage.getOrLoadData(DnVirtualEnergyWorldData.class, DATA_NAME);
        if (data == null) {
            data = new DnVirtualEnergyWorldData();
            storage.setData(DATA_NAME, data);
        }
        return data;
    }

    public DnVirtualEnergyNetwork getNetwork(String key) {
        String safeKey = key == null || key.isEmpty() ? "global" : key;
        DnVirtualEnergyNetwork network = networks.get(safeKey);
        if (network == null) {
            network = new DnVirtualEnergyNetwork(this, safeKey, DEFAULT_CAPACITY, DEFAULT_TRANSFER_LIMIT);
            networks.put(safeKey, network);
            markDirty();
        }
        return network;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        networks.clear();
        NBTTagCompound networkTags = nbt.getCompoundTag("Networks");
        for (String key : networkTags.getKeySet()) {
            DnVirtualEnergyNetwork network = new DnVirtualEnergyNetwork(this, key, DEFAULT_CAPACITY, DEFAULT_TRANSFER_LIMIT);
            network.readNetworkNbt(networkTags.getCompoundTag(key));
            networks.put(key, network);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound networkTags = new NBTTagCompound();
        for (Entry<String, DnVirtualEnergyNetwork> entry : networks.entrySet()) {
            networkTags.setTag(entry.getKey(), entry.getValue().writeNetworkNbt());
        }
        compound.setTag("Networks", networkTags);
        return compound;
    }
}
