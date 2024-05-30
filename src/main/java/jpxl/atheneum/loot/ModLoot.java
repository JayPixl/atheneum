package jpxl.atheneum.loot;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import jpxl.atheneum.Atheneum;
import jpxl.atheneum.items.ModItems;

public class ModLoot {
    public static void registerLootTables() {
        System.out.println("Registering Loot Tables for " + Atheneum.MOD_ID);

        LootTableEvents.MODIFY.register((key, tableBuilder, source) -> {
            if (source.isBuiltin()) {
                if (key.equals(LootTables.STRONGHOLD_LIBRARY_CHEST)) {
                    LootPool.Builder poolBuilder = getDefaultPool();

                    tableBuilder.pool(poolBuilder);
                } else if (key.equals(LootTables.BASTION_TREASURE_CHEST)) {
                    LootPool.Builder poolBuilder = getDefaultPool();

                    tableBuilder.pool(poolBuilder);
                } else if (key.equals(LootTables.ABANDONED_MINESHAFT_CHEST)) {
                    LootPool.Builder poolBuilder = getDefaultPool();

                    tableBuilder.pool(poolBuilder);
                } else if (key.equals(LootTables.BASTION_OTHER_CHEST)) {
                    LootPool.Builder poolBuilder = getDefaultPool();

                    tableBuilder.pool(poolBuilder);
                } else if (key.equals(LootTables.BASTION_BRIDGE_CHEST)) {
                    LootPool.Builder poolBuilder = getDefaultPool();

                    tableBuilder.pool(poolBuilder);
                } else if (key.equals(LootTables.BURIED_TREASURE_CHEST)) {
                    LootPool.Builder poolBuilder = getDefaultPool();

                    tableBuilder.pool(poolBuilder);
                } else if (key.equals(LootTables.DESERT_PYRAMID_CHEST)) {
                    LootPool.Builder poolBuilder = getDefaultPool();

                    tableBuilder.pool(poolBuilder);
                } else if (key.equals(LootTables.RUINED_PORTAL_CHEST)) {
                    LootPool.Builder poolBuilder = getDefaultPool();

                    tableBuilder.pool(poolBuilder);
                }
            }
        });
    }

    private static LootPool.Builder getDefaultPool() {
        return LootPool.builder()
                .rolls(UniformLootNumberProvider.create(0.0f, 5.0f))
                .with(ItemEntry.builder(Items.AIR).weight(5)
                        .apply(ApplyRandomTomeLevelLootFunction.builder()))
                .with(ItemEntry.builder(ModItems.ANCIENT_MANUSCRIPT).weight(10)
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 5.0f))))
                .with(ItemEntry.builder(ModItems.ANCIENT_TOME).weight(2)
                        .apply(ApplyRandomTomeLevelLootFunction.builder()))
                .with(ItemEntry.builder(ModItems.SHADOWBORNE_TOME)
                        .apply(ApplyRandomTomeLevelLootFunction.builder()))
                .with(ItemEntry.builder(ModItems.VERDANT_TOME)
                        .apply(ApplyRandomTomeLevelLootFunction.builder()))
                .with(ItemEntry.builder(ModItems.DREAMERS_TOME)
                        .apply(ApplyRandomTomeLevelLootFunction.builder()));
    }
}
