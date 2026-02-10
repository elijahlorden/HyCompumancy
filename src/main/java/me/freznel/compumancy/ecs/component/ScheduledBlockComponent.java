package me.freznel.compumancy.ecs.component;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.jspecify.annotations.Nullable;

public class ScheduledBlockComponent implements Component<ChunkStore> {
    public static final BuilderCodec<ScheduledBlockComponent> CODEC = BuilderCodec.builder(ScheduledBlockComponent.class, ScheduledBlockComponent::new).build();

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public @Nullable Component<ChunkStore> clone() {
        return this;
    }
}
