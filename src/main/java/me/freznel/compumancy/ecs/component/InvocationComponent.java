package me.freznel.compumancy.ecs.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.set.SetCodec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import me.freznel.compumancy.Compumancy;
import me.freznel.compumancy.vm.store.InvocationStore;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class InvocationComponent implements Component<EntityStore> {
    public static final BuilderCodec<InvocationComponent> CODEC = BuilderCodec.builder(InvocationComponent.class, InvocationComponent::new)
            .append(new KeyedCodec<>("Owner", Codec.UUID_BINARY), (o, v) -> o.owner = v, o -> o.owner)
            .add()
            .append(new KeyedCodec<>("Set", new SetCodec<>(Codec.UUID_BINARY, ObjectLinkedOpenHashSet::new, false)), (o, v) -> o.invocations= v, o -> o.invocations)
            .add()
            .build();

    private UUID owner;
    private Set<UUID> invocations;

    private InvocationComponent() { invocations = new ObjectLinkedOpenHashSet<>(); }
    public InvocationComponent(UUID owner) { this.owner = owner; super(); }
    public InvocationComponent(InvocationComponent other) {
        this.owner = other.owner;
        invocations = new ObjectLinkedOpenHashSet<>();
        invocations.addAll(other.invocations);
    }

    public void remove(UUID id) { invocations.remove(id); }
    public void add(UUID id) { invocations.add(id); }
    public boolean contains(UUID id) { return invocations.contains(id); }

    public UUID getOwner() { return owner; }
    public void setOwner(UUID owner) { this.owner = owner; }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Component<EntityStore> clone() {
        return new InvocationComponent(this);
    }




    public static class InvocationComponentRefSystem extends RefSystem<EntityStore> {
        private static ComponentType<EntityStore, InvocationComponent> COMPONENT_TYPE;

        public InvocationComponentRefSystem() {
            COMPONENT_TYPE = Compumancy.get().getInvocationComponentType();
        }

        @Override
        public void onEntityAdded(@NonNull Ref<EntityStore> ref, @NonNull AddReason addReason, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
            var comp = store.getComponent(ref, COMPONENT_TYPE);
            if (comp == null || comp.invocations.isEmpty()) return;
            var set = comp.invocations;
            var world = store.getExternalData().getWorld();
            InvocationStore.get(comp.owner).thenAccept(invocationStore -> {
                var failed = new ObjectLinkedOpenHashSet<UUID>();
                for (UUID id : set) {
                    if (!invocationStore.resume(id, ref, world)) failed.add(id);
                }
                if (!failed.isEmpty()) {
                    world.execute(() -> {
                        comp.invocations.removeAll(failed);
                    });
                }
            });
        }

        @Override
        public void onEntityRemove(@NonNull Ref<EntityStore> ref, @NonNull RemoveReason removeReason, @NonNull Store<EntityStore> store, @NonNull CommandBuffer<EntityStore> commandBuffer) {
            var comp = store.getComponent(ref, COMPONENT_TYPE);
            if (comp == null || comp.invocations.isEmpty()) return;
            var set = comp.invocations;
            if (removeReason == RemoveReason.REMOVE) {
                InvocationStore.get(comp.owner).thenAccept(invocationStore -> {
                    for (UUID id : set) invocationStore.kill(id);
                });
            } else if (removeReason == RemoveReason.UNLOAD) {
                InvocationStore.get(comp.owner).thenAccept(invocationStore -> {
                    for (UUID id : set) invocationStore.suspend(id);
                });
            }
        }

        @Override
        public @Nullable Query<EntityStore> getQuery() {
            return COMPONENT_TYPE;
        }
    }


}
