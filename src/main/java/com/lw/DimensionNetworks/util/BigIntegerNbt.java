package com.lw.DimensionNetworks.util;

import java.math.BigInteger;

import net.minecraft.nbt.NBTTagCompound;

public final class BigIntegerNbt {

    public static final int DEFAULT_CHUNK_SIZE = 16 * 1024;
    public static final int DEFAULT_MAX_BYTES = 1024 * 1024;

    private static final String CHUNK_COUNT_SUFFIX = "_ChunkCount";
    private static final String CHUNK_SUFFIX = "_Chunk";

    private BigIntegerNbt() {
    }

    public static void write(NBTTagCompound nbt, String key, BigInteger value) {
        write(nbt, key, value, DEFAULT_CHUNK_SIZE, DEFAULT_MAX_BYTES);
    }

    public static void write(NBTTagCompound nbt, String key, BigInteger value, int chunkSize, int maxBytes) {
        if (nbt == null || key == null || value == null || chunkSize <= 0 || maxBytes <= 0) {
            return;
        }

        clear(nbt, key);

        byte[] bytes = value.toByteArray();
        if (bytes.length > maxBytes) {
            throw new IllegalArgumentException("BigInteger NBT value for " + key + " exceeds " + maxBytes + " bytes");
        }

        if (bytes.length <= chunkSize) {
            nbt.setByteArray(key, bytes);
            return;
        }

        int chunkCount = (bytes.length + chunkSize - 1) / chunkSize;
        nbt.setInteger(key + CHUNK_COUNT_SUFFIX, chunkCount);
        for (int index = 0; index < chunkCount; index++) {
            int start = index * chunkSize;
            int length = Math.min(chunkSize, bytes.length - start);
            byte[] chunk = new byte[length];
            System.arraycopy(bytes, start, chunk, 0, length);
            nbt.setByteArray(key + CHUNK_SUFFIX + index, chunk);
        }
    }

    public static BigInteger read(NBTTagCompound nbt, String key, BigInteger defaultValue) {
        return read(nbt, key, defaultValue, DEFAULT_MAX_BYTES);
    }

    public static BigInteger read(NBTTagCompound nbt, String key, BigInteger defaultValue, int maxBytes) {
        if (nbt == null || key == null || defaultValue == null || maxBytes <= 0) {
            return defaultValue;
        }

        try {
            if (nbt.hasKey(key)) {
                byte[] bytes = nbt.getByteArray(key);
                if (bytes.length > maxBytes) {
                    return defaultValue;
                }
                return bytes.length == 0 ? BigInteger.ZERO : new BigInteger(bytes);
            }

            int chunkCount = nbt.getInteger(key + CHUNK_COUNT_SUFFIX);
            if (chunkCount <= 0) {
                return defaultValue;
            }

            int total = 0;
            byte[][] chunks = new byte[chunkCount][];
            for (int index = 0; index < chunkCount; index++) {
                byte[] chunk = nbt.getByteArray(key + CHUNK_SUFFIX + index);
                total += chunk.length;
                if (total > maxBytes) {
                    return defaultValue;
                }
                chunks[index] = chunk;
            }

            byte[] bytes = new byte[total];
            int offset = 0;
            for (byte[] chunk : chunks) {
                System.arraycopy(chunk, 0, bytes, offset, chunk.length);
                offset += chunk.length;
            }
            return bytes.length == 0 ? BigInteger.ZERO : new BigInteger(bytes);
        } catch (RuntimeException ignored) {
            return defaultValue;
        }
    }

    public static void clear(NBTTagCompound nbt, String key) {
        if (nbt == null || key == null) {
            return;
        }

        nbt.removeTag(key);
        int chunkCount = nbt.getInteger(key + CHUNK_COUNT_SUFFIX);
        nbt.removeTag(key + CHUNK_COUNT_SUFFIX);
        for (int index = 0; index < chunkCount; index++) {
            nbt.removeTag(key + CHUNK_SUFFIX + index);
        }
    }
}
