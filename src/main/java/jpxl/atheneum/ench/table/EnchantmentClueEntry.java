package jpxl.atheneum.ench.table;

import net.minecraft.enchantment.Enchantment;

public class EnchantmentClueEntry {
    public final Enchantment enchantment;
    public final int level;
    public final int accuracy;

    public EnchantmentClueEntry (Enchantment enchantment, int level, int accuracy) {
        this.enchantment = enchantment;
        this.level = level;
        this.accuracy = accuracy;
    }

    public EnchantmentClueEntry empty(int accuracy) {
        return new EnchantmentClueEntry(null, 1, accuracy);
    }
}
