package jpxl.atheneum.enchantments;

import jpxl.atheneum.Atheneum;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    public static Enchantment SURE_STEP_ENCHANTMENT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(Atheneum.MOD_ID, "sure_step"),
            new SureStepEnchantment(
                    Enchantment.properties(
                            ItemTags.FOOT_ARMOR_ENCHANTABLE,
                            10,
                            1,
                            Enchantment.leveledCost(5, 6),
                            Enchantment.leveledCost(11, 6),
                            2,
                            EquipmentSlot.FEET
                    )
            )
    );

    public static Enchantment CURSE_OF_CHICKEN = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(Atheneum.MOD_ID, "curse_of_chicken"),
            new Enchantment(Enchantment.properties(
                    ItemTags.DURABILITY_ENCHANTABLE,
                    10,
                    5,
                    Enchantment.leveledCost(5, 5),
                    Enchantment.leveledCost(11, 6),
                    2,
                    EquipmentSlot.values()
            )) {
                @Override
                public boolean isCursed() {
                    return true;
                }
            }
    );

    public static void registerEnchantments() {
        Atheneum.LOGGER.info("Registering enchantments for " + Atheneum.MOD_ID);
    }
}
