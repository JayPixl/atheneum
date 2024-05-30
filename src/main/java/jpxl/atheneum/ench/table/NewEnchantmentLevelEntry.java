package jpxl.atheneum.ench.table;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.collection.Weighted;

public class NewEnchantmentLevelEntry
        extends Weighted.Absent {
    public final Enchantment enchantment;
    public final int level;

    public NewEnchantmentLevelEntry(Enchantment enchantment, int level) {
        super(enchantment.getWeight());
        this.enchantment = enchantment;
        this.level = level;
    }

    public NewEnchantmentLevelEntry(Enchantment enchantment, int level, int rarity) {
        super(Math.max(0, enchantment.getWeight() + Math.round((((float) rarity / 2.5f) - 1f) * (float) (4 - enchantment.getWeight()))));
        this.enchantment = enchantment;
        this.level = level;
    }
}