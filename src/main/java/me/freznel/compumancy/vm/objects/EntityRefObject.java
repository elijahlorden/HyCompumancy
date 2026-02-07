package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.reference.PersistentRef;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class EntityRefObject extends VMObject implements IEvaluatable {
    public static final BuilderCodec<EntityRefObject> CODEC = BuilderCodec.builder(EntityRefObject.class, EntityRefObject::new)
            .append(new KeyedCodec<>("World", Codec.UUID_BINARY), EntityRefObject::setWorldId, EntityRefObject::getWorldId)
            .add()
            .append(new KeyedCodec<>("Ref", PersistentRef.CODEC), EntityRefObject::setRef, EntityRefObject::getRef)
            .add()
            .build();

    private UUID worldId;
    private PersistentRef persistentRef;

    private World world;
    private Ref<EntityStore> ref;

    private String displayNameCache;
    private long displayNameCacheInvalidOn;

    public EntityRefObject() { }
    public EntityRefObject(EntityRefObject other) {
        this.worldId = other.worldId;
        this.persistentRef = other.persistentRef;
        this.world = other.world;
        this.ref = other.ref;
        this.displayNameCache = other.displayNameCache;
        this.displayNameCacheInvalidOn = other.displayNameCacheInvalidOn;
    }

    public World getWorld() {
        if (worldId == null) return null;
        if (world == null || !world.isAlive()) {
            world = Universe.get().getWorld(worldId);
            if (world == null || !world.isAlive()) return null;
        }
        return world;
    }

    public Ref<EntityStore> getEntity() {
        if (getWorld() == null || persistentRef == null || !persistentRef.isValid()) return null;
        if (ref == null || !ref.isValid()) {
            final var store = world.getEntityStore().getStore();
            if (store.isInThread()) {
                ref = persistentRef.getEntity(store);
            } else {
                ref = CompletableFuture.supplyAsync(() -> persistentRef.getEntity(store)).join();
            }
            if (ref == null || !ref.isValid()) return null;
        }
        return ref;
    }

    public PersistentRef getRef() { return this.persistentRef; }
    public void setRef(PersistentRef persistentRef) { this.persistentRef = persistentRef; world = null; ref = null; }
    public UUID getWorldId() { return this.worldId; }
    public void setWorldId(UUID worldId) { this.worldId = worldId; world = null; ref = null; }

    public <T> T invokeSafe(Supplier<T> supplier) {
        if (getWorld() == null) return null;
        T result = null;
        if (world.isInThread()) {
            result = supplier.get();
        } else {
            result = CompletableFuture.supplyAsync(supplier, world).join();
        }
        return result;
    }

    public void invokeSafe(Runnable runnable) {
        if (getWorld() == null) return;
        if (world.isInThread()) {
            runnable.run();
        } else {
            CompletableFuture.runAsync(runnable, world).join();
        }
    }

    public String getDisplayName() {
        if (displayNameCache != null && System.currentTimeMillis() < displayNameCacheInvalidOn) return displayNameCache;
        var ref = getEntity();
        if (ref == null) return "";
        var store = ref.getStore();
        var result =  invokeSafe(() -> {
            var c = store.getComponent(ref, DisplayNameComponent.getComponentType());
            if (c == null) return "";
            var msg = c.getDisplayName();
            if (msg == null) return "";
            return msg.getAnsiMessage();
        });
        displayNameCache = result;
        displayNameCacheInvalidOn = System.currentTimeMillis() + 30000;
        return result;
    }

    @Override
    public String getObjectName() {
        return "Entity";
    }

    @Override
    public String toString() {
        String displayName = getDisplayName();
        if (displayName == null) return "Entity [Unnamed]";
        return String.format("Entity [%s]", displayName);
    }

    @Override
    public int getObjectSize() {
        return 1;
    }

    @Override
    public VMObject clone() {
        return new EntityRefObject(this);
    }

    @Override
    public int executionBudgetCost() {
        return 2;
    }

    @Override
    public void evaluate(Invocation invocation) throws VMException {
        invocation.push(new EntityRefObject(this));
    }

    @Override
    public boolean isEvalSynchronous() {
        return false;
    }

    public static EntityRefObject fromRef(Ref<EntityStore> ref) {
        EntityRefObject obj = new EntityRefObject();
        obj.ref = ref;
        var store = ref.getStore();
        obj.world = store.getExternalData().getWorld();
        obj.worldId = obj.world.getWorldConfig().getUuid();
        if (store.isInThread()) {
            obj.persistentRef = new PersistentRef();
            obj.persistentRef.setEntity(ref, store);
        } else {
            obj.persistentRef = CompletableFuture.supplyAsync(() -> {
                var pref = new PersistentRef();
                pref.setEntity(ref, store);
                return pref;
            }, obj.world).join();
        }
        return obj;
    }
}
