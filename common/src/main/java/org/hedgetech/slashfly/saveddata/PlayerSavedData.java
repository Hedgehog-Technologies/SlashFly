package org.hedgetech.slashfly.saveddata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.hedgetech.slashfly.data.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.hedgetech.slashfly.Constants.MOD_ID;

public class PlayerSavedData extends SavedData {
    public static final Codec<Map<String, PlayerData>> PLAYER_DATA_CODEC = Codec.unboundedMap(Codec.STRING, PlayerData.CODEC);
    public static final Codec<PlayerSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PLAYER_DATA_CODEC.fieldOf("player_data").forGetter(PlayerSavedData::getPlayerDatum)
    ).apply(instance, PlayerSavedData::new));

    private final HashMap<String, PlayerData> playerDatum;

    public PlayerSavedData() { playerDatum = new HashMap<>(); }

    public PlayerSavedData(Map<String, PlayerData> playerDatum) {
        this.playerDatum = new HashMap<>();
        this.playerDatum.putAll(playerDatum);
    }

    public Map<String, PlayerData> getPlayerDatum() { return this.playerDatum; }

    public PlayerData getPlayerData(String uuidString) {
        return this.playerDatum.computeIfAbsent(uuidString, playerUUID -> new PlayerData());
    }

    public boolean togglePlayerFlight(Player player) {
        var enabled = false;

        if (player != null) {
            var playerAbilities = player.getAbilities();
            var mayFly = playerAbilities.mayfly;
            enabled = togglePlayerFlight(player, !mayFly);
        }

        return enabled;
    }

    public boolean togglePlayerFlight(Player player, boolean enableFlight) {
        var enabled = false;

        if (player != null) {
            var playerData = getPlayerData(player.getStringUUID());
            var playerAbilities = player.getAbilities();
            var onGround = player.onGround();
            var flying = enableFlight && !onGround;

            playerAbilities.mayfly = enableFlight;
            playerAbilities.flying = flying;
            player.onUpdateAbilities();

            playerData.setMayFly(enableFlight);
            playerData.setIsFlying(flying);
            setDirty();
            enabled = enableFlight;
        }

        return enabled;
    }

    public void setPlayerFlightSpeed(Player player, float speed) {
        if (player != null) {
            var playerData = getPlayerData(player.getStringUUID());
            var playerAbilities = player.getAbilities();
            var normalizedSpeed = speed / 10.0f;

            playerAbilities.setFlyingSpeed(normalizedSpeed);
            player.onUpdateAbilities();

            playerData.setFlightSpeed(normalizedSpeed);
            setDirty();
        }
    }

    private static final SavedDataType<PlayerSavedData> TYPE = new SavedDataType<>(MOD_ID, PlayerSavedData::new, CODEC, null);

    public static PlayerSavedData ofServer(MinecraftServer server) {
        try {
            return Objects.requireNonNull(server.overworld())
                    .getDataStorage()
                    .computeIfAbsent(TYPE);
        } catch (Exception e) {
            var dataStore = Objects.requireNonNull(server.overworld()).getDataStorage();
            dataStore.set(TYPE, new PlayerSavedData());
            return dataStore.computeIfAbsent(TYPE);
        }
    }
}
