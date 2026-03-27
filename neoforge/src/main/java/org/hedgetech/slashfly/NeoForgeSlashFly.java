package org.hedgetech.slashfly;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.hedgetech.slashfly.commands.CommandRegistry;

import java.util.Objects;

/**
 * SlashFly's NeoForge Entry Point
 */
@Mod(Constants.MOD_ID)
public class NeoForgeSlashFly {
    /**
     * SlashFly constructor - entry point for NeoForge Mod Loader
     * @param eventBus - NeoForge EventBus
     */
    public NeoForgeSlashFly(IEventBus eventBus) {

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
//        Constants.LOG.info("Hello NeoForge world!");
        CommonClass.init();

        NeoForge.EVENT_BUS.addListener(NeoForgeSlashFly::onCommandRegister);
        NeoForge.EVENT_BUS.addListener(NeoForgeSlashFly::onPlayerLogout);
        NeoForge.EVENT_BUS.addListener(NeoForgeSlashFly::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(NeoForgeSlashFly::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(NeoForgeSlashFly::onPlayerBreakSpeed);
    }

    private static void onCommandRegister(RegisterCommandsEvent event) {
        CommandRegistry.registerCommands(event.getDispatcher());
    }

    private static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        //noinspection resource
        Fly.savePlayer(event.getEntity(), event.getEntity().level().getServer());
    }

    private static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sPlayer) {
            //noinspection resource
            Fly.initPlayer(sPlayer, event.getEntity().level().getServer());
        }
    }

    private static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        var player = event.getEntity();
        Fly.onPlayerRespawn(player.getStringUUID(), player);
    }

    private static void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        var player = event.getEntity();
        var abilities = player.getAbilities();
        var onGround = player.onGround();
        var inWater = player.isEyeInFluid(FluidTags.WATER);

        if (!onGround && abilities.flying) {
            event.setNewSpeed(event.getNewSpeed() * 5.0f);
        }

        if (inWater && abilities.flying) {
            event.setNewSpeed(event.getNewSpeed() / (float) Objects.requireNonNull(player.getAttribute(Attributes.SUBMERGED_MINING_SPEED)).getValue());
        }
    }
}