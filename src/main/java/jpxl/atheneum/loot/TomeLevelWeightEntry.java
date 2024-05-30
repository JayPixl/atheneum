package jpxl.atheneum.loot;

import net.minecraft.util.collection.Weighted;

public class TomeLevelWeightEntry extends Weighted.Absent {
    public final int level;

    public TomeLevelWeightEntry(int level, int weight) {
        super(weight);
        this.level = level;
    }
}
