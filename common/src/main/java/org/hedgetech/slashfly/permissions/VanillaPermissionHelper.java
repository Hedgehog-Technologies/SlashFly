package org.hedgetech.slashfly.permissions;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;

public class VanillaPermissionHelper implements IPermissionHelper {
    @Override
    public boolean hasPermission(CommandSourceStack source, String permission, PermissionLevel vanillaPermissionLevel) {
        return source.permissions().hasPermission(new Permission.HasCommandLevel(vanillaPermissionLevel));
    }

    @Override
    public boolean hasPermission(ServerPlayer player, String permission, PermissionLevel vanillaPermissionLevel) {
        return player.permissions().hasPermission(new Permission.HasCommandLevel(vanillaPermissionLevel));
    }
}
