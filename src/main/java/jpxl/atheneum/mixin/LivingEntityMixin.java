package jpxl.atheneum.mixin;

import jpxl.atheneum.enchantments.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    private World myWorld = ((LivingEntity)(Object) this).getWorld();

    @Inject(method = "applyDamage", at = @At("TAIL"))
    protected void applyDamage(DamageSource source, float amount, CallbackInfo ci) {
        Entity attacker = source.getAttacker();
        if (attacker != null) {
            if (attacker instanceof PlayerEntity) {
                int poultrificationLevel = EnchantmentHelper.getEquipmentLevel(ModEnchantments.POULTRIFICATION_CURSE, (LivingEntity) attacker);
                if (poultrificationLevel > 0 && ((PlayerEntity) attacker).getRandom().nextInt(Math.max(1, ModEnchantments.POULTRIFICATION_CURSE.getMaxLevel() + 5 - poultrificationLevel)) == 0) {
                    if (myWorld == null || myWorld.isClient()) return;
                    ChickenEntity chickenEntity = EntityType.CHICKEN.create(myWorld);
                    if (chickenEntity == null) return;
                    chickenEntity.setBreedingAge(-24000);
                    chickenEntity.refreshPositionAndAngles(((LivingEntity)(Object) this).getBlockPos(), ((LivingEntity)(Object) this).bodyYaw, ((LivingEntity)(Object) this).getPitch());
                    myWorld.spawnEntity(chickenEntity);
                }
            }
        }
    }

}
