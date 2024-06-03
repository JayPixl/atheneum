package jpxl.atheneum.ench.table;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import jpxl.atheneum.config.AtheneumConfig;

import java.util.*;

public abstract class NewEnchantmentHelper {
    public static int getEnchantmentStatsPerLevel(TableLevelStats stat, int level) {
        return switch (stat) {
            case MAX_POWER -> (5 + (level * 5));
            case MAX_STABILITY -> (5 + (level * 5));
            case MAX_INSIGHT -> (5 + (level * 5));
            case MAX_LAPIS -> Math.min(10, level);
            case MAX_TOMES -> Math.clamp(level / 2, 1, 5);
            case MAX_ARTIFACTS -> Math.clamp(level / 2, 1, 5);
            case MAX_BOOKSHELVES -> Math.min(level + 2, 10);
            default -> 0;
        };
    }

    public static int getEnchLevelXpPerLevel(int level) {
        return 10 + Math.round((level - 1) * ((float) (level - 1) / 2));
    }

    public static Formatting getRarityColor(Enchantment enchantment) {
        if (enchantment.getWeight() == 1) {
            return Formatting.AQUA;
        } else if (enchantment.getWeight() < 3) {
            return Formatting.BLUE;
        } else if (enchantment.getWeight() < 6) {
            return Formatting.GOLD;
        } else {
            return Formatting.GREEN;
        }
    }

