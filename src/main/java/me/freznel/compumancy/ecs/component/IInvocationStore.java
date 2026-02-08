package me.freznel.compumancy.ecs.component;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.UUID;

public interface IInvocationStore {
    public void remove(UUID id);
    public void add(UUID id);
    public boolean contains(UUID id);

    public UUID getOwner();
    public void setOwner(UUID owner);
}
