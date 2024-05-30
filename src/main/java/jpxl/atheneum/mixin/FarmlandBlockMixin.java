package jpxl.atheneum.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import jpxl.atheneum.enchantments.ModEnchantments;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin extends Block {
    public FarmlandBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(at = @At(value = "HEAD"), method = "onLandedUpon", locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void landingInject(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            if (EnchantmentHelper.getEquipmentLevel(ModEnchantments.SURE_STEP_ENCHANTMENT, player) > 0) {
                super.onLandedUpon(world, state, pos, entity, fallDistance);
                ci.cancel();
            }
        }
    }
}
