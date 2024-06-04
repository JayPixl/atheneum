package jpxl.atheneum.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import jpxl.atheneum.ench.table.NewEnchantmentScreenHandler;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {
    @Inject(at = @At("HEAD"), method = "createScreenHandlerFactory", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void createScreenHandlerFactoryMixin(BlockState state, World world, BlockPos pos, CallbackInfoReturnable<NamedScreenHandlerFactory> cir) {
//        System.out.println("LOADING MOD FROM MIXIN");
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EnchantingTableBlockEntity) {
            Text text = ((Nameable)((Object)blockEntity)).getDisplayName();
            cir.setReturnValue(new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> new NewEnchantmentScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)), text));
        } else {
            cir.setReturnValue(null);
        }
    }
}
