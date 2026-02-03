package me.freznel.compumancy.vm.store;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.util.BsonUtil;
import me.freznel.compumancy.Compumancy;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.execution.InvocationState;

import java.util.ArrayList;
import java.util.stream.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InvocationStore {
    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();

    public static final BuilderCodec<InvocationStore> CODEC = BuilderCodec.builder(InvocationStore.class, InvocationStore::new)
            .append(new KeyedCodec<>("Map", new MapCodec<>(InvocationState.CODEC, HashMap::new)),
                    (o, v) -> {
                        if (!v.isEmpty()) o.savedInvocations.putAll(v);
                    },
                    o -> o.savedInvocations)
            .add()
            .append(new KeyedCodec<>("Owner", Codec.UUID_BINARY), (o, v) -> o.owner = v, o -> o.owner)
            .add()
            .build();

    private static final ConcurrentHashMap<UUID, InvocationStore> stores = new ConcurrentHashMap<>();

    public static CompletableFuture<InvocationStore> Get(UUID owner) {
        return CompletableFuture.supplyAsync(() -> {
            return stores.computeIfAbsent(owner, _ -> {
                var path = Compumancy.Get().getDataDirectory()
                        .resolve("store")
                        .resolve("invocation")
                        .resolve(owner.toString() + ".bson");
                var doc = BsonUtil.readDocumentNow(path);
                if (doc == null) return new InvocationStore(owner);
                var dec = CODEC.decode(doc, new ExtraInfo());
                if (dec != null) {
                    dec.Load();
                    return dec;
                } else {
                    return new InvocationStore(owner);
                }
            });
        }, Compumancy.Get().GetExecutor());
    }

    public static boolean IsLoaded(UUID owner) { return stores.containsKey(owner); }

    public static void ResetExecuteCount() {
        for (var store : stores.values()) {
            store.exeCount.set(0);
        }
    }

    private UUID owner;
    private final ConcurrentHashMap<String, InvocationState> savedInvocations;

    private final int exeCapacity;
    private final int exeDelayStep;
    private final AtomicInteger exeCount;

    private final ConcurrentHashMap<UUID, Invocation> invocations;

    private InvocationStore() {
        savedInvocations = new ConcurrentHashMap<>();
        invocations = new ConcurrentHashMap<>();
        exeCapacity = Compumancy.Get().GetConfig().DelayThreshold;
        exeDelayStep = Compumancy.Get().GetConfig().DelayPerStep;
        exeCount = new AtomicInteger(0);
    }

    private InvocationStore(UUID owner) { this.owner = owner; this(); }

    public UUID GetOwner() { return owner; }

    public int Count() { return invocations.size(); }
    public boolean IsFull() { return invocations.size() >= exeCapacity; }

    public int resumeDelay() {
        int newCount = exeCount.accumulateAndGet(1, Integer::sum);
        return newCount > exeCapacity ? (newCount - exeCapacity) * exeDelayStep : 0;
    }

    public void Load() {

    }

    public void Persist(boolean reschedule) {
        savedInvocations.clear();
        ArrayList<CompletableFuture<InvocationState>> persistFutures = new ArrayList<>(invocations.size());

        for (var invocation : invocations.values()) {
            if (invocation.IsFinished()) {
                Kill(invocation.GetId());
            } else if (invocation.IsSuspended()) {
                savedInvocations.put(invocation.GetId().toString(), new InvocationState(invocation));
            } else { //This is horrifying.  Find a better way to do this.
                var future = new CompletableFuture<InvocationState>();
                persistFutures.add(future);
                future.thenAccept(state -> {
                    savedInvocations.put(invocation.GetId().toString(), state);
                    if (reschedule) invocation.Schedule();
                });
                invocation.SuspendFuture.set(future);
                invocation.Suspend();
            }
        }

        CompletableFuture.allOf(persistFutures.toArray(new CompletableFuture[0])).thenRun(() -> {
            //TODO: Actually save everything to the file
        });
    }

    public boolean Add(Invocation invocation) {
        var id = invocation.GetId();
        if (IsFull() || invocations.containsKey(id)) return false;
        invocations.put(id, invocation);
        var ref = invocation.GetCaster();
        invocation.GetWorld().execute(() -> {
            if (!ref.isValid()) return;
            var store = ref.getStore();
            var comp = store.getComponent(ref, Compumancy.Get().GetInvocationComponentType());
            if (comp == null) comp = store.addComponent(ref, Compumancy.Get().GetInvocationComponentType());
            comp.Add(id);
        });
        return true;
    }

    public boolean Suspend(UUID id) {
        var invocation = invocations.get(id);
        if (invocation == null) return false;
        invocation.Suspend();
        return true;
    }

    public boolean Kill(UUID id) {
        var invocation = invocations.remove(id);
        if (invocation == null) return false;
        invocation.Suspend();
        var ref = invocation.GetCaster();
        invocation.GetWorld().execute(() -> {
            if (!ref.isValid()) return;
            var store = ref.getStore();
            var comp = store.getComponent(ref, Compumancy.Get().GetInvocationComponentType());
            if (comp != null) comp.Remove(id);
        });
        return true;
    }

    public boolean Resume(UUID id) {
        var invocation = invocations.get(id);
        if (invocation == null) return false;
        if (!invocation.Schedule()) {
            invocations.remove(id);
            return false;
        }
        return true;
    }

    public void KillAll() {
        invocations.values().stream()
                .collect(Collectors.groupingBy(Invocation::GetWorld))
                .forEach((world, invocations) -> {
                    var store = world.getEntityStore().getStore();
                    world.execute(() -> {
                        for (var invocation :invocations) {
                            this.invocations.remove(invocation.GetId());
                            invocation.Suspend();
                            var ref = invocation.GetCaster();
                            var comp = store.getComponent(ref, Compumancy.Get().GetInvocationComponentType());
                            if (comp != null) comp.Remove(invocation.GetId());
                        }
                    });
                });
    }






}
