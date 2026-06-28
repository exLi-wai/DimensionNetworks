package com.lw.DimensionNetworks.block;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.lw.DimensionNetworks.Tags;
import com.lw.DimensionNetworks.network.energy.DnVirtualNetworkKeys;
import com.lw.DimensionNetworks.tile.TileDnBattery;
import com.lw.DimensionNetworks.util.EnergyFormat;
import com.mojang.authlib.GameProfile;

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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Loader;

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
                String capacity = EnergyFormat.formatCapacityFe(battery.getDnStorage().getMaxEnergyStoredBig());
                String networkName = getNetworkDisplayName(playerIn, battery.getNetworkKey());
                playerIn.sendMessage(new TextComponentTranslation("message.dimensionnetworks.network_storage", energy, capacity));
                playerIn.sendMessage(new TextComponentTranslation("message.dimensionnetworks.network_key", networkName));
            }
        }
        return true;
    }

    private static String getNetworkDisplayName(EntityPlayer player, String fallback) {
        if (!Loader.isModLoaded("ftbutilities") && !Loader.isModLoaded("ftblib")) {
            return fallback;
        }

        String teamName = getCurrentFtbTeamName(player);
        return isBlank(teamName) ? fallback : teamName;
    }

    private static String getCurrentFtbTeamName(EntityPlayer player) {
        GameProfile profile = player.getGameProfile();
        if (profile == null) {
            return null;
        }

        try {
            if (!Universe.loaded()) {
                return null;
            }

            Universe universe = Universe.get();
            ForgePlayer forgePlayer = universe == null ? null : universe.getPlayer(profile);
            ForgeTeam team = forgePlayer == null ? null : forgePlayer.team;
            return getTeamDisplayName(team);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static String getTeamDisplayName(ForgeTeam team) {
        if (team == null) {
            return null;
        }

        ITextComponent title = team.getTitle();
        if (title != null && !isBlank(title.getUnformattedText())) {
            return title.getUnformattedText();
        }

        String id = team.getId();
        if (!isBlank(id)) {
            return id;
        }

        return team.getUIDCode();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
