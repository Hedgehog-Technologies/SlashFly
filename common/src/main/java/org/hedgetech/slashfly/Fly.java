package org.hedgetech.slashfly;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.hedgetech.slashfly.saveddata.PlayerSavedData;

public class Fly {
    public static void initPlayer(ServerPlayer player) {
        var savedData = PlayerSavedData.ofServer(player.getServer());
        var playerData = savedData.getPlayerData(player.getStringUUID());

        var playerAbilities = player.getAbilities();
        playerAbilities.mayfly = playerData.getMayFly();
        playerAbilities.flying = playerData.getIsFlying();
        playerAbilities.setFlyingSpeed(playerData.getFlightSpeed());
        player.onUpdateAbilities();

        if (playerData.getMayFly()) {
            player.sendSystemMessage(Component.literal("Flying enabled and speed set to " + playerData.getFlightSpeed() * 10.0f + "!"));
        }
    }

    public static void savePlayer(Player player) {
        var savedData = PlayerSavedData.ofServer(player.getServer());
        var playerData = savedData.getPlayerData(player.getStringUUID());
        savedData.togglePlayerFlight(player, playerData.getMayFly());
    }

    public static void onPlayerRespawn(String uuidString, Player player) {
        var savedData = PlayerSavedData.ofServer(player.getServer());
        var playerData = savedData.getPlayerData(uuidString);

        savedData.togglePlayerFlight(player, playerData.getMayFly());
        savedData.setPlayerFlightSpeed(player, playerData.getFlightSpeed() * 10.0f);
    }

    public static int toggleFly(CommandSourceStack source) {
        var player = source.getPlayer();
        assert player != null;

        var savedData = PlayerSavedData.ofServer(player.getServer());
        if (savedData.togglePlayerFlight(player)) {
            source.sendSuccess(() -> Component.literal("Flying enabled!"), false);
        } else {
            source.sendSuccess(() -> Component.literal("Flying disabled!"), false);
        }

        return 1;
    }

    public static int setFlySpeed(CommandSourceStack source, float speed) {
        var player = source.getPlayer();
        assert player != null;

        var savedData = PlayerSavedData.ofServer(player.getServer());
        savedData.setPlayerFlightSpeed(player, speed);
        source.sendSuccess(() -> Component.literal("Flying speed set to " + speed + "!"), false);

        return 1;
    }
}
