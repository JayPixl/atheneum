package jpxl.atheneum.ench.table;

import jpxl.atheneum.Atheneum;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import jpxl.atheneum.util.EnchantLevelData;
import jpxl.atheneum.util.IEntityDataSaver;
import jpxl.atheneum.util.PlayerEnchantLevelEntry;
import jpxl.atheneum.util.IEnchantingBlock;
import jpxl.atheneum.config.AtheneumConfig;

import java.util.*;

public class NewEnchantmentScreenHandler extends ScreenHandler {

    private final Inventory inventory = new SimpleInventory(3) {
        @Override
        public void markDirty() {
            super.markDirty();
            NewEnchantmentScreenHandler.this.onContentChanged(this);
        }
    };

    private final ScreenHandlerContext context;
    private final Random random = Random.create();
    private final Property seed = Property.create();

    public boolean treasureAllowed;
    public boolean canCurselift;

    public int enchantingLevel;
    public int enchantingXP;

    public int chaos;
    public int minQuantity;
    public int rarityBonus;

    public int bookshelfCount;
    public int artifactCount;
    public int tomeCount;

    public int power;
    public int maxPower;
    public int insight;
    public int maxInsight;
    public int synergy;
    public int maxSynergy;

    public int screenIndex = NewEnchantmentHelper.TableScreens.NONE.ordinal();
    public int minLevel = -1;
    public int xpCost = -1;

    public int[] outputIds = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    public int[] outputLevels = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    public int[] clueAccuracy = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    public int[] selectedEnchants = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

