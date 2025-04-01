package org.HedgeTech.slashfly.states;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import org.HedgeTech.slashfly.data.PlayerData;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static org.HedgeTech.slashfly.SlashFly.MOD_ID;

public class StateSaverAndLoader extends PersistentState {
    public HashMap<UUID, PlayerData> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        var playersNbt = new NbtCompound();

        players.forEach((uuid, playerData) -> {
            var playerNbt = new NbtCompound();

            playerNbt.putBoolean("inFlight", playerData.inFlight);

            playersNbt.put(uuid.toString(), playerNbt);
        });

        nbt.put("players", playersNbt);

        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        var state = new StateSaverAndLoader();
        var playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            var playerData = new PlayerData();
            var uuid = UUID.fromString(key);

            playerData.inFlight = playersNbt.getCompound(key).getBoolean("inFlight");
            state.players.put(uuid, playerData);
        });
        return state;
    }

    public static StateSaverAndLoader createNew() {
        var state = new StateSaverAndLoader();
        state.players = new HashMap<>();
        return state;
    }

    private static final Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::createNew, // If there's no 'StateSaverAndLoader' yet create one and refresh variables
            StateSaverAndLoader::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        var serverWorld = server.getWorld(World.OVERWORLD);
        assert serverWorld != null;

        // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
        var state = serverWorld.getPersistentStateManager().getOrCreate(type, MOD_ID);

        // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
        // there were no actual change to any of the mods state (INCREDIBLY RARE).
        state.markDirty();

        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player) {
        var serverState = getServerState(Objects.requireNonNull(player.getWorld().getServer()));

        // Either get the player by the uuid, or we don't have data for him yet, make a new player state
        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());
    }
}
