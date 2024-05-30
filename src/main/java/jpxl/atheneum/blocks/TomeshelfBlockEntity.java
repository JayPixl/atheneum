package jpxl.atheneum.blocks;

import com.mojang.logging.LogUtils;
import jpxl.atheneum.config.AtheneumConfig;
import jpxl.atheneum.properties.ModProperties;
import jpxl.atheneum.properties.TomeSlot;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;
import jpxl.atheneum.Atheneum;
import jpxl.atheneum.components.ModComponents;
import jpxl.atheneum.tags.ModTags;
import jpxl.atheneum.util.*;

import java.util.Objects;

public class TomeshelfBlockEntity extends BlockEntity implements Inventory, IEnchantingBlock, IActivatableEnchantingBlock {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public TomeshelfBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TOMESHELF_BLOCK_ENTITY, pos, state);
    }

    private void updateState(int interactedSlot) {
        if (interactedSlot != 0) {
            LOGGER.error("Expected slot 0, got {}", (Object) interactedSlot);
            return;
        }
        BlockState blockState = this.getCachedState();

        blockState = (BlockState) blockState.with(ModProperties.TOME_SLOT, getItemStackTomeSlotEnumValue(this.getStack(0)));

        Objects.requireNonNull(this.world).setBlockState(this.pos, blockState, Block.NOTIFY_ALL);
        this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.pos, GameEvent.Emitter.of(blockState));
    }

    public TomeSlot getItemStackTomeSlotEnumValue(ItemStack itemStack) {
        if (itemStack.isEmpty()) return TomeSlot.EMPTY;
        String itemStackId = Registries.ITEM.getId(itemStack.getItem()).getPath();

        for (TomeSlot slot : TomeSlot.values()) {
            if (Objects.equals(itemStackId, slot.toString())) {
                return slot;
            }
        }
        return TomeSlot.EMPTY;
    }

    public AtheneumConfig.LibraryBonuses getLibraryBonus() {
        ItemStack stack = getStack(0);
        if (stack.isEmpty()) return new AtheneumConfig.LibraryBonuses();

        for (AtheneumConfig.TomeBonus tomeBonus : Atheneum.config.tomeBonuses) {
            if (tomeBonus.ids.stream().anyMatch(id -> Registries.ITEM.getId(stack.getItem()).compareTo(new Identifier(id)) == 0)) {
                try {
                    return tomeBonus.bonusesPerLevel.multiply(stack.get(ModComponents.TOME_LEVEL));
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }

        return new AtheneumConfig.LibraryBonuses();
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory.clear();
        Inventories.readNbt(nbt, this.inventory, registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, true, registryLookup);
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Objects.requireNonNullElse(this.inventory.get(slot), ItemStack.EMPTY);
        this.inventory.set(slot, ItemStack.EMPTY);
        if (!itemStack.isEmpty()) {
            this.updateState(slot);
        }
        return itemStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.removeStack(slot, 1);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (stack.isIn(ModTags.Items.TOME_ITEM)) {
            this.inventory.set(slot, stack);
            this.updateState(slot);
        } else if (stack.isEmpty()) {
            this.removeStack(slot, 1);
        }
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return hopperInventory.containsAny((ItemStack stack2) -> {
            if (stack2.isEmpty()) {
                return true;
            }
            return ItemStack.areItemsAndComponentsEqual(stack, stack2) && stack2.getCount() + stack.getCount() <= hopperInventory.getMaxCount((ItemStack) stack2);
        });
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return stack.isIn(ModTags.Items.TOME_ITEM) && this.getStack(slot).isEmpty() && stack.getCount() == this.getMaxCountPerStack();
    }

    @Override
    protected void readComponents(BlockEntity.ComponentsAccess components) {
        super.readComponents(components);
        components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(this.inventory);
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.inventory));
    }

    @Override
    public boolean isActive() {
        return !getStack(0).isEmpty();
    }

//    @Override
//    public void removeFromCopiedStackNbt(NbtCompound nbt) {
//        nbt.remove("Items");
//    }
}
