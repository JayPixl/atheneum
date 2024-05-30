/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package jpxl.atheneum.loot;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import jpxl.atheneum.Atheneum;
import jpxl.atheneum.components.ModComponents;

public class ApplyRandomTomeLevelLootFunction
        extends ConditionalLootFunction {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Codec<RegistryEntryList<Enchantment>> ENCHANTMENT_LIST_CODEC = Registries.ENCHANTMENT.getEntryCodec().listOf().xmap(RegistryEntryList::of, enchantments -> enchantments.stream().toList());
    public static final MapCodec<ApplyRandomTomeLevelLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> ApplyRandomTomeLevelLootFunction.addConditionsField(instance).apply(instance, ApplyRandomTomeLevelLootFunction::new));

    ApplyRandomTomeLevelLootFunction(List<LootCondition> conditions) {
        super(conditions);
    }

    public LootFunctionType<ApplyRandomTomeLevelLootFunction> getType() {
        return Atheneum.APPLY_RANDOM_TOME_LEVEL;
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        Random random = context.getRandom();
        List<TomeLevelWeightEntry> tomeEntryList = List.of(
                new TomeLevelWeightEntry(1, 20),
                new TomeLevelWeightEntry(2, 8),
                new TomeLevelWeightEntry(3, 4),
                new TomeLevelWeightEntry(4, 2),
                new TomeLevelWeightEntry(5, 1)
        );
        TomeLevelWeightEntry chosenEntry = Weighting.getRandom(random, tomeEntryList).get();
        stack.set(ModComponents.TOME_LEVEL, chosenEntry.level);
        return stack;
    }

    public static Builder create() {
        return new Builder();
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return ApplyRandomTomeLevelLootFunction.builder(conditions -> new ApplyRandomTomeLevelLootFunction(conditions));
    }

    public static class Builder
            extends ConditionalLootFunction.Builder<Builder> {
        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        @Override
        public LootFunction build() {
            return new ApplyRandomTomeLevelLootFunction(this.getConditions());
        }

//        @Override
//        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
//            return this.getThisBuilder();
//        }
    }
}

