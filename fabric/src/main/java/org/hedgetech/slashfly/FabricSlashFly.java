package org.hedgetech.slashfly;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.hedgetech.slashfly.commands.CommandRegistry;

/**
 * Fabric Server Entry Point for SlashFly Mod
 */
public class FabricSlashFly implements ModInitializer {
    
    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
//        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                CommandRegistry.registerCommands(dispatcher)
        );

        ServerPlayConnectionEvents.DISCONNECT.register((serverPlayNetworkHandler, minecraftServer) ->
                Fly.savePlayer(serverPlayNetworkHandler.getPlayer())
        );

        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) ->
                Fly.initPlayer(serverPlayNetworkHandler.getPlayer())
        );

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                Fly.onPlayerRespawn(oldPlayer.getStringUUID(), newPlayer)
        );
    }

    /**
     * Default Constructor
     */
    public FabricSlashFly() { }
}
