package org.HedgeTech.slashfly;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.HedgeTech.slashfly.states.StateSaverAndLoader;

public class SlashFly implements ModInitializer {
    public static final String MOD_ID = "HedgeTech.SlashFly";

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            CommandRegistry.registerCommands(dispatcher)
        );

        ServerPlayConnectionEvents.DISCONNECT.register((serverPlayNetworkHandler, minecraftServer) -> {
            var player = serverPlayNetworkHandler.getPlayer();
            var playerData = StateSaverAndLoader.getPlayerState(player);
            var playerAbilities = player.getAbilities();

            playerData.inFlight = !player.groundCollision && (playerAbilities.flying || playerAbilities.allowFlying);
        });

        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            var player = serverPlayNetworkHandler.getPlayer();
            var playerData = StateSaverAndLoader.getPlayerState(player);

            if (playerData.inFlight) {
                var playerAbilities = player.getAbilities();
                playerAbilities.flying = true;
                playerAbilities.allowFlying = true;
                player.sendAbilitiesUpdate();
            }
        });
    }
}
