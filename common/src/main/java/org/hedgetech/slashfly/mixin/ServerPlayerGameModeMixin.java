package org.hedgetech.slashfly.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.level.GameType;
import org.hedgetech.slashfly.Constants;
import org.hedgetech.slashfly.platform.Services;
import org.hedgetech.slashfly.saveddata.PlayerSavedData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Final @Shadow
    protected ServerPlayer player;

    @Shadow
    private GameType gameModeForPlayer;

    @Unique
    private GameType slashFly$previousGameMode;

    @Unique
    private boolean slashFly$wasFlying;

    @Inject(method = "changeGameModeForPlayer", at = @At("HEAD"))
    private void captureOldMode(GameType newMode, CallbackInfoReturnable<Boolean> cir) {
        slashFly$previousGameMode = gameModeForPlayer;
        slashFly$wasFlying = player.getAbilities().flying;
    }

    @Inject(method = "changeGameModeForPlayer", at = @At("RETURN"))
    private void fireEvent(GameType requestedMode, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;

        var oldMode = slashFly$previousGameMode;
        var newMode = gameModeForPlayer;

        if (oldMode == newMode) return;

        //noinspection resource
        var server = player.level().getServer();

        if (Services.PERMISSIONS.hasPermission(player, Constants.FLY_TOGGLE_PERM, PermissionLevel.ALL)) {
            if (slashFly$wasFlying) {
                player.getAbilities().mayfly = true;
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
            }
        }

        PlayerSavedData.ofServer(server).updatePlayerAbilities(player);
    }
}
