package jpxl.atheneum.items;

import net.minecraft.client.item.TooltipData;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import jpxl.atheneum.Atheneum;
import jpxl.atheneum.components.ModComponents;

import java.util.List;
import java.util.Optional;

public class TomeItem extends Item {

    public TomeItem(Settings settings) {
        super(settings);
    }

    protected static final List<String> suffixes = List.of("0", "I", "II", "III", "IV", "V");

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        setTomeLevel(stack, 1);
        return stack;
    }

    public static void setTomeLevel(ItemStack stack, int level) {
        stack.set(ModComponents.TOME_LEVEL, level);
    }

    public static int getTomeLevel(ItemStack stack) {
        return stack.get(ModComponents.TOME_LEVEL);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
    }

    @Override
    public Text getName(ItemStack stack) {
        int level = getTomeLevel(stack);
        String suffix = " " + suffixes.get(level);
        return Text.translatable("item." + Atheneum.MOD_ID + "." + Registries.ITEM.getId(stack.getItem()).getPath()).append(Text.literal(suffix));
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        return super.getTooltipData(stack);
    }
}
