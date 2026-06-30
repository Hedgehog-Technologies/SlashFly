package org.hedgetech.slashfly.permissions;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;

public interface IPermissionHelper {
    /**
      * Check if a source has a given permission.
      * Falls back to vanilla op level if LuckPerms is not present.
      */
    boolean hasPermission(CommandSourceStack source, String permission, PermissionLevel vanillaPermissionLevel);

    /**
      * Check if a player has a given permission.
      */
    boolean hasPermission(ServerPlayer player, String permission, PermissionLevel vanillaPermissionLevel);
}
