package jpxl.atheneum;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import jpxl.atheneum.blocks.ModBlocks;
import jpxl.atheneum.blocks.TomeshelfBlock;
import jpxl.atheneum.properties.TomeSlot;

import java.util.List;
import java.util.Optional;

public class AtheneumDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(MyModelGenerator::new);
    }

    private static class MyModelGenerator extends FabricModelProvider {
        private MyModelGenerator(FabricDataOutput generator) {
            super(generator);
        }

        Model TEMPLATE_TOMESHELF = new Model(Optional.of(new Identifier(Atheneum.MOD_ID, "block/template_tomeshelf")), Optional.empty(), TextureKey.SIDE, TextureKey.FRONT, TextureKey.TOP);
        Model TEMPLATE_TOMESHELF_INVENTORY = new Model(Optional.of(new Identifier(Atheneum.MOD_ID, "block/template_tomeshelf_inventory")), Optional.of("_inventory"), TextureKey.SIDE, TextureKey.FRONT, TextureKey.TOP);
        Model TEMPLATE_TOMESHELF_BOOK = new Model(Optional.of(new Identifier(Atheneum.MOD_ID, "block/template_tomeshelf_book")), Optional.of("_book"), TextureKey.TEXTURE);

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

            for (TomeSlot slotId : TomeSlot.values()) {
                String path = "block/tomeshelf_book_" + slotId;
                Identifier bookIdentifier = TEMPLATE_TOMESHELF_BOOK.upload(new Identifier(Atheneum.MOD_ID, path), new TextureMap().put(TextureKey.TEXTURE, new Identifier(Atheneum.MOD_ID, path)), blockStateModelGenerator.modelCollector);
            }

            List.of(Pair.of(ModBlocks.OAK_TOMESHELF_BLOCK, ModBlocks.OAK_TOMESHELF_BLOCK_ITEM)).forEach(blockPair -> {
                Block block = (Block) blockPair.getFirst();
                Item blockItem = (Item) blockPair.getSecond();

                MultipartBlockStateSupplier blockStateSupplier = MultipartBlockStateSupplier.create(block);
//
//                //Identifier identifier2 = TexturedModel.ORIENTABLE_WITH_BOTTOM.upload(ModBlocks.TOMESHELF_BLOCK, "_inventory", blockStateModelGenerator.modelCollector);
//                Identifier inventoryModelId = TexturedModel.makeFactory(block2 -> new TextureMap().put(TextureKey.FRONT, TextureMap.getSubId(ModBlocks.OAK_TOMESHELF_BLOCK, "_front")).put(TextureKey.SIDE, TextureMap.getSubId(ModBlocks.OAK_TOMESHELF_BLOCK, "_side")).put(TextureKey.TOP, TextureMap.getSubId(ModBlocks.OAK_TOMESHELF_BLOCK, "_top")), Models.ORIENTABLE).upload(ModBlocks.OAK_TOMESHELF_BLOCK, "_inventory", blockStateModelGenerator.modelCollector);
//                blockStateModelGenerator.registerParentedItemModel(ModBlocks.OAK_TOMESHELF_BLOCK_ITEM, inventoryModelId);
//
                Identifier shelfIdentifier = TEMPLATE_TOMESHELF.upload(block, new TextureMap().put(TextureKey.FRONT, TextureMap.getSubId(block, "_front")).put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).put(TextureKey.TOP, TextureMap.getSubId(block, "_top")), blockStateModelGenerator.modelCollector);
                Identifier inventoryIdentifier = TEMPLATE_TOMESHELF_INVENTORY.upload(block, new TextureMap().put(TextureKey.FRONT, TextureMap.getSubId(block, "_front")).put(TextureKey.SIDE, TextureMap.getSubId(block, "_side")).put(TextureKey.TOP, TextureMap.getSubId(block, "_top")), blockStateModelGenerator.modelCollector);
                blockStateModelGenerator.registerParentedItemModel(blockItem, ModelIds.getBlockSubModelId(block, "_inventory"));

                //Identifier identifier = TexturedModel.makeFactory(block2 -> new TextureMap().put(TextureKey.SIDE, TextureMap.getSubId(ModBlocks.OAK_TOMESHELF_BLOCK, "_side")).put(TextureKey.TOP, TextureMap.getSubId(ModBlocks.OAK_TOMESHELF_BLOCK, "_top")), new Model(Optional.of(new Identifier("minecraft:block/block")), Optional.empty(), TextureKey.SIDE, TextureKey.TOP)).upload(ModBlocks.OAK_TOMESHELF_BLOCK, blockStateModelGenerator.modelCollector);
                List.of(Pair.of(Direction.NORTH, VariantSettings.Rotation.R0), Pair.of(Direction.EAST, VariantSettings.Rotation.R90), Pair.of(Direction.SOUTH, VariantSettings.Rotation.R180), Pair.of(Direction.WEST, VariantSettings.Rotation.R270)).forEach(pair -> {
                    Direction direction = pair.getFirst();
                    VariantSettings.Rotation rotation = pair.getSecond();
                    When.PropertyCondition propertyCondition = When.create().set(Properties.HORIZONTAL_FACING, direction);
                    blockStateSupplier.with(propertyCondition, BlockStateVariant.create().put(VariantSettings.MODEL, shelfIdentifier).put(VariantSettings.Y, rotation).put(VariantSettings.UVLOCK, true));
                    for (TomeSlot slotId : TomeSlot.values()) {
                        String path = "block/tomeshelf_book_" + slotId;
                        //Identifier bookIdentifier = TEMPLATE_TOMESHELF_BOOK.upload(block, suffix, new TextureMap().put(TextureKey.TEXTURE, TextureMap.getSubId(block, suffix)), blockStateModelGenerator.modelCollector);
                        blockStateSupplier.with(When.allOf(propertyCondition, When.create().set(TomeshelfBlock.TOME_SLOT, slotId)), BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier(Atheneum.MOD_ID, path)).put(VariantSettings.Y, rotation));
                    }
                });

                blockStateModelGenerator.blockStateCollector.accept(blockStateSupplier);

            });
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
//            Model inventoryModel = new Model(Optional.empty(), Optional.of("_inventory"), TextureKey.FRONT, TextureKey.SIDE, TextureKey.TOP);
//            //inventoryModel.upload(ModBlocks.TOMESHELF_BLOCK, TextureMap.of(TextureKey.FRONT, ModelIds.getBlockSubModelId(ModBlocks.TOMESHELF_BLOCK, "_inventory")));
//            TexturedModel.ORIENTABLE_WITH_BOTTOM.get(ModBlocks.TOMESHELF_BLOCK).upload(ModBlocks.TOMESHELF_BLOCK);
//            itemModelGenerator.register(ModBlocks.TOMESELF_BLOCK_ITEM, Models.ORIENTABLE_WITH_BOTTOM);
        }
    }
}