    private final PropertyDelegate outputIdsPropertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return outputIds[index];
        }

        @Override
        public void set(int index, int value) {
            outputIds[index] = value;
        }

        @Override
        public int size() {
            return 10;
        }
    };

    private final PropertyDelegate outputLevelsPropertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return outputLevels[index];
        }

        @Override
        public void set(int index, int value) {
            outputLevels[index] = value;
        }

        @Override
        public int size() {
            return 10;
        }
    };

    private final PropertyDelegate clueAccuracyPropertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return clueAccuracy[index];
        }

        @Override
        public void set(int index, int value) {
            clueAccuracy[index] = value;
        }

        @Override
        public int size() {
            return 10;
        }
    };

    private final PropertyDelegate selectedEnchantsPropertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return selectedEnchants[index];
        }

        @Override
        public void set(int index, int value) {
            selectedEnchants[index] = value;
        }

        @Override
        public int size() {
            return 10;
        }
    };

    private final PropertyDelegate tableStatsPropertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            NewEnchantmentHelper.TableStats statsValue = NewEnchantmentHelper.TableStats.values()[index];
            return switch (statsValue) {
                case POWER -> power;
                case MAX_POWER -> maxPower;
                case RARITY_BONUS -> rarityBonus;
                case MAX_SYNERGY -> maxSynergy;
                case MIN_QUANTITY -> minQuantity;
                case MAX_INSIGHT -> maxInsight;
                case CHAOS -> chaos;
                case INSIGHT -> insight;
                case SYNERGY -> synergy;
                case ENCH_LEVEL -> enchantingLevel;
                case ENCH_XP -> enchantingXP;
                case SCREEN_INDEX -> screenIndex;
                case MIN_LEVEL -> minLevel;
                case TREASURE_ALLOWED -> treasureAllowed ? 1 : 0;
                case CAN_CURSELIFT -> canCurselift ? 1 : 0;
                case XP_COST -> xpCost;
                case ARTIFACT_COUNT -> artifactCount;
                case BOOKSHELF_COUNT -> bookshelfCount;
                case TOME_COUNT -> tomeCount;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            NewEnchantmentHelper.TableStats statsValue = NewEnchantmentHelper.TableStats.values()[index];
            switch (statsValue) {
                case POWER: {
                    power = value;
                    break;
                }
                case MAX_POWER: {
                    maxPower = value;
                    break;
                }
                case RARITY_BONUS: {
                    rarityBonus = value;
                    break;
                }
                case MAX_SYNERGY: {
                    maxSynergy = value;
                    break;
                }
                case MIN_QUANTITY: {
                    minQuantity = value;
                    break;
                }
                case MAX_INSIGHT: {
                    maxInsight = value;
                    break;
                }
                case CHAOS: {
                    chaos = value;
                    break;
                }
                case INSIGHT: {
                    insight = value;
                    break;
                }
                case SYNERGY: {
                    synergy = value;
                    break;
                }
                case ENCH_LEVEL: {
                    enchantingLevel = value;
                    break;
                }
                case ENCH_XP: {
                    enchantingXP = value;
                    break;
                }
                case SCREEN_INDEX: {
                    screenIndex = value;
                    break;
                }
                case MIN_LEVEL: {
                    minLevel = value;
                    break;
                }
                case XP_COST: {
                    xpCost = value;
                    break;
                }
                case TREASURE_ALLOWED: {
                    treasureAllowed = value == 1;
                    break;
                }
                case CAN_CURSELIFT: {
                    canCurselift = value == 1;
                    break;
                }
                case TOME_COUNT: {
                    tomeCount = value;
                    break;
                }
                case BOOKSHELF_COUNT: {
                    bookshelfCount = value;
                    break;
                }
                case ARTIFACT_COUNT: {
                    artifactCount = value;
                    break;
                }
            }
        }

        @Override
        public int size() {
            return 19;
        }
    };

    private final PlayerInventory playerInventory;

    public NewEnchantmentScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public NewEnchantmentScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(Atheneum.NEW_ENCHANTMENT_SCREEN_HANDLER, syncId);
        int i;
        this.context = context;

        // Initialize Simple Inventory
        this.addSlot(new Slot(this.inventory, 0, 8, 47) {

            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.inventory, 1, 28, 47) {

            @Override
            public boolean canInsert(ItemStack stack) {
                for (AtheneumConfig.Catalyst catalyst : Atheneum.config.catalysts) {
                    if (catalyst.ids.stream().anyMatch(id -> Registries.ITEM.getId(stack.getItem()).compareTo(new Identifier(id)) == 0)) {
                        return true;
                    }
                }
                return false;
            }
        });
        this.addSlot(new Slot(this.inventory, 2, 48, 47) {

            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });

        // Add main Inventory Slots
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 115 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 173));
        }

        this.playerInventory = playerInventory;

        this.resetEnchantStats();
        this.generateTableStats();

        // Add Properties for output enchantments
        this.addProperty(this.seed).set(playerInventory.player.getEnchantmentTableSeed());
        this.addProperties(tableStatsPropertyDelegate);
        this.addProperties(outputIdsPropertyDelegate);
        this.addProperties(outputLevelsPropertyDelegate);
        this.addProperties(clueAccuracyPropertyDelegate);
        this.addProperties(selectedEnchantsPropertyDelegate);
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        if (inventory == this.inventory) {
            ItemStack itemStack = inventory.getStack(0);
            if (itemStack.isEmpty()) {
                this.resetOutput(true);
                this.generateTableStats();

                this.screenIndex = NewEnchantmentHelper.TableScreens.NONE.ordinal();
            } else if (itemStack.isEnchantable()) {
                this.context.run((world, pos) -> {
                    this.screenIndex = NewEnchantmentHelper.TableScreens.GENERATE.ordinal();
                    this.resetOutput(true);
                    this.generateTableStats();

                    this.seed.set(playerInventory.player.getEnchantmentTableSeed());
                    this.random.setSeed(this.seed.get());

                    int usableLapisCount = getUsableLapisCount();
                    int adjustedPower = (power / 2) + (random.nextInt(Math.max(5, (int) (double) (power / 2))));
                    int targetQuantity = Math.clamp(random.nextInt((int) Math.sqrt((double) (usableLapisCount * 8) + itemStack.getItem().getEnchantability())), minQuantity, 10);

                    List<NewEnchantmentLevelEntry> totalList = NewEnchantmentHelper.getCompatibleEnchantmentList(adjustedPower, rarityBonus, world.getEnabledFeatures(), treasureAllowed, false, false, true, 255, itemStack, null);
                    List<NewEnchantmentLevelEntry> targetList = NewEnchantmentHelper.pickRandomEnchants(random, targetQuantity, totalList);
                    List<NewEnchantmentLevelEntry> returnList = corruptEnchantmentsInList(world, itemStack, targetList, totalList, treasureAllowed, true);
                    generateClues(returnList, targetList, world, adjustedPower, treasureAllowed, itemStack);

                    System.out.println("CLUES:");
                    for (int z = 0; z < 10; z++) {
                        if (clueAccuracy[z] != -1) {
                            String name = outputIds[z] == -2 ? "JSLEIN" : outputIds[z] == -1 ? "NONE" : Enchantment.byRawId(outputIds[z]).getName(outputLevels[z]).getString();
                            System.out.println(name + " - " + clueAccuracy[z] + "%");
                        }
                    }

                    xpCost = Math.max(1, Math.round((((float) power + ((float) rarityBonus) + (this.getUsableLapisCount() * 2)) / 8f)));
                    minLevel = Math.max(5, Math.round((((float) power + ((float) rarityBonus) + (this.getUsableLapisCount() * 2)) / 2f)));

                    this.sendContentUpdates();
                });
            } else if (NewEnchantmentHelper.hasEnchantments(itemStack)) {
                if (NewEnchantmentHelper.getEnchantments(itemStack).stream().anyMatch(Enchantment::isCursed)) {
                    // DISENCHANTING
                    this.context.run((world, blockPos) -> {
                        System.out.println("CURSED!");
                        this.resetOutput(this.screenIndex != NewEnchantmentHelper.TableScreens.CURSED.ordinal());
                        this.screenIndex = NewEnchantmentHelper.TableScreens.CURSED.ordinal();
                        this.generateTableStats();

                        ItemStack itemStack1 = this.inventory.getStack(0);

                        this.seed.set(playerInventory.player.getEnchantmentTableSeed());
                        this.random.setSeed(this.seed.get());

                        if (canCurselift) {

                            // Get all enchants from current item

                            List<NewEnchantmentLevelEntry> targetList = NewEnchantmentHelper.enchantmentEntryListFromItemStack(itemStack1);

                            List<NewEnchantmentLevelEntry> returnList = getCurseliftResults(world, itemStack, targetList, treasureAllowed, false);

                            generateCurseliftClues(returnList, targetList);

                            System.out.println("CLUES:");
                            for (int z = 0; z < 10; z++) {
                                if (outputIds[z] >= 0) {
                                    System.out.println(Enchantment.byRawId(outputIds[z]).getName(outputLevels[z]).getString() + " - " + clueAccuracy[z] + "%, Selected: " + selectedEnchants[z]);
                                }
                            }

                            // Calculate Requirements/Costs

                            List<NewEnchantmentLevelEntry> selectedList = new ArrayList<>(10);
                            for (int i = 0; i < 10; i++) {
                                if (selectedEnchants[i] >= 0) {
                                    if (targetList.size() > i && targetList.get(i) != null) {
                                        selectedList.add(targetList.get(i));
                                    }
                                }
                            }

                            if (!selectedList.isEmpty()) {
                                xpCost = generateTransferXpCost(selectedList);
                                minLevel = generateTransferLevelCost(selectedList);
                            } else {
                                xpCost = 0;
                                minLevel = 0;
                            }

                        }


                        this.sendContentUpdates();
                    });
                } else {
                    // TRANSFER
                    this.context.run((world, blockPos) -> {
                        System.out.println("TRANSFER! (CONTENT CHANGED)");
                        this.resetOutput(this.screenIndex != NewEnchantmentHelper.TableScreens.TRANSFER.ordinal());
                        this.screenIndex = NewEnchantmentHelper.TableScreens.TRANSFER.ordinal();
                        this.generateTableStats();

                        ItemStack itemStack1 = this.inventory.getStack(0);
                        ItemStack itemStack3 = this.inventory.getStack(2);

                        this.seed.set(playerInventory.player.getEnchantmentTableSeed());
                        this.random.setSeed(this.seed.get());

                        if (!itemStack3.isEmpty() && (itemStack3.isEnchantable() || NewEnchantmentHelper.hasEnchantments(itemStack3))) {

                            // Get all enchants from current item

                            List<NewEnchantmentLevelEntry> targetList = NewEnchantmentHelper.enchantmentEntryListFromItemStack(itemStack1);

                            List<NewEnchantmentLevelEntry> returnList = corruptEnchantmentsInList(world, itemStack3, targetList, null, treasureAllowed, false);

                            generateTransferClues(returnList, targetList);

                            System.out.println("CLUES:");
                            for (int z = 0; z < 10; z++) {
                                if (outputIds[z] >= 0) {
                                    System.out.println(Enchantment.byRawId(outputIds[z]).getName(outputLevels[z]).getString() + " - " + clueAccuracy[z] + "%, Selected: " + selectedEnchants[z]);
                                }
                            }

                            // Calculate Requirements/Costs

                            List<NewEnchantmentLevelEntry> selectedList = new ArrayList<>(10);
                            for (int i = 0; i < 10; i++) {
                                if (selectedEnchants[i] >= 0) {
                                    if (targetList.size() > i && targetList.get(i) != null) {
                                        selectedList.add(targetList.get(i));
                                    }
                                }
                            }

                            if (!selectedList.isEmpty()) {
                                xpCost = generateTransferXpCost(selectedList);
                                minLevel = generateTransferLevelCost(selectedList);
                            } else {
                                xpCost = 0;
                                minLevel = 0;
                            }

                        }


                        this.sendContentUpdates();
                    });
                }
            } else {
                this.screenIndex = NewEnchantmentHelper.TableScreens.NONE.ordinal();
                this.resetOutput(true);
                this.generateTableStats();

                this.seed.set(playerInventory.player.getEnchantmentTableSeed());
                this.random.setSeed(this.seed.get());

                this.sendContentUpdates();
            }
        }

    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        //System.out.println("BUTTON CLICKED! " + generatedEnchantments.size());

        if (id < 0 || id > 11) {
            Util.error(player.getName() + " pressed invalid button id: " + id);
            return false;
        }

        if (id < 10) {
            ItemStack itemStack = this.slots.get(0).getStack();
            ItemStack itemStack3 = this.slots.get(2).getStack();
            if (NewEnchantmentHelper.hasEnchantments(itemStack)) {
                if (NewEnchantmentHelper.getEnchantments(itemStack).stream().anyMatch(Enchantment::isCursed)) {
                    // DISENCHANTING
                    System.out.println("CURSED!");

                    List<NewEnchantmentLevelEntry> targetList = NewEnchantmentHelper.enchantmentEntryListFromItemStack(itemStack);

                    if (!(targetList.size() > id)) {
                        return false;
                    }

                    if (targetList.get(id).enchantment.isCursed()) {
                        selectedEnchants[id] = selectedEnchants[id] == -2 ? 0 :
                                selectedEnchants[id] == -1 ? 1 :
                                        selectedEnchants[id] == 0 ? -2 : -1;
                    }

                    this.context.run((world, blockPos) -> {
                        System.out.println("CURSELIFTING! (ID < 10)");
                        this.resetOutput(this.screenIndex != NewEnchantmentHelper.TableScreens.CURSED.ordinal());
                        this.screenIndex = NewEnchantmentHelper.TableScreens.CURSED.ordinal();
                        this.generateTableStats();

                        this.seed.set(playerInventory.player.getEnchantmentTableSeed());
                        this.random.setSeed(this.seed.get());

                        if (canCurselift) {

                            // Get all enchants from current item

                            List<NewEnchantmentLevelEntry> returnList = getCurseliftResults(world, itemStack, targetList, treasureAllowed, false);

                            generateCurseliftClues(returnList, targetList);

                            System.out.println("CLUES:");
                            for (int z = 0; z < 10; z++) {
                                if (outputIds[z] >= 0) {
                                    System.out.println(Enchantment.byRawId(outputIds[z]).getName(outputLevels[z]).getString() + " - " + clueAccuracy[z] + "%, Selected: " + selectedEnchants[z]);
                                }
                            }

                            // Calculate Requirements/Costs

                            List<NewEnchantmentLevelEntry> selectedList = new ArrayList<>(10);
                            for (int i = 0; i < 10; i++) {
                                if (selectedEnchants[i] >= 0) {
                                    if (targetList.size() > i && targetList.get(i) != null) {
                                        selectedList.add(targetList.get(i));
                                    }
                                }
                            }

                            if (!selectedList.isEmpty()) {
                                xpCost = generateTransferXpCost(selectedList);
                                minLevel = generateTransferLevelCost(selectedList);
                            } else {
                                xpCost = 0;
                                minLevel = 0;
                            }

                        }

                        this.sendContentUpdates();
                    });
                    return true;
                } else {
                    // TRANSFER

                    List<NewEnchantmentLevelEntry> targetList = NewEnchantmentHelper.enchantmentEntryListFromItemStack(itemStack);

                    if (!(targetList.size() > id)) {
                        return false;
                    }

                    if (NewEnchantmentHelper.canApplyToItemStack(itemStack3, targetList.get(id))) {
                        selectedEnchants[id] = selectedEnchants[id] == -2 ? 0 :
                                selectedEnchants[id] == -1 ? 1 :
                                        selectedEnchants[id] == 0 ? -2 : -1;
                    }

                    this.context.run((world, blockPos) -> {
                        System.out.println("TRANSFER! (ID < 10)");
                        this.resetOutput(this.screenIndex != NewEnchantmentHelper.TableScreens.TRANSFER.ordinal());
                        this.screenIndex = NewEnchantmentHelper.TableScreens.TRANSFER.ordinal();
                        this.generateTableStats();

                        this.seed.set(playerInventory.player.getEnchantmentTableSeed());
                        this.random.setSeed(this.seed.get());

                        if (!itemStack3.isEmpty() && (itemStack3.isEnchantable() || NewEnchantmentHelper.hasEnchantments(itemStack3))) {

                            // Get all enchants from current item

                            List<NewEnchantmentLevelEntry> returnList = corruptEnchantmentsInList(world, itemStack3, targetList, null, treasureAllowed, false);

                            generateTransferClues(returnList, targetList);

                            System.out.println("CLUES:");
                            for (int z = 0; z < 10; z++) {
                                if (outputIds[z] >= 0) {
                                    System.out.println(Enchantment.byRawId(outputIds[z]).getName(outputLevels[z]).getString() + " - " + clueAccuracy[z] + "%, Selected: " + selectedEnchants[z]);
                                }
                            }

                            // Calculate Requirements/Costs

                            List<NewEnchantmentLevelEntry> selectedList = new ArrayList<>(10);
                            for (int i = 0; i < 10; i++) {
                                if (selectedEnchants[i] >= 0) {
                                    if (targetList.size() > i && targetList.get(i) != null) {
                                        selectedList.add(targetList.get(i));
                                    }
                                }
                            }

                            if (!selectedList.isEmpty()) {
                                xpCost = generateTransferXpCost(selectedList);
                                minLevel = generateTransferLevelCost(selectedList);
                            } else {
                                xpCost = 0;
                                minLevel = 0;
                            }

                        }

                        this.sendContentUpdates();
                    });
                    return true;
                }
            } else {
                return false;
            }
        } else if (id == 10) {
            if (this.screenIndex == NewEnchantmentHelper.TableScreens.GENERATE.ordinal()) {
                ItemStack itemStack = this.inventory.getStack(0);
                ItemStack itemStack2 = this.inventory.getStack(1);
                this.generateTableStats();

                if ((itemStack2.isEmpty() || itemStack2.getCount() < getUsableLapisCount()) && !player.isInCreativeMode()) {
                    return false;
                }

                if (!itemStack.isEmpty() && itemStack.isEnchantable()) {
                    this.context.run((world, pos) -> {
                        ItemStack itemStack3 = itemStack;

                        this.seed.set(playerInventory.player.getEnchantmentTableSeed());
                        this.random.setSeed(this.seed.get());

                        boolean treasureAllowed = true;

                        int usableLapisCount = getUsableLapisCount();
                        int adjustedPower = (power / 2) + (random.nextInt(Math.max(5, (int) (double) (power / 2))));
                        int targetQuantity = Math.clamp(random.nextInt((int) Math.sqrt((double) (usableLapisCount * 8) + itemStack.getItem().getEnchantability())), minQuantity, 10);

                        List<NewEnchantmentLevelEntry> totalList = NewEnchantmentHelper.getCompatibleEnchantmentList(adjustedPower, rarityBonus, world.getEnabledFeatures(), treasureAllowed, false, false, true, 255, itemStack3, null);
                        List<NewEnchantmentLevelEntry> targetList = NewEnchantmentHelper.pickRandomEnchants(random, targetQuantity, totalList);
                        List<NewEnchantmentLevelEntry> returnList = corruptEnchantmentsInList(world, itemStack3, targetList, totalList, treasureAllowed, true);

                        if (!returnList.isEmpty() && ((player.experienceLevel >= xpCost && player.experienceLevel >= minLevel) || player.getAbilities().creativeMode)) {
                            player.applyEnchantmentCosts(itemStack3, xpCost);
                            EnchantLevelData.addEnchantmentXP((IEntityDataSaver) player, xpCost);

                            this.inventory.setStack(0, NewEnchantmentHelper.applyEnchantments(itemStack3, returnList));

                            if (!player.isInCreativeMode()) {
                                itemStack2.decrement(usableLapisCount);
                                if (itemStack2.isEmpty()) {
                                    this.inventory.setStack(1, ItemStack.EMPTY);
                                }
                            }
                            player.incrementStat(Stats.ENCHANT_ITEM);
                            if (player instanceof ServerPlayerEntity) {
                                Criteria.ENCHANTED_ITEM.trigger((ServerPlayerEntity) player, itemStack3, xpCost);
                            }


                            this.resetEnchantStats();
                            this.resetOutput(true);

                            this.inventory.markDirty();
                            this.onContentChanged(this.inventory);

                            world.playSound(null, (BlockPos) pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0f, world.random.nextFloat() * 0.1f + 0.9f);
                        }
                    });

                    return true;
                }
            } else if (this.screenIndex == NewEnchantmentHelper.TableScreens.TRANSFER.ordinal()) {
                ItemStack itemStack = this.slots.get(0).getStack();
                ItemStack itemStack2 = this.slots.get(1).getStack();
                if (itemStack.hasEnchantments() || !Objects.requireNonNull(itemStack.get(DataComponentTypes.STORED_ENCHANTMENTS)).isEmpty()) {
                    if (NewEnchantmentHelper.getEnchantments(itemStack).stream().anyMatch(Enchantment::isCursed)) {
                        return false;
                    } else {
                        // TRANSFER
                        this.context.run((world, blockPos) -> {
                            System.out.println("TRANSFER! (BUTTON CLICKED!)");
                            this.resetOutput(this.screenIndex != NewEnchantmentHelper.TableScreens.TRANSFER.ordinal());
                            this.screenIndex = NewEnchantmentHelper.TableScreens.TRANSFER.ordinal();
                            this.generateTableStats();


                            ItemStack itemStack1 = this.slots.get(0).getStack();
                            ItemStack itemStack3 = this.slots.get(2).getStack();

                            this.seed.set(playerInventory.player.getEnchantmentTableSeed());
                            this.random.setSeed(this.seed.get());

                            if (!itemStack3.isEmpty() && (itemStack3.isEnchantable() || NewEnchantmentHelper.hasEnchantments(itemStack3))) {

                                // Get all enchants from current item

                                List<NewEnchantmentLevelEntry> targetList = NewEnchantmentHelper.enchantmentEntryListFromItemStack(itemStack1);

                                List<NewEnchantmentLevelEntry> returnList = corruptEnchantmentsInList(world, itemStack3, targetList, null, treasureAllowed, false);

                                generateTransferClues(returnList, targetList);

                                System.out.println("CLUES:");
                                for (int z = 0; z < 10; z++) {
                                    if (outputIds[z] >= 0) {
                                        System.out.println(Enchantment.byRawId(outputIds[z]).getName(outputLevels[z]).getString() + " - " + clueAccuracy[z] + "%, Selected: " + selectedEnchants[z]);
                                    }
                                }

                                // Calculate Requirements/Costs

                                List<NewEnchantmentLevelEntry> selectedList = new ArrayList<>();
                                List<NewEnchantmentLevelEntry> selectedReturnList = new ArrayList<>();
                                for (int i = 0; i < 10; i++) {
                                    if (selectedEnchants[i] >= 0) {
                                        if (targetList.size() > i && NewEnchantmentHelper.canApplyToItemStack(itemStack3, targetList.get(i))) {
                                            selectedList.add(targetList.get(i));
                                            selectedReturnList.add(returnList.get(i));
                                        }
                                    }
                                }

                                if (!selectedList.isEmpty()) {
                                    xpCost = generateTransferXpCost(selectedList);
                                    minLevel = generateTransferLevelCost(selectedList);
                                } else {
                                    xpCost = 0;
                                    minLevel = 0;
                                }

                                System.out.println("SELECTED LIST:");
                                for (NewEnchantmentLevelEntry entry : selectedList) {
                                    System.out.println(entry.enchantment.getName(entry.level).getString());
                                }

                                if (!selectedList.isEmpty() && itemStack2.getCount() >= selectedList.size() && ((player.experienceLevel >= xpCost && player.experienceLevel >= minLevel) || player.getAbilities().creativeMode)) {

                                    player.applyEnchantmentCosts(itemStack3, xpCost);
                                    EnchantLevelData.addEnchantmentXP((IEntityDataSaver) player, (int) (double) (xpCost / 2));

                                    this.inventory.setStack(2, NewEnchantmentHelper.applyEnchantments(itemStack3, selectedReturnList));

                                    this.inventory.setStack(0, NewEnchantmentHelper.removeEnchantments(itemStack1, selectedList));
                                    if (!player.isInCreativeMode()) {
                                        itemStack2.decrement(selectedList.size());
                                        if (itemStack2.isEmpty()) {
                                            this.inventory.setStack(1, ItemStack.EMPTY);
                                        }
                                    }

                                    //player.incrementStat(Stats.ENCHANT_ITEM);
                                    //if (player instanceof ServerPlayerEntity) {
                                    //Criteria.ENCHANTED_ITEM.trigger((ServerPlayerEntity) player, itemStack3, xpCost);
                                    //}

                                    this.resetEnchantStats();
                                    this.resetOutput(true);

                                    this.inventory.markDirty();
                                    this.onContentChanged(this.inventory);

                                    world.playSound(null, (BlockPos) blockPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0f, world.random.nextFloat() * 0.1f + 0.9f);
                                }
                            }

                            this.sendContentUpdates();
                        });
                        return true;
                    }
                } else {
                    return false;
                }
            } else if (this.screenIndex == NewEnchantmentHelper.TableScreens.CURSED.ordinal()) {
                ItemStack itemStack = this.slots.get(0).getStack();
                ItemStack itemStack2 = this.slots.get(1).getStack();
                if (NewEnchantmentHelper.getEnchantments(itemStack).stream().anyMatch(Enchantment::isCursed)) {
                    // CURSED
                    this.context.run((world, blockPos) -> {
                        System.out.println("CURSED! (BUTTON CLICKED!)");
                        this.resetOutput(this.screenIndex != NewEnchantmentHelper.TableScreens.CURSED.ordinal());
                        this.screenIndex = NewEnchantmentHelper.TableScreens.CURSED.ordinal();
                        this.generateTableStats();


                        ItemStack itemStack1 = this.slots.getFirst().getStack();

                        this.seed.set(playerInventory.player.getEnchantmentTableSeed());
                        this.random.setSeed(this.seed.get());

                        if (canCurselift) {

                            // Get all enchants from current item

                            List<NewEnchantmentLevelEntry> targetList = NewEnchantmentHelper.enchantmentEntryListFromItemStack(itemStack1);

                            List<NewEnchantmentLevelEntry> returnList = getCurseliftResults(world, itemStack1, targetList, treasureAllowed, false);

                            generateCurseliftClues(returnList, targetList);

                            System.out.println("CLUES:");
                            for (int z = 0; z < 10; z++) {
                                if (outputIds[z] >= 0) {
                                    System.out.println(Enchantment.byRawId(outputIds[z]).getName(outputLevels[z]).getString() + " - " + clueAccuracy[z] + "%, Selected: " + selectedEnchants[z]);
                                }
                            }

                            // Calculate Requirements/Costs

                            List<NewEnchantmentLevelEntry> selectedList = new ArrayList<>();
                            for (int i = 0; i < 10; i++) {
                                if (selectedEnchants[i] >= 0) {
                                    if (targetList.size() > i) {
                                        selectedList.add(targetList.get(i));
                                    }
                                }
                            }

                            if (!selectedList.isEmpty()) {
                                xpCost = generateTransferXpCost(selectedList);
                                minLevel = generateTransferLevelCost(selectedList);
                            } else {
                                xpCost = 0;
                                minLevel = 0;
                            }

                            System.out.println("SELECTED LIST:");
                            for (NewEnchantmentLevelEntry entry : selectedList) {
                                System.out.println(entry.enchantment.getName(entry.level).getString());
                            }

                            if (!selectedList.isEmpty() && itemStack2.getCount() >= selectedList.size() && ((player.experienceLevel >= xpCost && player.experienceLevel >= minLevel) || player.getAbilities().creativeMode)) {

                                player.applyEnchantmentCosts(itemStack1, xpCost);
                                //EnchantLevelData.addEnchantmentXP((IEntityDataSaver) player, (int) (double) (xpCost / 2));

                                this.inventory.setStack(0, NewEnchantmentHelper.replaceEnchantments(itemStack1, returnList));

                                if (!player.isInCreativeMode()) {
                                    itemStack2.decrement(selectedList.size());
                                    if (itemStack2.isEmpty()) {
                                        this.inventory.setStack(1, ItemStack.EMPTY);
                                    }
                                }

                                //player.incrementStat(Stats.ENCHANT_ITEM);
                                //if (player instanceof ServerPlayerEntity) {
                                //Criteria.ENCHANTED_ITEM.trigger((ServerPlayerEntity) player, itemStack3, xpCost);
                                //}

                                this.resetEnchantStats();
                                this.resetOutput(true);

                                this.inventory.markDirty();
                                this.onContentChanged(this.inventory);

                                world.playSound(null, (BlockPos) blockPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0f, world.random.nextFloat() * 0.1f + 0.9f);
                            }
                        }

                        this.sendContentUpdates();
                    });
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (id == 11) {
            if (player.experienceLevel < 1) {
                return false;
            }
            this.context.run((world, pos) -> {
                player.applyEnchantmentCosts(ItemStack.EMPTY, 1);
                this.onContentChanged(this.inventory);
            });
            return true;
        }

        return false;
    }

    public int getLapisCount() {
        ItemStack itemStack = this.inventory.getStack(1);
        if (itemStack.isEmpty()) {
            return 0;
        }
        return itemStack.getCount();
    }

    public int getUsableLapisCount() {
        return Math.min(NewEnchantmentHelper.getEnchantmentStatsPerLevel(NewEnchantmentHelper.TableLevelStats.MAX_LAPIS, enchantingLevel), this.getLapisCount());
    }

    public int getSeed() {
        return this.seed.get();
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.inventory));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return NewEnchantmentScreenHandler.canUse(this.context, player, Blocks.ENCHANTING_TABLE);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot == 0) {
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot == 1) {
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot == 2) {
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemStack2.isOf(Items.LAPIS_LAZULI)) {
                if (!this.insertItem(itemStack2, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.slots.getFirst().hasStack() && this.slots.getFirst().canInsert(itemStack2)) {
                ItemStack itemStack3 = itemStack2.copyWithCount(1);
                itemStack2.decrement(1);
                this.slots.getFirst().setStack(itemStack3);
            } else if (!this.slots.get(2).hasStack() && this.slots.get(2).canInsert(itemStack2)) {
                ItemStack itemStack3 = itemStack2.copyWithCount(1);
                itemStack2.decrement(1);
                this.slots.get(2).setStack(itemStack3);
            } else {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot2.onTakeItem(player, itemStack2);
        }
        return itemStack;
    }

    private List<NewEnchantmentLevelEntry> corruptEnchantmentsInList(World world, ItemStack itemStack, List<NewEnchantmentLevelEntry> targetList, @Nullable List<NewEnchantmentLevelEntry> passedTotalList, boolean treasureAllowed, boolean spareFirst) {

        // calculate corruption chance for enchantments and apply corruption

        List<NewEnchantmentLevelEntry> returnList = new ArrayList<>();
        for (int i = 0; i < targetList.size(); i++) {
            if (i == 0 && spareFirst) {
                returnList.add(targetList.getFirst());
                continue;
            }

            NewEnchantmentLevelEntry entry = targetList.get(i);
            if (random.nextInt(100) < chaos) {
                int REROLL_WEIGHT = 50 + synergy;
                int LOST_WEIGHT = 50;
                int CURSE_WEIGHT = Math.round(8f * (float) (100 / Math.max(synergy, 1)));
                int chaosRandom = random.nextInt(REROLL_WEIGHT + LOST_WEIGHT + CURSE_WEIGHT);
                int sum = REROLL_WEIGHT + LOST_WEIGHT + CURSE_WEIGHT;
                System.out.println("CHAOS RANDOM: " + chaosRandom + "/" + sum);
                List<NewEnchantmentLevelEntry> totalList = passedTotalList == null ? NewEnchantmentHelper.getCompatibleEnchantmentList(
                        Math.max(5, NewEnchantmentHelper.getAveragePower(targetList.get(i).enchantment, targetList.get(i).level) + random.nextInt(Math.max(1, synergy)) - random.nextInt(chaos)),
                        -1,
                        world.getEnabledFeatures(),
                        treasureAllowed,
                        false,
                        false,
                        false,
                        255,
                        itemStack,
                        NewEnchantmentHelper.getEnchantments(targetList)
                ) : passedTotalList;

                if (chaosRandom < REROLL_WEIGHT && !totalList.isEmpty()) {
                    // REROLL

                    Optional<NewEnchantmentLevelEntry> addedEnchantment = Weighting.getRandom(random, totalList);

                    if (addedEnchantment.isEmpty()) {
                        returnList.add(null);
                    } else {
                        returnList.add(addedEnchantment.get());
                        //totalList.removeIf(entry2 -> !entry2.enchantment.canCombine(addedEnchantment.get().enchantment));
                    }

                } else if (chaosRandom < REROLL_WEIGHT + LOST_WEIGHT) {
                    // LOST
                    returnList.add(null);
                } else {
                    // CURSE!
                    int cursePower = Math.max(5, ((targetList.get(i).enchantment.getMinPower(targetList.get(i).level) + targetList.get(i).enchantment.getMaxPower(targetList.get(i).level)) / 2) - random.nextInt(synergy + 1));

                    System.out.println("CURSED!");
                    List<NewEnchantmentLevelEntry> totalCurseList = NewEnchantmentHelper.getCompatibleEnchantmentList(cursePower, -1, world.getEnabledFeatures(), treasureAllowed, true, true, false, 255, itemStack, NewEnchantmentHelper.getEnchantments(targetList));
                    if (totalCurseList.isEmpty()) {
                        returnList.add(null);
                        continue;
                    }

                    NewEnchantmentLevelEntry addedCurse = NewEnchantmentHelper.pickRandomEnchants(random, 1, totalCurseList).getFirst();

                    returnList.add(addedCurse);
                }
            } else {
                returnList.add(entry);
            }
        }

        System.out.println("RETURN LIST:");
        for (NewEnchantmentLevelEntry entry2 : returnList) {
            System.out.println(entry2 == null ? "ENCHANTMENT LOST!" : entry2.enchantment.getName(entry2.level).getString());
        }

        return returnList;
    }

    private List<NewEnchantmentLevelEntry> getCurseliftResults(World world, ItemStack itemStack, List<NewEnchantmentLevelEntry> targetList, boolean treasureAllowed, boolean spareFirst) {

        // calculate corruption chance for enchantments and apply corruption

        List<NewEnchantmentLevelEntry> returnList = new ArrayList<>();
        for (int i = 0; i < targetList.size(); i++) {
            NewEnchantmentLevelEntry entry = targetList.get(i);

            if ((i == 0 && spareFirst) || !entry.enchantment.isCursed()) {
                returnList.add(entry);
                continue;
            }

            if (random.nextInt(100) < chaos) {
                returnList.add(null);
            } else {
                List<NewEnchantmentLevelEntry> totalList = NewEnchantmentHelper.getCompatibleEnchantmentList(
                        Math.max(5, NewEnchantmentHelper.getAveragePower(entry.enchantment, entry.level) - random.nextInt(Math.max(1, synergy)) + random.nextInt(chaos)),
                        -1,
                        world.getEnabledFeatures(),
                        treasureAllowed,
                        true,
                        true,
                        false,
                        255,
                        itemStack,
                        NewEnchantmentHelper.getEnchantments(targetList)
                );

                // REROLL

                if (totalList.isEmpty()) {
                    returnList.add(entry);
                    continue;
                }

                Optional<NewEnchantmentLevelEntry> addedCurse = Weighting.getRandom(random, totalList);

                if (addedCurse.isEmpty()) {
                    returnList.add(entry);
                } else {
                    returnList.add(addedCurse.get());
                }
            }
        }

        System.out.println("RETURN LIST:");
        for (NewEnchantmentLevelEntry entry2 : returnList) {
            System.out.println(entry2 == null ? "ENCHANTMENT LOST!" : entry2.enchantment.getName(entry2.level).getString());
        }

        return returnList;
    }

    private void generateClues(List<NewEnchantmentLevelEntry> returnList, List<NewEnchantmentLevelEntry> targetList, World world, int adjustedPower, boolean treasureAllowed, ItemStack itemStack) {
        int q = 0;
        int lastHit = 0;
        int currentInsight = insight;
        for (NewEnchantmentLevelEntry entry2 : returnList) {
            // apply current insight and compare to power

            if (currentInsight < 2) {
                break;
            }

            int accuracy = (currentInsight / 2) + random.nextInt(currentInsight);
            int totalPower = entry2 == null ? NewEnchantmentHelper.getAveragePower(targetList.get(q).enchantment, targetList.get(q).level) : NewEnchantmentHelper.getAveragePower(entry2.enchantment, entry2.level);
            if (accuracy >= totalPower) {
                // Accurate clue!
                clueAccuracy[q] = 100;
                if (entry2 == null) {
                    outputIds[q] = -1;
                    outputLevels[q] = -1;
                } else {
                    outputIds[q] = Registries.ENCHANTMENT.getRawId(entry2.enchantment);
                    outputLevels[q] = entry2.level;
                    lastHit = q;
                }
            } else {
                float accuracyChance = (float) accuracy / totalPower;
                clueAccuracy[q] = Math.round(accuracyChance * 100);

                if (random.nextFloat() < accuracyChance) {
                    // Accurate clue!
                    if (entry2 == null) {
                        outputIds[q] = -1;
                        outputLevels[q] = -1;
                    } else {
                        outputIds[q] = Registries.ENCHANTMENT.getRawId(entry2.enchantment);
                        outputLevels[q] = entry2.level;
                        lastHit = q;
                    }
                } else {
                    // Inaccurate Clue!
                    if (Math.round(accuracyChance * 100) < 25) {
                        // Break loop
                        outputIds[q] = -2;
                        outputLevels[q] = -1;
                        lastHit = q;
                        break;
                    } else {
                        // Accurate corruption?
                        boolean accurateCorruption = random.nextBoolean();
                        boolean previousCorrupted = entry2 == null ? random.nextBoolean() : entry2.enchantment != targetList.get(q).enchantment;
                        boolean guessIsCorrupted = accurateCorruption == previousCorrupted;
                        int guessPower = entry2 == null ? adjustedPower : NewEnchantmentHelper.getAveragePower(entry2.enchantment, entry2.level);

                        List<NewEnchantmentLevelEntry> totalWrongEnchantmentList = NewEnchantmentHelper.getCompatibleEnchantmentList(guessPower, -1, world.getEnabledFeatures(), treasureAllowed, guessIsCorrupted, guessIsCorrupted, false, 255, itemStack, NewEnchantmentHelper.getEnchantments(returnList));
                        if (totalWrongEnchantmentList.isEmpty()) {
                            outputIds[q] = -1;
                            outputLevels[q] = -1;
                        } else {
                            NewEnchantmentLevelEntry addedWrongEnchantment = NewEnchantmentHelper.pickRandomEnchants(random, 1, totalWrongEnchantmentList).getFirst();
                            outputIds[q] = Registries.ENCHANTMENT.getRawId(addedWrongEnchantment.enchantment);
                            outputLevels[q] = addedWrongEnchantment.level;
                            lastHit = q;
                        }
                    }
                }
            }

            // decay insight
            currentInsight -= random.nextInt(currentInsight) + 1;
            q++;
        }

        // Fix last clue if empty
        for (int p = lastHit + 1; p < 10; p++) {
            if (p != 0) {
                outputIds[p] = -1;
                outputLevels[p] = -1;
                clueAccuracy[p] = -1;
            }
        }
    }

    private void generateTransferClues(List<NewEnchantmentLevelEntry> returnList, List<NewEnchantmentLevelEntry> targetList) {
        int q = 0;
        for (NewEnchantmentLevelEntry entry2 : returnList) {
            if (targetList.get(q) == null) {
                q++;
                continue;
            }

            outputIds[q] = Registries.ENCHANTMENT.getRawId(targetList.get(q).enchantment);
            outputLevels[q] = targetList.get(q).level;

            int accuracy = random.nextInt(insight);
            int totalPower = entry2 == null ? NewEnchantmentHelper.getAveragePower(targetList.get(q).enchantment, targetList.get(q).level) : NewEnchantmentHelper.getAveragePower(entry2.enchantment, entry2.level);

            if (accuracy >= totalPower) {
                // Accurate clue!
                clueAccuracy[q] = 100;
                if (selectedEnchants[q] >= 0) {
                    selectedEnchants[q] = entry2 != targetList.get(q) ? 0 : 1;
                } else {
                    selectedEnchants[q] = entry2 != targetList.get(q) ? -2 : -1;
                }
            } else {
                float accuracyChance = (float) accuracy / totalPower;
                if (random.nextFloat() < accuracyChance) {
                    // Accurate clue!
                    clueAccuracy[q] = Math.round(accuracyChance * 100f);
                    if (selectedEnchants[q] >= 0) {
                        selectedEnchants[q] = entry2 != targetList.get(q) ? 0 : 1;
                    } else {
                        selectedEnchants[q] = entry2 != targetList.get(q) ? -2 : -1;
                    }
                } else {
                    // Inaccurate Clue!
                    clueAccuracy[q] = Math.round(accuracyChance * 100f);
                    if (selectedEnchants[q] >= 0) {
                        selectedEnchants[q] = entry2 == targetList.get(q) ? 0 : 1;
                    } else {
                        selectedEnchants[q] = entry2 == targetList.get(q) ? -2 : -1;
                    }
                }
            }

            // decay insight
            q++;
        }
    }

    private void generateCurseliftClues(List<NewEnchantmentLevelEntry> returnList, List<NewEnchantmentLevelEntry> targetList) {
        int q = 0;
        for (NewEnchantmentLevelEntry entry2 : returnList) {
            outputIds[q] = Registries.ENCHANTMENT.getRawId(targetList.get(q).enchantment);
            outputLevels[q] = targetList.get(q).level;
            clueAccuracy[q] = 0;

            if (!targetList.get(q).enchantment.isCursed()) {
                q++;
                continue;
            }

            int accuracy = random.nextInt(insight);
            int totalPower = NewEnchantmentHelper.getAveragePower(targetList.get(q).enchantment, targetList.get(q).level);

            if (accuracy >= totalPower) {
                // Accurate clue!
                clueAccuracy[q] = 100;
                if (selectedEnchants[q] >= 0) {
                    selectedEnchants[q] = entry2 != targetList.get(q) ? 1 : 0;
                } else {
                    selectedEnchants[q] = entry2 != targetList.get(q) ? -1 : -2;
                }
            } else {
                float accuracyChance = (float) accuracy / totalPower;
                clueAccuracy[q] = Math.round(accuracyChance * 100f);
                if (random.nextFloat() < accuracyChance) {
                    // Accurate clue!
                    if (selectedEnchants[q] >= 0) {
                        selectedEnchants[q] = entry2 != targetList.get(q) ? 1 : 0;
                    } else {
                        selectedEnchants[q] = entry2 != targetList.get(q) ? -1 : -2;
                    }
                } else {
                    // Inaccurate Clue!
                    if (selectedEnchants[q] >= 0) {
                        selectedEnchants[q] = entry2 == targetList.get(q) ? 1 : 0;
                    } else {
                        selectedEnchants[q] = entry2 == targetList.get(q) ? -1 : -2;
                    }
                }
            }

            q++;
        }
    }

    private int generateTransferXpCost(List<NewEnchantmentLevelEntry> targetList) {
        int tXpCost = Math.max(
                1,
                (Math.round(
                        (
                                ((float) (
                                        targetList.getFirst().enchantment.getMinPower(targetList.getFirst().level) +
                                                targetList.getFirst().enchantment.getMaxPower(targetList.getFirst().level)
                                ) / 2f) +
                                        (11f - (float) targetList.getFirst().enchantment.getWeight())
                        ) / 8f
                ))
        );

        for (int p = 1; p < targetList.size(); p++) {
            tXpCost += Math.max(
                    1,
                    (Math.round(
                            (
                                    ((float) (
                                            targetList.get(p).enchantment.getMinPower(targetList.get(p).level) +
                                                    targetList.get(p).enchantment.getMaxPower(targetList.get(p).level)
                                    ) / 2f) +
                                            (11f - (float) targetList.get(p).enchantment.getWeight())
                            ) / 16f
                    ))
            );
        }

        return tXpCost;
    }

    private int generateTransferLevelCost(List<NewEnchantmentLevelEntry> targetList) {
        int tMinLevel = Math.max(
                5,
                (Math.round(
                        (
                                ((float) (
                                        targetList.getFirst().enchantment.getMinPower(targetList.getFirst().level) +
                                                targetList.getFirst().enchantment.getMaxPower(targetList.getFirst().level)
                                ) / 2f) +
                                        (11f - (float) targetList.getFirst().enchantment.getWeight())
                        ) / 2f
                ))
        );

        for (int p = 1; p < targetList.size(); p++) {
            tMinLevel += Math.max(
                    5,
                    (Math.round(
                            (
                                    ((float) (
                                            targetList.get(p).enchantment.getMinPower(targetList.get(p).level) +
                                                    targetList.get(p).enchantment.getMaxPower(targetList.get(p).level)
                                    ) / 2f) +
                                            (11f - (float) targetList.get(p).enchantment.getWeight())
                            ) / 4f
                    ))
            );
        }

        return tMinLevel;
    }

    private void generateTableStats() {
        final int[] tChaos = {NewEnchantmentHelper.DEFAULT_CHAOS};
        final int[] tStability = {NewEnchantmentHelper.DEFAULT_SYNERGY};
        final int[] tPower = {NewEnchantmentHelper.DEFAULT_POWER};
        final int[] tMaxPower = {NewEnchantmentHelper.getEnchantmentStatsPerLevel(NewEnchantmentHelper.TableLevelStats.MAX_POWER, enchantingLevel)};
        final int[] tInsight = {NewEnchantmentHelper.DEFAULT_INSIGHT};
        final int[] tMaxInsight = {NewEnchantmentHelper.getEnchantmentStatsPerLevel(NewEnchantmentHelper.TableLevelStats.MAX_INSIGHT, enchantingLevel)};
        final int[] tRarity = {NewEnchantmentHelper.DEFAULT_RARITY};
        final int[] tMaxStability = {NewEnchantmentHelper.getEnchantmentStatsPerLevel(NewEnchantmentHelper.TableLevelStats.MAX_STABILITY, enchantingLevel)};
        final int[] tMinQuantity = {NewEnchantmentHelper.DEFAULT_QUANTITY};
        final boolean[] tTreasureAllowed = {false};
        final boolean[] tCanCurselift = {false};

        this.context.run((world, pos) -> {

            List<NewEnchantmentHelper.LibraryBonusWithCategory> bonusList = new ArrayList<>();

            int tBookshelfCount = 0;
            int tArtifactCount = 0;
            int tTomeCount = 0;

            for (BlockPos blockPos : NewEnchantmentHelper.POWER_PROVIDER_OFFSETS) {

                BlockState targetBlockState = world.getBlockState(pos.add(blockPos));

                if (world.getBlockState(pos.add(blockPos.getX() / 2, blockPos.getY(), blockPos.getZ() / 2)).isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER)) {
                    for (AtheneumConfig.LibraryComponent component : Atheneum.config.libraryComponents) {
                        if (!(component.ids.stream().anyMatch(id -> Registries.BLOCK.getId(targetBlockState.getBlock()).compareTo(new Identifier(id)) == 0))) continue;

                        AtheneumConfig.LibraryBonuses bonuses;

                        if (component.type == AtheneumConfig.ComponentTypes.simple) {
                            bonuses = component.bonuses;
                        } else {
                            BlockEntity blockEntity = world.getBlockEntity(pos.add(blockPos));
                            try {
                                bonuses = ((IEnchantingBlock) blockEntity).getLibraryBonus();
                            } catch (Exception e) {
                                e.printStackTrace();
                                bonuses = component.bonuses;
                            }
                        }

                        if (bonuses.isActive()) {
                            bonusList.add(new NewEnchantmentHelper.LibraryBonusWithCategory(bonuses, component.category));
                        }
                    }

                }

            }

            bonusList.sort((o1, o2) -> {
                if (o1.category == AtheneumConfig.ComponentCategories.bookshelf) {
                    if (o2.category == AtheneumConfig.ComponentCategories.bookshelf) {
                        return o1.bonuses.totalBonus() - o2.bonuses.totalBonus();
                    } else {
                        return 1;
                    }
                } else {
                    if (o2.category == AtheneumConfig.ComponentCategories.bookshelf) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });

            for (NewEnchantmentHelper.LibraryBonusWithCategory entry : bonusList) {
                System.out.println(entry.category);
                try {
                    System.out.println(AtheneumConfig.ComponentCategories.valueOf(entry.category.name()));
                } catch (Exception ignored) { }
                System.out.println(entry.category.name());
                System.out.println(AtheneumConfig.ComponentCategories.bookshelf.name());
                if (entry.category == AtheneumConfig.ComponentCategories.bookshelf) {
                    tBookshelfCount++;
                    if (tBookshelfCount > NewEnchantmentHelper.getEnchantmentStatsPerLevel(NewEnchantmentHelper.TableLevelStats.MAX_BOOKSHELVES, enchantingLevel)) {
                        continue;
                    }
                } else {
                    if (entry.category == AtheneumConfig.ComponentCategories.artifact) {
                        tArtifactCount++;
                    } else {
                        tTomeCount++;
                    }
                    if (tArtifactCount > NewEnchantmentHelper.getEnchantmentStatsPerLevel(NewEnchantmentHelper.TableLevelStats.MAX_ARTIFACTS, enchantingLevel) || tTomeCount > NewEnchantmentHelper.getEnchantmentStatsPerLevel(NewEnchantmentHelper.TableLevelStats.MAX_TOMES, enchantingLevel)) {
                        tChaos[0] += 10;
                        tStability[0] -= 5;
                    }
                }

                tPower[0] += entry.bonuses.power;
                tMaxPower[0] += entry.bonuses.maxPower;
                tStability[0] += entry.bonuses.synergy;
                tMaxStability[0] += entry.bonuses.maxSynergy;
                tInsight[0] += entry.bonuses.insight;
                tMaxInsight[0] += entry.bonuses.maxInsight;
                tChaos[0] += entry.bonuses.chaos;
                tMinQuantity[0] += entry.bonuses.minQuantity;
                tRarity[0] += entry.bonuses.rarityBonus;

                if (entry.bonuses.allowTreasure) tTreasureAllowed[0] = true;
                if (entry.bonuses.canCurselift) tCanCurselift[0] = true;
            }

            try {
                AtheneumConfig.LibraryBonuses catalystBonuses = Atheneum.config.catalysts.stream().filter(catalyst ->
                        catalyst.ids.stream().anyMatch(id -> Registries.ITEM.getId(this.inventory.getStack(1).getItem()).compareTo(new Identifier(id)) == 0)
                ).toList().getFirst().bonuses;

                tPower[0] += catalystBonuses.power;
                tMaxPower[0] += catalystBonuses.maxPower;
                tStability[0] += catalystBonuses.synergy;
                tMaxStability[0] += catalystBonuses.maxSynergy;
                tInsight[0] += catalystBonuses.insight;
                tMaxInsight[0] += catalystBonuses.maxInsight;
                tChaos[0] += catalystBonuses.chaos;
                tMinQuantity[0] += catalystBonuses.minQuantity;
                tRarity[0] += catalystBonuses.rarityBonus;

                if (catalystBonuses.allowTreasure) tTreasureAllowed[0] = true;
                if (catalystBonuses.canCurselift) tCanCurselift[0] = true;
            } catch (Exception e) { }


            power = Math.clamp(tPower[0], 0, tMaxPower[0]);
            maxPower = tMaxPower[0];
            rarityBonus = Math.clamp(tRarity[0], 0, 10);
            insight = Math.clamp(tInsight[0], 0, tMaxInsight[0]);
            maxInsight = tMaxInsight[0];
            chaos = Math.clamp(tChaos[0], 10, 90);
            synergy = Math.clamp(tStability[0], 0, tMaxStability[0]);
            maxSynergy = tMaxStability[0];
            minQuantity = Math.clamp(tMinQuantity[0], 1, 10);
            treasureAllowed = tTreasureAllowed[0];
            canCurselift = tCanCurselift[0];

            bookshelfCount = tBookshelfCount;
            tomeCount = tTomeCount;
            artifactCount = tArtifactCount;

            System.out.println("TOME COUNT: " + tTomeCount + " / " + NewEnchantmentHelper.getEnchantmentStatsPerLevel(NewEnchantmentHelper.TableLevelStats.MAX_TOMES, enchantingLevel));
            System.out.println("BOOKSHELF COUNT: " + tBookshelfCount + " / " + NewEnchantmentHelper.getEnchantmentStatsPerLevel(NewEnchantmentHelper.TableLevelStats.MAX_BOOKSHELVES, enchantingLevel));
            System.out.println("ARTIFACT COUNT: " + tArtifactCount + " / " + NewEnchantmentHelper.getEnchantmentStatsPerLevel(NewEnchantmentHelper.TableLevelStats.MAX_ARTIFACTS, enchantingLevel));
        });

    }

    private void resetOutput(boolean resetSelected) {
        for (int i = 0; i < 10; i++) {
            this.outputIds[i] = -1;
            this.outputLevels[i] = -1;
            this.clueAccuracy[i] = -1;
            if (resetSelected) {
                this.selectedEnchants[i] = -1;
            }
        }
        this.xpCost = -1;
        this.minLevel = -1;
    }

    private void resetEnchantStats() {
        PlayerEnchantLevelEntry enchantLevel = EnchantLevelData.getEnchantmentLevels((IEntityDataSaver) playerInventory.player);
        enchantingLevel = enchantLevel.getLevel();
        enchantingXP = enchantLevel.getXp();
    }


}
