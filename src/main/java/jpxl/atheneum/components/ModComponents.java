package jpxl.atheneum.components;

import jpxl.atheneum.Atheneum;
import net.minecraft.component.DataComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public class ModComponents {

    protected static final DataComponentType.Builder<Integer> TOME_LEVEL_BUILDER = DataComponentType.builder();

    public static final DataComponentType<Integer> TOME_LEVEL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            new Identifier(Atheneum.MOD_ID, "tome_level"),
            TOME_LEVEL_BUILDER.codec(Codecs.rangedInt(1, 5)).packetCodec(PacketCodecs.VAR_INT).build()
    );

}
