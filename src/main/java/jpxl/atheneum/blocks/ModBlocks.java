package jpxl.atheneum.blocks;


import jpxl.atheneum.Atheneum;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block OAK_TOMESHELF_BLOCK = Registry.register(
            Registries.BLOCK,
            new Identifier(Atheneum.MOD_ID, "oak_tomeshelf"),
            new TomeshelfBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS))
    );
    public static final BlockEntityType<TomeshelfBlockEntity> TOMESHELF_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(Atheneum.MOD_ID, "tomeshelf"),
            BlockEntityType.Builder.create(TomeshelfBlockEntity::new, OAK_TOMESHELF_BLOCK).build()
    );
    public static final Item OAK_TOMESHELF_BLOCK_ITEM = Registry.register(
            Registries.ITEM,
            new Identifier(Atheneum.MOD_ID, "oak_tomeshelf"),
            new BlockItem(OAK_TOMESHELF_BLOCK, new Item.Settings())
    );

    public static void registerModBlocks() {
        Atheneum.LOGGER.info("Registering ModBlocks for " + Atheneum.MOD_ID);
    }
}
