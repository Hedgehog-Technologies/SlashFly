package org.hedgetech.slashfly.permissions;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import org.hedgetech.slashfly.platform.Services;

public class FabricPermissionHelper implements IPermissionHelper {
    @Override
    public boolean hasPermission(CommandSourceStack source, String permission, PermissionLevel vanillaPermissionLevel) {
        if (Services.PLATFORM.isModLoaded("fabric-permissions-api-v0")) {
            return Permissions.check(source, permission, vanillaPermissionLevel);
        }

        return source.permissions().hasPermission(new Permission.HasCommandLevel(vanillaPermissionLevel));
    }

    @Override
    public boolean hasPermission(ServerPlayer player, String permission, PermissionLevel vanillaPermissionLevel) {
        if (Services.PLATFORM.isModLoaded("fabric-permissions-api-v0")) {
            return Permissions.check(player, permission, vanillaPermissionLevel);
        }

        return player.permissions().hasPermission(new Permission.HasCommandLevel(vanillaPermissionLevel));
    }
}
