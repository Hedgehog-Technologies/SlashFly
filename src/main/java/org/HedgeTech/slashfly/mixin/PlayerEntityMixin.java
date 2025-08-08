package org.HedgeTech.slashfly.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @ModifyExpressionValue(
            method = "getBlockBreakingSpeed",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isOnGround()Z")
    )
    private boolean allowFastBreakWhileFlying(boolean original) {
        var player = (PlayerEntity) (Object) this;

        if (!original && !player.isCreative() && !player.isSpectator()) {
            return true;
        }

        return original;
    }
}
