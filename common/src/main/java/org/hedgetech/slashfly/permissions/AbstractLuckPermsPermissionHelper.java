package org.hedgetech.slashfly.permissions;

import net.luckperms.api.LuckPermsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import org.hedgetech.slashfly.platform.Services;

public abstract class AbstractLuckPermsPermissionHelper implements IPermissionHelper {

    @Override
    public boolean hasPermission(CommandSourceStack source, String permission, PermissionLevel vanillaPermissionLevel) {
        var player = source.getPlayer();

        if (player != null && Services.PLATFORM.isModLoaded("luckperms")) {
            return checkLuckPerms(player, permission, vanillaPermissionLevel);
        }

        return source.permissions().hasPermission(new Permission.HasCommandLevel(vanillaPermissionLevel));
    }

    @Override
    public boolean hasPermission(ServerPlayer player, String permission, PermissionLevel vanillaPermissionLevel) {
        if (Services.PLATFORM.isModLoaded("luckperms")) {
            return checkLuckPerms(player, permission, vanillaPermissionLevel);
        }

        return player.permissions().hasPermission(new Permission.HasCommandLevel(vanillaPermissionLevel));
    }

    private boolean checkLuckPerms(ServerPlayer player, String permission, PermissionLevel vanillaPermissionLevel) {
        try {
            var lp = LuckPermsProvider.get();
            var lpUser = lp.getUserManager().getUser(player.getUUID());

            if (lpUser == null) {
                return player.permissions().hasPermission(new Permission.HasCommandLevel(vanillaPermissionLevel));
            }

            return lpUser.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
        } catch (Exception e) {
            return player.permissions().hasPermission(new Permission.HasCommandLevel(vanillaPermissionLevel));
        }
    }
}
