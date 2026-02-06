package me.freznel.compumancy.vm.store;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.BsonUtil;
import me.freznel.compumancy.Compumancy;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.execution.InvocationState;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.stream.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InvocationStore {
    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();

    public static final BuilderCodec<InvocationStore> CODEC = BuilderCodec.builder(InvocationStore.class, InvocationStore::new)
            .append(new KeyedCodec<>("Invocations", new ArrayCodec<>(InvocationState.CODEC, InvocationState[]::new)),
                    (o, states) -> {
                        for (InvocationState state : states) {
                            o.loadedInvocations.put(state.GetId(), state);
                        }
                    },
                    o -> o.saveStates.toArray(new InvocationState[0]))
            .add()
            .append(new KeyedCodec<>("Owner", Codec.UUID_BINARY), (o, v) -> o.owner = v, o -> o.owner)
            .add()
            .build();

    private static final ConcurrentHashMap<UUID, InvocationStore> stores = new ConcurrentHashMap<>();

    public static CompletableFuture<InvocationStore> Get(UUID owner) {
        if (stores.containsKey(owner)) return CompletableFuture.completedFuture(stores.get(owner));
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
                    return dec;
                } else {
                    return new InvocationStore(owner);
                }
            });
        }, Compumancy.Get().GetDaemonExecutor());
    }

    public static boolean IsLoaded(UUID owner) { return stores.containsKey(owner); }

    public static void ResetExecuteCount() {
        for (var store : stores.values()) {
            store.exeCount.set(0);
        }
    }

    public static synchronized void SaveAll(boolean suspend) {
        long start = System.nanoTime();
        var futures = new ArrayList<CompletableFuture<Integer>>();

        for (var store : stores.values()) {
            futures.add(store.Save(suspend));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        int count = futures.stream().mapToInt(CompletableFuture::join).sum();
        long delta = (System.nanoTime() - start) / 1_000_000;
        Logger.at(Level.INFO).log(String.format("Saved %d invocations in %dms", count, delta));
    }

    private UUID owner;
    private final ArrayList<InvocationState> saveStates;

    private final int exeCapacity;
    private final int exeDelayStep;
    private final AtomicInteger exeCount;

    private final ConcurrentHashMap<UUID, InvocationState> loadedInvocations;
    private final ConcurrentHashMap<UUID, Invocation> invocations;

    private InvocationStore() {
        saveStates = new ArrayList<>();
        invocations = new ConcurrentHashMap<>();
        loadedInvocations = new ConcurrentHashMap<>();
        exeCapacity = Compumancy.Get().GetConfig().DelayThreshold;
        exeDelayStep = Compumancy.Get().GetConfig().DelayPerStep;
        exeCount = new AtomicInteger(0);
    }

    private InvocationStore(UUID owner) { this.owner = owner; this(); }

    public UUID GetOwner() { return owner; }

    public int Count() { return invocations.size(); }

    public int resumeDelay() {
        int newCount = exeCount.accumulateAndGet(1, Integer::sum);
        return newCount > exeCapacity ? (newCount - exeCapacity) * exeDelayStep : 0;
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public CompletableFuture<Integer> Save(boolean suspend) {
        return CompletableFuture.supplyAsync(() -> {
            saveStates.clear();
            ArrayList<CompletableFuture<InvocationState>> persistFutures = new ArrayList<>(invocations.size());

            for (var invocation : invocations.values()) {
                if (suspend) invocation.Suspend();
                synchronized (invocation) {
                    saveStates.add(new InvocationState(invocation));
                }
            }

            var path = Compumancy.Get().getDataDirectory()
                    .resolve("store")
                    .resolve("invocation")
                    .resolve(owner.toString() + ".bson");
            try {
                BsonUtil.writeSync(path, CODEC, this, Logger);
                return saveStates.size();
            } catch (Exception e) {
                return 0;
            }
        }, Compumancy.Get().GetExecutor()); //Run on the high-priority blocking executor
    }

    public boolean Resume(Invocation invocation) {
        var id = invocation.GetId();
        if (invocations.containsKey(id)) return false;
        invocations.put(id, invocation);
        var ref = invocation.GetCaster();
        if (!ref.isValid()) return false;
        invocation.GetWorld().execute(() -> {
            if (!ref.isValid()) {
                invocations.remove(id);
            }
            var store = ref.getStore();
            var comp = store.getComponent(ref, Compumancy.Get().GetInvocationComponentType());
            if (comp == null) comp = store.addComponent(ref, Compumancy.Get().GetInvocationComponentType());
            comp.SetOwner(owner);
            comp.Add(id);
            if (!invocation.Schedule()) {
                comp.Remove(id);
                invocations.remove(id);
            }
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
        if (!ref.isValid()) return true;
        invocation.GetWorld().execute(() -> {
            if (!ref.isValid()) return;
            var store = ref.getStore();
            var comp = store.getComponent(ref, Compumancy.Get().GetInvocationComponentType());
            if (comp != null) comp.Remove(id);
        });
        return true;
    }

    public boolean Resume(UUID id, Ref<EntityStore> caster, World world) {
        var invocation = invocations.get(id);
        if (invocation == null) {
            var state = loadedInvocations.get(id);
            if (state == null) return false;
            var restored = new Invocation(world, caster, state, this);
            if (!restored.Schedule()) {
                return false;
            }
            invocations.put(id, restored);
            return true;
        } else if (invocation.Schedule(caster, world)) {
            return true;
        } else {
            invocations.remove(id);
            return false;
        }
    }

    public void KillAll() {
        invocations.values().stream()
                .collect(Collectors.groupingBy(Invocation::GetWorld))
                .forEach((world, invocations) -> {
                    var store = world.getEntityStore().getStore();
                    world.execute(() -> {
                        for (var invocation : invocations) {
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
