package org.hedgetech.slashfly;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import org.hedgetech.slashfly.commands.CommandRegistry;

import java.util.Objects;

/**
 * SlashFly's Forge Entry Point
 */
@Mod(Constants.MOD_ID)
public class ForgeSlashFly {
    /**
     * SlashFly constructor - entry point for Forge Mod Loader
     */
    public ForgeSlashFly() {
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
//        Constants.LOG.info("Hello Forge world!");
        CommonClass.init();

        RegisterCommandsEvent.BUS.addListener(ForgeSlashFly::registerCommandsEventHandler);
        PlayerEvent.PlayerLoggedOutEvent.BUS.addListener(ForgeSlashFly::playerLoggedOutEventHandler);
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(ForgeSlashFly::playerLoggedInEventHandler);
        PlayerEvent.PlayerRespawnEvent.BUS.addListener(ForgeSlashFly::playerRespawnEventHandler);
        PlayerEvent.BreakSpeed.BUS.addListener(ForgeSlashFly::playerBreakSpeedEventHandler);
    }

    private static void registerCommandsEventHandler(RegisterCommandsEvent event) {
        CommandRegistry.registerCommands(event.getDispatcher());
    }

    private static void playerLoggedOutEventHandler(PlayerEvent.PlayerLoggedOutEvent event) {
        //noinspection resource
        Fly.savePlayer(event.getEntity(), event.getEntity().level().getServer());
    }

    private static void playerLoggedInEventHandler(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            //noinspection resource
            Fly.initPlayer(serverPlayer, serverPlayer.level().getServer());
        }
    }

    private static void playerRespawnEventHandler(PlayerEvent.PlayerRespawnEvent event) {
        var player = event.getEntity();
        Fly.onPlayerRespawn(player.getStringUUID(), player);
    }

    private static void playerBreakSpeedEventHandler(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        var abilities = player.getAbilities();
        var onGround = player.onGround();
        var inWater = player.isEyeInFluidType(ForgeMod.WATER_TYPE.get());

        if (!onGround && abilities.flying) {
            event.setNewSpeed(event.getNewSpeed() * 5.0f);
        }

        if (inWater && abilities.flying) {
            event.setNewSpeed(event.getNewSpeed() / (float) Objects.requireNonNull(player.getAttribute(Attributes.SUBMERGED_MINING_SPEED)).getValue());
        }
    }
}