package jpxl.atheneum.util;

public class PlayerEnchantLevelEntry {
    private final IEntityDataSaver player;
    private final int level;
    private final int xp;

    public PlayerEnchantLevelEntry(IEntityDataSaver player, int level, int xp) {
        this.player = player;
        this.level = level;
        this.xp = xp;
    }

    public final int getLevel() {
        return level;
    }

    public final int getXp() {
        return xp;
    }
}