    public static boolean hasEnchantments(ItemStack itemStack) {
        if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
            return !Objects.requireNonNull(itemStack.get(DataComponentTypes.STORED_ENCHANTMENTS)).isEmpty();
        } else {
            return itemStack.hasEnchantments();
        }
    }

    public static List<Enchantment> getEnchantments(ItemStack itemStack) {
        List<Enchantment> enchantments = new ArrayList<>();
        if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
            for (RegistryEntry<Enchantment> entry : Objects.requireNonNull(itemStack.get(DataComponentTypes.STORED_ENCHANTMENTS)).getEnchantments()) {
                enchantments.add(Registries.ENCHANTMENT.get(entry.getKey().get()));
            }
        }
        final ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(itemStack.getEnchantments());
        for (RegistryEntry<Enchantment> entry : builder.getEnchantments()) {
            enchantments.add(Registries.ENCHANTMENT.get(entry.getKey().get()));
        }
        return enchantments;
    }

    public static List<Enchantment> getEnchantments(List<NewEnchantmentLevelEntry> enchantmentList) {
        return enchantmentList.stream().map(e -> e == null ? null : e.enchantment).filter(Objects::nonNull).toList();
    }

    public static List<NewEnchantmentLevelEntry> enchantmentEntryListFromItemStack(ItemStack sourceItemStack) {
        List<NewEnchantmentLevelEntry> enchantments = new ArrayList<>(10);
        if (sourceItemStack.isOf(Items.ENCHANTED_BOOK)) {
            ItemEnchantmentsComponent.Builder builder1 = new ItemEnchantmentsComponent.Builder(Objects.requireNonNull(sourceItemStack.get(DataComponentTypes.STORED_ENCHANTMENTS)));
            for (RegistryEntry<Enchantment> entry : builder1.getEnchantments()) {
                Enchantment enchantment = Registries.ENCHANTMENT.get(entry.getKey().get());
                if (enchantment != null) {
                    enchantments.add(new NewEnchantmentLevelEntry(enchantment, builder1.getLevel(enchantment)));
                }
            }
        }
        final ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(sourceItemStack.getEnchantments());
        for (RegistryEntry<Enchantment> entry : builder.getEnchantments()) {
            Enchantment enchantment = Registries.ENCHANTMENT.get(entry.getKey().get());
            if (enchantment != null) {
                enchantments.add(new NewEnchantmentLevelEntry(enchantment, builder.getLevel(enchantment)));
            }
        }
        return enchantments;
    }

    public static int getLevel(ItemStack itemStack, Enchantment enchantment) {
        if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
            ItemEnchantmentsComponent component = itemStack.get(DataComponentTypes.STORED_ENCHANTMENTS);
            if (component == null) return 0;
            return component.getLevel(enchantment);
        }
        return itemStack.getEnchantments().getLevel(enchantment);
    }

    public static int getAveragePower(Enchantment enchantment, int level) {
        return (enchantment.getMinPower(level) + enchantment.getMaxPower(level)) / 2;
    }

    public static List<NewEnchantmentLevelEntry> pickRandomEnchants(Random random, int targetQuantity, @Nullable List<NewEnchantmentLevelEntry> listToChooseFrom) {
        if (listToChooseFrom == null) listToChooseFrom = getCompatibleEnchantmentList(-1, -1, FeatureSet.empty(), true, false, false, true, 255, null, null);

//        System.out.println("RECEIVED LIST:");
//        for (NewEnchantmentLevelEntry entry : listToChooseFrom) {
//            System.out.println(entry.enchantment.getName(entry.level).getString());
//        }

        List<NewEnchantmentLevelEntry> chosenList = new ArrayList<>();

        List<NewEnchantmentLevelEntry> remainingList = new ArrayList<>(listToChooseFrom);
        int m = 0;
        while (true) {
//            System.out.println("REMAINING LIST:");
//            for (NewEnchantmentLevelEntry entry : remainingList) {
//                System.out.println(entry.enchantment.getWeight() + " " + entry.level);
//            }
            Optional<NewEnchantmentLevelEntry> entryToAdd = Weighting.getRandom(random, remainingList);
            if (entryToAdd.isEmpty()) {
                break;
            }
            chosenList.add(entryToAdd.get());
            m++;

            if (m >= targetQuantity) {
                break;
            }

            remainingList.removeIf(e -> !e.enchantment.canCombine(entryToAdd.get().enchantment));
        }

//        System.out.println("RANDOM LIST:");
//        for (NewEnchantmentLevelEntry entry : chosenList) {
//            System.out.println(entry.enchantment.getName(entry.level).getString());
//        }

        return chosenList;
    }

    public static List<NewEnchantmentLevelEntry> getCompatibleEnchantmentList(int power, int rarity, FeatureSet enabledFeatures, boolean treasureAllowed, boolean allowCurses, boolean onlyCurses, boolean requirePrimary, int levelsPerEnchantment, @Nullable ItemStack targetItemStack, @Nullable List<Enchantment> compatibleEnchList) {
        //if (targetItemStack == null) targetItemStack = ItemStack.EMPTY;
        if (compatibleEnchList == null) compatibleEnchList = new ArrayList<>();

        List<NewEnchantmentLevelEntry> filteredList = new ArrayList<>();
        //System.out.println("ALL DA ENCHANTMENTS!!!");
        for (Enchantment enchantment : Registries.ENCHANTMENT) {
            if (
                    !enchantment.isEnabled(enabledFeatures) ||
                            (!allowCurses && enchantment.isCursed()) ||
                            (onlyCurses && !enchantment.isCursed()) ||
                            enchantment.isTreasure() && !treasureAllowed ||
                            !enchantment.isAvailableForRandomSelection() ||
                            !compatibleEnchList.stream().filter(ench -> !ench.canCombine(enchantment)).toList().isEmpty() ||
                            targetItemStack != null &&
                                    (!(targetItemStack.isOf(Items.BOOK) || targetItemStack.isOf(Items.ENCHANTED_BOOK)) &&
                                            (!enchantment.isAcceptableItem(targetItemStack) || (requirePrimary && !enchantment.isPrimaryItem(targetItemStack))))
            )
                continue;
            int m = 0;
            for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                if (power >= 0 && (power < enchantment.getMinPower(i) || power > enchantment.getMaxPower(i)))
                    continue;
                filteredList.add(rarity >= 0 ? new NewEnchantmentLevelEntry(enchantment, i, rarity) : new NewEnchantmentLevelEntry(enchantment, i));
                if (m >= levelsPerEnchantment) break;
                m++;
            }
        }

