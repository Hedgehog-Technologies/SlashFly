package org.hedgetech.slashfly;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.hedgetech.slashfly.saveddata.PlayerSavedData;

import java.util.Objects;

public class Fly {
    public static void initPlayer(ServerPlayer player, MinecraftServer server) {
        var savedData = PlayerSavedData.ofServer(server);
        var playerData = savedData.getPlayerData(player.getStringUUID());

        var playerAbilities = player.getAbilities();
        playerAbilities.mayfly = player.isCreative() || player.isSpectator() || playerData.getMayFly();
        playerAbilities.flying = ((player.isCreative() || player.isSpectator()) && !player.onGround()) || playerData.getIsFlying();
        playerAbilities.setFlyingSpeed(playerData.getFlightSpeed());
        player.onUpdateAbilities();

        if (playerAbilities.mayfly) {
            player.sendSystemMessage(Component.literal("Flying enabled and speed set to " + playerData.getFlightSpeed() * 10.0f + "!"));
        }
    }

    public static void savePlayer(Player player, MinecraftServer server) {
        var savedData = PlayerSavedData.ofServer(server);
        var playerData = savedData.getPlayerData(player);
        savedData.togglePlayerFlight(player, playerData.getMayFly());
    }

    public static void onPlayerRespawn(String uuidString, Player player) {
        //noinspection resource
        var savedData = PlayerSavedData.ofServer(player.level().getServer());
        var playerData = savedData.getPlayerData(player);

        savedData.togglePlayerFlight(player, playerData.getMayFly());
        savedData.setPlayerFlightSpeed(player, playerData.getFlightSpeed() * 10.0f);
    }

    public static int toggleFly(CommandSourceStack source) {
        var player = source.getPlayer();
        assert player != null;

        var savedData = PlayerSavedData.ofServer(source.getServer());
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

        var savedData = PlayerSavedData.ofServer(source.getServer());
        savedData.setPlayerFlightSpeed(player, speed);
        source.sendSuccess(() -> Component.literal("Flying speed set to " + speed + "!"), false);

        return 1;
    }

    public static float adjustBlockBreakSpeedForFlight(Player player, float currentSpeed) {
        var abilities = player.getAbilities();

        if (!abilities.flying) {
            return currentSpeed;
        }

        float speed = currentSpeed;

        if (!player.onGround()) {
            speed *= 5.0F;
        }

        if (player.isEyeInFluid(FluidTags.WATER)) {
            speed /= (float) Objects.requireNonNull(player.getAttribute(Attributes.SUBMERGED_MINING_SPEED)).getValue();
        }

        return speed;
    }
}
