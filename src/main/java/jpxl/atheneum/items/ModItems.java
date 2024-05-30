package jpxl.atheneum.items;

import jpxl.atheneum.Atheneum;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import jpxl.atheneum.components.ModComponents;

public class ModItems {

    public static final Item ANCIENT_MANUSCRIPT = Registry.register(
            Registries.ITEM,
            new Identifier(Atheneum.MOD_ID, "ancient_manuscript"),
            new Item(new Item.Settings())
    );

    public static final Item LAPIS_TENEBRIS = Registry.register(
            Registries.ITEM,
            new Identifier(Atheneum.MOD_ID, "lapis_tenebris"),
            new Item(new Item.Settings())
    );

    public static final Item ANCIENT_TOME = registerTome("ancient_tome");

    public static final Item SHADOWBORNE_TOME = registerTome("shadowborne_tome");

    public static final Item DREAMERS_TOME = registerTome("dreamers_tome");

    public static final Item VERDANT_TOME = registerTome("verdant_tome");


    private static Item registerTome(String name) {
        return Registry.register(
                Registries.ITEM,
                new Identifier(Atheneum.MOD_ID, name),
                new TomeItem(new Item.Settings().maxCount(1).component(ModComponents.TOME_LEVEL, 1))
        );
    }

    public static void registerModItems() {
        Atheneum.LOGGER.info("Registering ModItems for " + Atheneum.MOD_ID);
    }
}
