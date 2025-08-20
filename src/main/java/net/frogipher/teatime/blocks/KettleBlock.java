package net.frogipher.teatime.blocks;

import net.frogipher.teatime.advancement.TeatimeAdvancements;
import net.frogipher.teatime.registry.TeatimeBlockEntities;
import net.frogipher.teatime.registry.TeatimeItems;
import net.frogipher.teatime.registry.TeatimeSoundEvents;
import net.frogipher.teatime.tea.TeaInfusions;
import net.frogipher.teatime.util.TeatimeTags;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class KettleBlock extends BlockWithEntity {
    public static final BooleanProperty LIT = Properties.LIT;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape BODY_SHAPE = Block.createCuboidShape(3, 0, 3, 13, 9, 13);

    public KettleBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(LIT, false)
                .with(FACING, Direction.NORTH));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        if (!world.isClient && placer instanceof ServerPlayerEntity sp) {
            TeatimeAdvancements.onKettlePlaced(sp);
        }
        if (!world.isClient && isHeated(world, pos)) {
            world.setBlockState(pos, state.with(LIT, true), 3);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KettleBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, TeatimeBlockEntities.KETTLE, KettleBlockEntity::tickServer);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            stopWhistle(world, pos);
            world.removeBlockEntity(pos);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        BlockEntity raw = world.getBlockEntity(pos);
        if (!(raw instanceof KettleBlockEntity be)) return ActionResult.PASS;

        ItemStack held = player.getStackInHand(hand);

        if (!be.hasWater() && held.isOf(Items.WATER_BUCKET)) {
            be.setWater(true);

            boolean heated = isHeated(world, pos);
            if (heated) world.setBlockState(pos, state.with(LIT, true), 3);
            be.primeBoil(world.getTime());

            world.playSound(null, pos, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT,
                    SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);

            be.maybeStart(world, pos);

            player.setStackInHand(hand, ItemUsage.exchangeStack(held, player, new ItemStack(Items.BUCKET)));
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1f, 1f);

            be.markDirty();
            return ActionResult.SUCCESS;
        }

        if (be.hasWater() && !be.isBrewed() && TeaInfusions.isIngredient(held.getItem())) {
            be.addIngredient(held.getItem());
            if (!player.getAbilities().creativeMode) held.decrement(1);

            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS,
                    0.6F, 0.95F + world.random.nextFloat() * 0.1F);

            be.maybeStart(world, pos);
            be.markDirty();
            return ActionResult.SUCCESS;
        }

        if (be.isBrewed() && held.isOf(TeatimeItems.TEACUP) && be.getServingsRemaining() > 0) {
            int canServe = Math.min(3, held.getCount());
            int toServe  = Math.min(canServe, be.getServingsRemaining());
            if (toServe > 0) {
                if (!player.getAbilities().creativeMode) held.decrement(toServe);

                ItemStack out = new ItemStack(TeatimeItems.TEA, toServe);
                NbtCompound nbt = out.getOrCreateNbt();
                nbt.put(KettleBlockEntity.NBT_EFFECTS, be.copyCompiledEffects());
                nbt.put(KettleBlockEntity.NBT_INGREDIENTS, be.copyIngredientIdList());

                if (!player.getInventory().insertStack(out)) player.dropItem(out, false);

                if (player instanceof ServerPlayerEntity sp) {
                    TeatimeAdvancements.onServe(sp, toServe);
                }

                be.decrementServings(toServe);
                be.markDirty();

                if (be.getServingsRemaining() == 0) {
                    stopWhistle(world, pos);
                    be.reset();
                    world.setBlockState(pos, state.with(LIT, false), 3);
                }
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    static boolean isHeated(World world, BlockPos pos) {
        BlockPos belowPos = pos.down();
        BlockState below = world.getBlockState(belowPos);

        if (below.getBlock() instanceof CampfireBlock) {
            Boolean lit = below.getOrEmpty(Properties.LIT).orElse(false);
            if (lit) return true;
        }

        if (below.getBlock() instanceof AbstractFurnaceBlock) {
            Boolean lit = below.getOrEmpty(AbstractFurnaceBlock.LIT).orElse(false);
            if (lit) return true;
        }

        if (below.isOf(net.minecraft.block.Blocks.MAGMA_BLOCK)
                || below.isOf(net.minecraft.block.Blocks.FIRE)
                || below.isOf(net.minecraft.block.Blocks.SOUL_FIRE)
                || world.getFluidState(belowPos).isIn(net.minecraft.registry.tag.FluidTags.LAVA)) {
            return true;
        }

        return below.isIn(TeatimeTags.HEAT_SOURCES);
    }

    static void whistle(World world, BlockPos pos) {
        world.playSound(null, pos, TeatimeSoundEvents.KETTLE_WHISTLE, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    private static void stopWhistle(World world, BlockPos pos) {
        if (world.isClient) return;
        ServerWorld sw = (ServerWorld) world;
        Identifier soundId = Registries.SOUND_EVENT.getId(TeatimeSoundEvents.KETTLE_WHISTLE);
        if (soundId == null) return;

        StopSoundS2CPacket pkt = new StopSoundS2CPacket(soundId, SoundCategory.BLOCKS);
        double cx = pos.getX() + 0.5, cy = pos.getY() + 0.5, cz = pos.getZ() + 0.5;
        double radiusSq = 64 * 64;

        for (ServerPlayerEntity p : sw.getPlayers()) {
            if (p.squaredDistanceTo(cx, cy, cz) <= radiusSq) {
                p.networkHandler.sendPacket(pkt);
            }
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BODY_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BODY_SHAPE;
    }
}
