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
        event.setNewSpeed(Fly.adjustBlockBreakSpeedForFlight(event.getEntity(), event.getNewSpeed()));
    }
}