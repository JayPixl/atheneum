package jpxl.atheneum.tags;

import jpxl.atheneum.Atheneum;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public class Blocks {
        public static final TagKey<Block> ENCHANTING_BLOCK = TagKey.of(RegistryKeys.BLOCK, new Identifier(Atheneum.MOD_ID, "enchanting_block"));
    }
    public class Items {
        public static final TagKey<Item> TOME_ITEM = TagKey.of(RegistryKeys.ITEM, new Identifier(Atheneum.MOD_ID, "tome_item"));
        public static final TagKey<Item> ENCHANTING_CATALYST = TagKey.of(RegistryKeys.ITEM, new Identifier(Atheneum.MOD_ID, "enchanting_catalyst"));
    }
}
