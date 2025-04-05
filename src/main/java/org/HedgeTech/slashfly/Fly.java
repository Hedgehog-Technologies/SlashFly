package org.HedgeTech.slashfly;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class Fly {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("fly")
                .executes(context -> toggleFly(context.getSource()))
                .then(CommandManager.argument("speed", FloatArgumentType.floatArg(0.1f, 100.0f))
                        .executes(context ->
                            setFlySpeed(context.getSource(), FloatArgumentType.getFloat(context, "speed"))
                        )
                )
        );
    }

    private static int toggleFly(ServerCommandSource source) {
        var player = source.getPlayer();

        if (player != null) {
            var playerAbilities = player.getAbilities();
            var flyEnabled = playerAbilities.flying || playerAbilities.allowFlying;

            if (flyEnabled) {
                // Disable flying
                playerAbilities.flying = false;
                playerAbilities.allowFlying = false;

                source.sendFeedback(() -> Text.literal("Flying disabled!"), false);
            } else {
                // Enable flying
                playerAbilities.flying = true;
                playerAbilities.allowFlying = true;

                source.sendFeedback(() -> Text.literal("Flying enabled!"), false);
            }

            player.sendAbilitiesUpdate();
        }
        return 1;
    }

    private static int setFlySpeed(ServerCommandSource source, float speed) {
        var player = source.getPlayer();

        if (player != null) {
            var playerAbilities = player.getAbilities();
            var flyEnabled = playerAbilities.flying || playerAbilities.allowFlying;

            if (flyEnabled) {
                playerAbilities.setFlySpeed(speed / 10.0f); // MC uses a scale of 0.0 to 1.0 for fly speed
                player.sendAbilitiesUpdate();
                source.sendFeedback(() -> Text.literal("Flying speed set to " + speed + "!"), false);
            } else {
                source.sendFeedback(() -> Text.literal("You must be flying to set the speed! Use /fly to enable flying first."), false);
            }
        }

        return 1;
    }
}
