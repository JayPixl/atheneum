package jpxl.atheneum.properties;

import net.minecraft.util.StringIdentifiable;

public enum TomeSlot implements StringIdentifiable {
    EMPTY("empty"),
    ANCIENT_TOME("ancient_tome"),
    SHADOWBORNE_TOME("shadowborne_tome"),
    VERDANT_TOME("verdant_tome"),
    DREAMERS_TOME("dreamers_tome");

    private final String name;

    TomeSlot(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
