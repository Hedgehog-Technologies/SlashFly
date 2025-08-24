package org.hedgetech.slashfly.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class  PlayerData {
    public static final Codec<PlayerData> CODEC;

    private boolean mayFly;
    private boolean isFlying;
    private float flightSpeed;

    public PlayerData() {
        this.mayFly = false;
        this.isFlying = false;
        flightSpeed = 0.05f;
    }

    public PlayerData(boolean mayFly, boolean isFlying, float speed) {
        this.mayFly = mayFly;
        this.isFlying = isFlying;
        this.flightSpeed = speed;
    }

    public boolean getIsFlying() { return this.isFlying; }

    public void setIsFlying(boolean isFlying) {
        this.isFlying = isFlying;
    }

    public boolean getMayFly() { return this.mayFly; }

    public void setMayFly(boolean mayFly) {
        this.mayFly = mayFly;
    }

    public float getFlightSpeed() { return this.flightSpeed; }

    public void setFlightSpeed(float speed) { this.flightSpeed = speed; }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("mayFly").forGetter(PlayerData::getMayFly),
                Codec.BOOL.fieldOf("isFlying").forGetter(PlayerData::getIsFlying),
                Codec.FLOAT.fieldOf("flightSpeed").forGetter(PlayerData::getFlightSpeed)
        ).apply(instance, PlayerData::new));
    }
}
