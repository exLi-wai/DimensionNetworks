package com.lw.DimensionNetworks.block;

import com.lw.DimensionNetworks.Tags;
import com.lw.DimensionNetworks.tile.TileDnBattery;
import com.lw.DimensionNetworks.util.EnergyFormat;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.creativetab.CreativeTabs;

import com.lw.DimensionNetworks.network.energy.DnVirtualNetworkKeys;

import javax.annotation.Nonnull;

public class BlockDnBattery extends Block implements ITileEntityProvider {

    public static final String NAME = "dn_network_storage";

    public BlockDnBattery() {
        super(Material.IRON);
        setRegistryName(Tags.MOD_ID, NAME);
        setTranslationKey(Tags.MOD_ID + "." + NAME);
        setCreativeTab(CreativeTabs.MISC);
        setHardness(3.0F);
        setResistance(10.0F);
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileDnBattery();
    }

    @Override
    public boolean hasTileEntity(@Nonnull IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, IBlockState state) {
        return new TileDnBattery();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileDnBattery) {
                String key = placer instanceof EntityPlayer
                        ? DnVirtualNetworkKeys.forPlayer(worldIn, (EntityPlayer) placer)
                        : DnVirtualNetworkKeys.forBlock(worldIn, pos);
                ((TileDnBattery) tile).setNetworkKey(key);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileDnBattery battery) {
                String energy = EnergyFormat.formatFe(battery.getDnStorage().getEnergyStoredBig());
                String capacity = EnergyFormat.formatFe(battery.getDnStorage().getMaxEnergyStoredBig());
                playerIn.sendMessage(new TextComponentString("DN Network Storage: " + energy + " / " + capacity));
                playerIn.sendMessage(new TextComponentString("Network: " + battery.getNetworkKey()));
            }
        }
        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
