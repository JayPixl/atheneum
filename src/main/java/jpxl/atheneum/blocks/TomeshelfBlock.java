package jpxl.atheneum.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import jpxl.atheneum.tags.ModTags;
import jpxl.atheneum.properties.ModProperties;
import jpxl.atheneum.properties.TomeSlot;

public class TomeshelfBlock extends BlockWithEntity {
    public static final MapCodec<TomeshelfBlock> CODEC = TomeshelfBlock.createCodec(TomeshelfBlock::new);
    public static final EnumProperty<TomeSlot> TOME_SLOT = ModProperties.TOME_SLOT;
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public TomeshelfBlock(Settings settings) {
        super(settings);
        BlockState blockState = (BlockState)((BlockState)this.stateManager.getDefaultState())
                .with(FACING, Direction.NORTH)
                .with(TOME_SLOT, TomeSlot.EMPTY);
        this.setDefaultState(blockState);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof TomeshelfBlockEntity tomeshelfBlockEntity)) {
            return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        if (!stack.isIn(ModTags.Items.TOME_ITEM) || !state.get(TOME_SLOT).equals(TomeSlot.EMPTY)) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        TomeshelfBlock.tryAddBook(world, pos, player, tomeshelfBlockEntity, stack);
        return ItemActionResult.success(world.isClient);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof TomeshelfBlockEntity tomeshelfBlockEntity)) {
            return ActionResult.PASS;
        }
        if (state.get(TOME_SLOT).equals(TomeSlot.EMPTY)) {
            return ActionResult.CONSUME;
        }
        TomeshelfBlock.tryRemoveBook(world, pos, player, tomeshelfBlockEntity);
        return ActionResult.success(world.isClient);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public MapCodec<TomeshelfBlock> getCodec() {
        return CODEC;
    }

    private static void tryAddBook(World world, BlockPos pos, PlayerEntity player, TomeshelfBlockEntity blockEntity, ItemStack stack) {
        if (world.isClient) {
            return;
        }
        //System.out.println(atheneum.config.toggleA);

        player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        SoundEvent soundEvent = SoundEvents.BLOCK_CHISELED_BOOKSHELF_INSERT;
        blockEntity.setStack(0, stack.split(1));
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
        if (player.isCreative()) {
            stack.increment(1);
        }
    }

    private static void tryRemoveBook(World world, BlockPos pos, PlayerEntity player, TomeshelfBlockEntity blockEntity) {
        if (world.isClient) {
            return;
        }
        ItemStack itemStack = blockEntity.removeStack(0, 1);
        SoundEvent soundEvent = SoundEvents.BLOCK_CHISELED_BOOKSHELF_PICKUP;
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
        if (!player.getInventory().insertStack(itemStack)) {
            player.dropItem(itemStack, false);
        }
        world.emitGameEvent((Entity)player, GameEvent.BLOCK_CHANGE, pos);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TomeshelfBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder
                .add(FACING)
                .add(TOME_SLOT);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        TomeshelfBlockEntity tomeshelfBlockEntity;
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof TomeshelfBlockEntity && !(tomeshelfBlockEntity = (TomeshelfBlockEntity) blockEntity).isEmpty()) {
            ItemStack itemStack = tomeshelfBlockEntity.getStack(0);
            if (!itemStack.isEmpty()) {
                ItemScatterer.spawn(world, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), itemStack);
            }
            tomeshelfBlockEntity.clear();
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
