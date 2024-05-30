package jpxl.atheneum.recipe;

import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import jpxl.atheneum.Atheneum;
import jpxl.atheneum.components.ModComponents;

public class CombineTomeRecipe extends SpecialCraftingRecipe {

    public CombineTomeRecipe(CraftingRecipeCategory craftingRecipeCategory) { super(craftingRecipeCategory); }

    @Nullable
    private Pair<ItemStack, ItemStack> findPair(RecipeInputInventory inventory) {
        ItemStack itemStack = null;
        ItemStack itemStack2 = null;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack3 = inventory.getStack(i);
            if (itemStack3.isEmpty()) continue;
            if (itemStack == null) {
                itemStack = itemStack3;
                continue;
            }
            if (itemStack2 == null) {
                itemStack2 = itemStack3;
                continue;
            }
            return null;
        }
        if (itemStack != null && itemStack2 != null && CombineTomeRecipe.canCombineStacks(itemStack, itemStack2)) {
            return Pair.of(itemStack, itemStack2);
        }
        return null;
    }

    private static boolean canCombineStacks(ItemStack first, ItemStack second) {
        return second.isOf(first.getItem()) &&
                first.getCount() == 1 &&
                second.getCount() == 1 &&
                first.contains(ModComponents.TOME_LEVEL) &&
                second.contains(ModComponents.TOME_LEVEL) &&
                first.get(ModComponents.TOME_LEVEL) < 5 &&
                second.get(ModComponents.TOME_LEVEL) < 5 &&
                first.get(ModComponents.TOME_LEVEL) == second.get(ModComponents.TOME_LEVEL);
    }

    @Override
    public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
        return this.findPair(recipeInputInventory) != null;
    }

    @Override
    public ItemStack craft(RecipeInputInventory recipeInputInventory, RegistryWrapper.WrapperLookup lookup) {
        Pair<ItemStack, ItemStack> pair = this.findPair(recipeInputInventory);
        if (pair == null) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = pair.getFirst();
        ItemStack itemStack2 = pair.getSecond();

        ItemStack itemStack3 = itemStack.copyComponentsToNewStack(itemStack.getItem(), 1);
        itemStack3.set(ModComponents.TOME_LEVEL, itemStack.get(ModComponents.TOME_LEVEL) + 1);

        return itemStack3;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Atheneum.COMBINE_TOME_RECIPE_RECIPE_SERIALIZER;
    }
}
