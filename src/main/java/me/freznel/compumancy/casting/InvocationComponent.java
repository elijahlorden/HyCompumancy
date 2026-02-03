package me.freznel.compumancy.casting;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.set.SetCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

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

    public void Remove(UUID id) { invocations.remove(id); }
    public void Add(UUID id) { invocations.add(id); }
    public boolean Contains(UUID id) { return invocations.contains(id); }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Component<EntityStore> clone() {
        return new InvocationComponent(this);
    }

}
