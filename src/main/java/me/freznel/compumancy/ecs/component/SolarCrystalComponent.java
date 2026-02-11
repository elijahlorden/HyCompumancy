package me.freznel.compumancy.ecs.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public class SolarCrystalComponent implements Component<ChunkStore> {
    public static BuilderCodec<SolarCrystalComponent> CODEC = BuilderCodec.builder(SolarCrystalComponent.class, SolarCrystalComponent::new)
            .append(new KeyedCodec<>("Rate", Codec.DOUBLE), (o, v) -> o.rate = v, o -> o.rate)
            .add()
            .append(new KeyedCodec<>("Timestamp", Codec.INSTANT), (o, v) -> o.timestamp = v, o -> o.timestamp)
            .add()
            .build();

    public double rate;
    private Instant timestamp;

    public SolarCrystalComponent() { }
    public SolarCrystalComponent(SolarCrystalComponent other) {
        this.rate = other.rate;
        this.timestamp = other.timestamp;
    }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public @Nullable Component<ChunkStore> clone() {
        return new SolarCrystalComponent(this);
    }
}
