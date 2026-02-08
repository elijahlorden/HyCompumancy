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
import me.freznel.compumancy.vm.execution.Caster;
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
                            o.loadedInvocations.put(state.getId(), state);
                        }
                    },
                    o -> o.saveStates.toArray(new InvocationState[0]))
            .add()
            .append(new KeyedCodec<>("Owner", Codec.UUID_BINARY), (o, v) -> o.owner = v, o -> o.owner)
            .add()
            .build();

    private static final ConcurrentHashMap<UUID, InvocationStore> stores = new ConcurrentHashMap<>();

    public static CompletableFuture<InvocationStore> get(UUID owner) {
        if (stores.containsKey(owner)) return CompletableFuture.completedFuture(stores.get(owner));
        return CompletableFuture.supplyAsync(() -> {
            return stores.computeIfAbsent(owner, _ -> {
                var path = Compumancy.get().getDataDirectory()
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
        }, Compumancy.get().getDaemonExecutor());
    }

    public static boolean isLoaded(UUID owner) { return stores.containsKey(owner); }

    public static void resetExecuteCount() {
        for (var store : stores.values()) {
            store.exeCount.set(0);
        }
    }

    public static synchronized void saveAll(boolean suspend) {
        long start = System.nanoTime();
        var futures = new ArrayList<CompletableFuture<Integer>>();

        for (var store : stores.values()) {
            futures.add(store.save(suspend));
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
        exeCapacity = Compumancy.get().getConfig().DelayThreshold;
        exeDelayStep = Compumancy.get().getConfig().DelayPerStep;
        exeCount = new AtomicInteger(0);
    }

    private InvocationStore(UUID owner) { this.owner = owner; this(); }

    public UUID getOwner() { return owner; }

    public int count() { return invocations.size(); }

    public int resumeDelay() {
        int newCount = exeCount.accumulateAndGet(1, Integer::sum);
        return newCount > exeCapacity ? (newCount - exeCapacity) * exeDelayStep : 0;
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public CompletableFuture<Integer> save(boolean suspend) {
        return CompletableFuture.supplyAsync(() -> {
            saveStates.clear();
            saveStates.addAll(loadedInvocations.values());

            for (var invocation : invocations.values()) {
                if (suspend) invocation.suspend();
                synchronized (invocation) {
                    saveStates.add(new InvocationState(invocation));
                }
            }

            var path = Compumancy.get().getDataDirectory()
                    .resolve("store")
                    .resolve("invocation")
                    .resolve(owner.toString() + ".bson");
            try {
                BsonUtil.writeSync(path, CODEC, this, Logger);
                return saveStates.size();
            } catch (Exception e) {
                return 0;
            }
        }, Compumancy.get().getUserExecutor()); //Run on the high-priority blocking executor
    }

    public boolean resume(Invocation invocation) {
        var id = invocation.getId();
        if (invocations.containsKey(id)) return false;
        invocations.put(id, invocation);
        var caster = invocation.getCaster();
        if (!caster.isValid()) return false;
        invocation.getWorld().execute(() -> {
            if (!caster.isValid()) {
                invocations.remove(id);
            }
            var comp = caster.getOrCreateInvocationStoreComponent();
            comp.setOwner(owner);
            comp.add(id);
            if (!invocation.schedule()) {
                comp.remove(id);
                invocations.remove(id);
            }
        });
        return true;
    }

    public boolean suspend(UUID id) {
        var invocation = invocations.get(id);
        if (invocation == null) return false;
        invocation.suspend();
        return true;
    }

    public boolean kill(UUID id) {
        var invocation = invocations.remove(id);
        if (invocation == null) return false;
        invocation.suspend();
        var caster = invocation.getCaster();
        if (!caster.isValid()) return true;
        invocation.getWorld().execute(() -> {
            if (!caster.isValid()) return;
            var comp = caster.getInvocationStoreComponent();
            if (comp != null) comp.remove(id);
        });
        return true;
    }

    public boolean resume(UUID id, Caster<?> caster, World world) {
        var invocation = invocations.get(id);
        if (invocation == null) {
            var state = loadedInvocations.get(id);
            if (state == null) return false;
            var restored = new Invocation(world, caster, state, this);
            if (!restored.schedule()) {
                return false;
            }
            invocations.put(id, restored);
            return true;
        } else if (invocation.schedule(caster, world)) {
            return true;
        } else {
            invocations.remove(id);
            return false;
        }
    }

    public void killAll() {
        invocations.values().stream()
                .collect(Collectors.groupingBy(Invocation::getWorld))
                .forEach((world, invocations) -> {
                    var store = world.getEntityStore().getStore();
                    world.execute(() -> {
                        for (var invocation : invocations) {
                            this.invocations.remove(invocation.getId());
                            invocation.suspend();
                            var caster = invocation.getCaster();
                            var comp = caster.getInvocationStoreComponent();
                            if (comp != null) comp.remove(invocation.getId());
                        }
                    });
                });
    }






}
