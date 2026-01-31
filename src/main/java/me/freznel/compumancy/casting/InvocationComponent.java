package me.freznel.compumancy.casting;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.execution.InvocationState;
import me.freznel.compumancy.vm.execution.frame.ExecutionFrame;
import me.freznel.compumancy.vm.objects.VMObject;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class InvocationComponent implements Component<EntityStore> {
    public static final BuilderCodec<InvocationComponent> CODEC = BuilderCodec.builder(InvocationComponent.class, InvocationComponent::new)
            .append(new KeyedCodec<>("Map", new MapCodec<>(InvocationState.CODEC, HashMap::new)),
                    (o, v) -> {
                        if (!v.isEmpty()) o.invocations.putAll(v);
                    },
                    o -> o.invocations)
            .add()
            .append(new KeyedCodec<>("Max", Codec.INTEGER), (o, v) -> o.max = v == null ? 0 : v, o -> o.max)
            .add()
            .build();

    private Map<String, InvocationState> invocations;
    private int max;

    public InvocationComponent() {
        invocations = new HashMap<>();
    }
    public InvocationComponent(int max) {
        this.max = max;
    }
    public InvocationComponent(InvocationComponent other) {
        this.max = other.max;
        this.invocations = new HashMap<>(other.invocations.size());
        for (var kv : other.invocations.entrySet())
        {
            this.invocations.put(kv.getKey(), kv.getValue().clone());
        }
    }

    public boolean IsFull() { return invocations.size() >= max; }

    public int getMaxInvocations() { return max; }
    public void SetMaxInvocations(int max) { this.max = max; }

    public boolean Add(Invocation invocation) {
        if (IsFull()) return false;
        var id = invocation.GetId().toString();
        if (invocations.containsKey(id)) return false;
        invocations.put(id, new InvocationState(invocation));
        return true;
    }

    public boolean Remove(UUID invocationId) {
        String id = invocationId.toString();
        if (!invocations.containsKey(id)) return false;
        invocations.remove(id);
        return true;
    }

    public boolean Replace(InvocationState state) {
        String id = state.GetId().toString();
        if (!invocations.containsKey(id)) return false;
        invocations.replace(id, state);
        return true;
    }

    public void RemoveAll() {
        invocations.clear();
    }

    public Iterator<Map.Entry<String, InvocationState>> GetIterator() {
        return invocations.entrySet().iterator();
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Component<EntityStore> clone() {
        return new InvocationComponent(this);
    }

}
