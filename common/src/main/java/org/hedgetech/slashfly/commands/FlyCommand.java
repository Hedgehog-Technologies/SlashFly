package org.hedgetech.slashfly.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.PermissionLevel;
import org.hedgetech.slashfly.Constants;
import org.hedgetech.slashfly.Fly;
import org.hedgetech.slashfly.platform.Services;

public class FlyCommand {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fly")
                .requires(source -> Services.PERMISSIONS.hasPermission(source, Constants.FLY_TOGGLE_PERM, PermissionLevel.ALL))
                .executes(context -> Fly.toggleFly(context.getSource()))
                .then(Commands.argument("speed", FloatArgumentType.floatArg(0.1f, 100.0f))
                        .requires(source -> Services.PERMISSIONS.hasPermission(source, Constants.FLY_SPEED_PERM, PermissionLevel.ALL))
                        .executes(context ->
                                Fly.setFlySpeed(context.getSource(), FloatArgumentType.getFloat(context, "speed")))));
    }
}
