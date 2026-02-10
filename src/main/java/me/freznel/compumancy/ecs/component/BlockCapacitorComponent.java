package me.freznel.compumancy.ecs.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import me.freznel.compumancy.energy.ICapacitor;
import org.jspecify.annotations.Nullable;

public class BlockCapacitorComponent implements Component<ChunkStore>, ICapacitor {
    public static BuilderCodec<BlockCapacitorComponent> CODEC = BuilderCodec.builder(BlockCapacitorComponent.class, BlockCapacitorComponent::new)
            .append(new KeyedCodec<>("Charge", Codec.DOUBLE), (o, v) -> o.charge = v, o -> o.charge)
            .add()
            .append(new KeyedCodec<>("Capacity", Codec.DOUBLE), (o, v) -> o.capacity = v, o -> o.capacity)
            .add()
            .build();

    private double capacity;
    private double charge;

    public BlockCapacitorComponent() { }
    public BlockCapacitorComponent(BlockCapacitorComponent other) {
        this.charge = other.charge;
        this.capacity = other.capacity;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public @Nullable Component<ChunkStore> clone() {
        return new BlockCapacitorComponent(this);
    }

    @Override
    public double getCapacity() {
        return capacity;
    }

    @Override
    public double getCharge() {
        return charge;
    }

    @Override
    public double add(double amount) {
        double added = Math.min(capacity - charge, amount);
        charge += added;
        return added;
    }

    @Override
    public double remove(double amount) {
        double removed = Math.min(capacity - charge, amount);
        charge += removed;
        return removed;
    }
}