//        System.out.println("TOTAL LIST:");
//        for (NewEnchantmentLevelEntry entry : filteredList) {
//            System.out.println(entry.enchantment.getName(entry.level).getString());
//        }

        filteredList.removeIf(e -> e.getWeight().getValue() == 0);

        return filteredList;
    }

    public static ItemStack applyEnchantments(ItemStack itemStack, List<NewEnchantmentLevelEntry> enchantmentsToApply) {
        ItemEnchantmentsComponent.Builder builder;

//        System.out.println("ADDING ENCHANTMENTS!");
//        for (NewEnchantmentLevelEntry entry : enchantmentsToApply) {
//            if (entry == null) continue;;
//            System.out.println(entry.enchantment.getName(entry.level));
//        }

        if (itemStack.isOf(Items.BOOK) && enchantmentsToApply.stream().anyMatch(Objects::nonNull)) {
            itemStack = itemStack.copyComponentsToNewStack(Items.ENCHANTED_BOOK, 1);
        }

        if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
            builder = new ItemEnchantmentsComponent.Builder(Objects.requireNonNull(itemStack.get(DataComponentTypes.STORED_ENCHANTMENTS)));
            for (NewEnchantmentLevelEntry entryToApply : enchantmentsToApply) {
                if (entryToApply != null) {
                    if (canCombineToUpgrade(itemStack, entryToApply)) {
                        builder.set(entryToApply.enchantment, entryToApply.level + 1);
                    } else if (builder.getEnchantments().stream().allMatch(e -> Objects.requireNonNull(Registries.ENCHANTMENT.get(e.getKey().get())).canCombine(entryToApply.enchantment))) {
                        builder.add(entryToApply.enchantment, entryToApply.level);
                    }
                }
            }
            itemStack.set(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());
        } else {
            builder = new ItemEnchantmentsComponent.Builder(itemStack.getEnchantments());
            for (NewEnchantmentLevelEntry entryToApply : enchantmentsToApply) {
                if (canCombineToUpgrade(itemStack, entryToApply)) {
                    builder.set(entryToApply.enchantment, entryToApply.level + 1);
                } else if (builder.getEnchantments().stream().allMatch(e -> Objects.requireNonNull(Registries.ENCHANTMENT.get(e.getKey().get())).canCombine(entryToApply.enchantment))) {
                    builder.add(entryToApply.enchantment, entryToApply.level);
                }
            }
            itemStack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
        }
        return itemStack;
    }

    public static ItemStack removeEnchantments(ItemStack itemStack, List<NewEnchantmentLevelEntry> enchantmentsToRemove) {
        ItemEnchantmentsComponent.Builder builder;

        if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
            builder = new ItemEnchantmentsComponent.Builder(Objects.requireNonNull(itemStack.get(DataComponentTypes.STORED_ENCHANTMENTS)));

            builder.remove(e -> enchantmentsToRemove.stream().anyMatch(r -> r != null && r.enchantment == Registries.ENCHANTMENT.get(e.getKey().get())));

            itemStack.set(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());

            if (builder.getEnchantments().isEmpty()) {
                itemStack = itemStack.copyComponentsToNewStack(Items.BOOK, 1);
            }
        } else {
            builder = new ItemEnchantmentsComponent.Builder(itemStack.getEnchantments());

            builder.remove(e -> enchantmentsToRemove.stream().anyMatch(r -> r != null && r.enchantment == Registries.ENCHANTMENT.get(e.getKey().get())));

            itemStack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
        }

        return itemStack;
    }

    public static ItemStack replaceEnchantments(ItemStack itemStack, List<NewEnchantmentLevelEntry> newEnchantmentList) {
        ItemEnchantmentsComponent.Builder builder;

        if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
            builder = new ItemEnchantmentsComponent.Builder(Objects.requireNonNull(itemStack.get(DataComponentTypes.STORED_ENCHANTMENTS)));
            builder.remove(e -> newEnchantmentList.stream().noneMatch(j -> j != null && j.enchantment == Registries.ENCHANTMENT.get(e.getKey().get())));

            for (NewEnchantmentLevelEntry entry : newEnchantmentList) {
                if (entry == null) continue;
                int thisLevel = builder.getLevel(entry.enchantment);
                if (thisLevel == 0) {
                    builder.add(entry.enchantment, entry.level);
                } else if (thisLevel != entry.level) {
                    builder.set(entry.enchantment, entry.level);
                }
            }

            itemStack.set(DataComponentTypes.STORED_ENCHANTMENTS, builder.build());

            if (builder.getEnchantments().isEmpty()) {
                itemStack = itemStack.copyComponentsToNewStack(Items.BOOK, 1);
            }
        } else {
            builder = new ItemEnchantmentsComponent.Builder(itemStack.getEnchantments());
            builder.remove(e -> newEnchantmentList.stream().noneMatch(j -> j != null && j.enchantment == Registries.ENCHANTMENT.get(e.getKey().get())));

            for (NewEnchantmentLevelEntry entry : newEnchantmentList) {
                if (entry == null) continue;
                int thisLevel = builder.getLevel(entry.enchantment);
                if (thisLevel == 0) {
                    builder.add(entry.enchantment, entry.level);
                } else if (thisLevel != entry.level) {
                    builder.set(entry.enchantment, entry.level);
                }
            }

            itemStack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
        }
        return itemStack;
    }

    public static boolean canApplyToItemStack(ItemStack itemStack, NewEnchantmentLevelEntry entry) {
        return itemStack.isOf(Items.BOOK) ||
                itemStack.isOf(Items.ENCHANTED_BOOK) &&
                        getEnchantments(itemStack).stream().allMatch(e -> e.canCombine(entry.enchantment)) ||
                entry.enchantment.isAcceptableItem(itemStack) &&
                        getEnchantments(itemStack).stream().allMatch(e -> e.canCombine(entry.enchantment))
                || canCombineToUpgrade(itemStack, entry);
    }

    public static boolean canCombineToUpgrade(ItemStack targetItemStack, NewEnchantmentLevelEntry entry) {
        return getEnchantments(targetItemStack).stream().anyMatch(e -> e == entry.enchantment && getLevel(targetItemStack, e) == entry.level);
    }

    public static class LibraryBonusWithCategory {

        public AtheneumConfig.LibraryBonuses bonuses;

        public AtheneumConfig.ComponentCategories category;

        public LibraryBonusWithCategory(AtheneumConfig.LibraryBonuses bonuses, AtheneumConfig.ComponentCategories category) {
            this.bonuses = bonuses;
            this.category = category;
        }
    }

    public static final int DEFAULT_POWER = 1;
    public static final int DEFAULT_RARITY = 0;
    public static final int DEFAULT_INSIGHT = 1;
    public static final int DEFAULT_CHAOS = 10;
    public static final int DEFAULT_SYNERGY = 1;
    public static final int DEFAULT_QUANTITY = 1;

    public static final List<BlockPos> POWER_PROVIDER_OFFSETS = BlockPos.stream(-2, 0, -2, 2, 2, 2).filter(pos -> Math.abs(pos.getX()) == 2 || Math.abs(pos.getZ()) == 2).map(BlockPos::toImmutable).toList();

    public enum TableLevelStats {
        MAX_POWER,
        MAX_STABILITY,
        MAX_INSIGHT,
        MAX_BOOKSHELVES,
        MAX_ARTIFACTS,
        MAX_TOMES,
        MAX_LAPIS
    }
    public enum TableScreens {
        NONE,
        GENERATE,
        TRANSFER,
        CURSED
    }
    public enum TableStats {
        POWER,
        MAX_POWER,
        MIN_QUANTITY,
        MAX_INSIGHT,
        RARITY_BONUS,
        MAX_SYNERGY,
        CHAOS,
        SYNERGY,
        INSIGHT,
        TREASURE_ALLOWED,
        CAN_CURSELIFT,
        ENCH_LEVEL,
        ENCH_XP,
        SCREEN_INDEX,
        XP_COST,
        MIN_LEVEL,
        TOME_COUNT,
        BOOKSHELF_COUNT,
        ARTIFACT_COUNT
    }
}
