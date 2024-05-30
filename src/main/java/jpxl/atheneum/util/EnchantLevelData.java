package jpxl.atheneum.util;

import net.minecraft.nbt.NbtCompound;
import jpxl.atheneum.ench.table.NewEnchantmentHelper;

public class EnchantLevelData {
    public static void addEnchantmentXP(IEntityDataSaver player, int amount) {
        NbtCompound nbt = getNbt(player);
        int enchLevel = nbt.getInt("level");
        int enchXP = nbt.getInt("xp");
        int maxXp = NewEnchantmentHelper.getEnchLevelXpPerLevel(enchLevel);

        System.out.println("OLD ENCH STATS: Level " + enchLevel + ", " + enchXP + " XP, Level max: " + maxXp + ", Amount to add: " + amount);

        int newAmount = enchXP + amount;
        if (newAmount >= maxXp) {
            if (enchLevel >= 15) {
                enchLevel = 15;
                enchXP = maxXp;
            } else {
                enchLevel += 1;
                enchXP = newAmount - maxXp;
            }
        } else {
            enchXP  = newAmount;
        }
        //System.out.println("NEW ENCH STATS: Level " + enchLevel + ", " + enchXP + " XP, Level max: " + maxXp);

        nbt.putInt("level", enchLevel);
        nbt.putInt("xp", enchXP);
    }

    public static PlayerEnchantLevelEntry getEnchantmentLevels(IEntityDataSaver player) {
        NbtCompound nbt = getNbt(player);
        int enchLevel = nbt.getInt("level");
        int enchXP = nbt.getInt("xp");

        return new PlayerEnchantLevelEntry(player, enchLevel, enchXP);
    }

    private static NbtCompound getNbt(IEntityDataSaver player) {
        NbtCompound nbt = player.getPersistentData();
        int enchLevel = nbt.getInt("level");
        int enchXp = nbt.getInt("xp");
        if (enchLevel == 0) {
            nbt.putInt("level", 1);
            nbt.putInt("xp", 0);
        }
        if (enchXp < 0) {
            nbt.putInt("xp", 0);
            //System.out.println("RESETTING XP!");
        }
        return nbt;
    }

    public static void resetEnchantingLevels(IEntityDataSaver player) {
        NbtCompound nbt = player.getPersistentData();
        nbt.putInt("level", 1);
        nbt.putInt("xp", 0);
    }
}
