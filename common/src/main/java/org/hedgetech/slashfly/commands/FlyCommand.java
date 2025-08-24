package org.hedgetech.slashfly.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.hedgetech.slashfly.Fly;

public class FlyCommand {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fly")
                .executes(context -> Fly.toggleFly(context.getSource()))
                .then(Commands.argument("speed", FloatArgumentType.floatArg(0.1f, 100.0f))
                        .executes(context ->
                                Fly.setFlySpeed(context.getSource(), FloatArgumentType.getFloat(context, "speed")))));
    }
}
