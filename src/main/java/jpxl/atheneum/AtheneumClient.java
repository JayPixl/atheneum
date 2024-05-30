package jpxl.atheneum;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import jpxl.atheneum.ench.table.NewEnchantmentScreen;

@Environment(EnvType.CLIENT)
public class AtheneumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(Atheneum.NEW_ENCHANTMENT_SCREEN_HANDLER, NewEnchantmentScreen::new);
    }
}
