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
import me.freznel.compumancy.Compumancy;
import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;

import javax.xml.stream.events.EndElement;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EntityRefObject extends VMObject implements IEvaluatable {
    public static final BuilderCodec<EntityRefObject> CODEC = BuilderCodec.builder(EntityRefObject.class, EntityRefObject::new)
            .append(new KeyedCodec<>("World", Codec.UUID_BINARY), EntityRefObject::SetWorldId, EntityRefObject::GetWorldId)
            .add()
            .append(new KeyedCodec<>("Ref", PersistentRef.CODEC), EntityRefObject::SetRef, EntityRefObject::GetRef)
            .add()
            .build();

    static {
        VMObject.CODEC.register("EntityRef", EntityRefObject.class, CODEC);
    }

    private UUID worldId;
    private PersistentRef persistentRef;
    private World world;
    private Ref<EntityStore> ref;

    public EntityRefObject() { }
    public EntityRefObject(EntityRefObject other) {
        this.worldId = other.worldId;
        this.persistentRef = other.persistentRef;
        this.world = other.world;
        this.ref = other.ref;
    }

    public World GetWorld() {
        if (worldId == null) return null;
        if (world == null || !world.isAlive()) {
            world = Universe.get().getWorld(worldId);
            if (world == null || !world.isAlive()) return null;
        }
        return world;
    }

    public Ref<EntityStore> GetEntity() {
        if (GetWorld() == null || persistentRef == null || !persistentRef.isValid()) return null;
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

    public PersistentRef GetRef() { return this.persistentRef; }
    public void SetRef(PersistentRef persistentRef) { this.persistentRef = persistentRef; world = null; ref = null; }
    public UUID GetWorldId() { return this.worldId; }
    public void SetWorldId(UUID worldId) { this.worldId = worldId; world = null; ref = null; }

    @Override
    public String GetName() {
        return "Entity";
    }

    @Override
    public String toString() {
        if (GetWorld() == null) return "Unloaded Entity";
        var ref = GetEntity();
        if (ref == null) return "Unloaded Entity";
        var store = ref.getStore();
        String name = null;
        if (store.isInThread()) {
            var c = store.getComponent(ref, DisplayNameComponent.getComponentType());
            if (c != null) {
                var msg = c.getDisplayName();
                if (msg != null) name = msg.getAnsiMessage();
            }
        } else {
            name = CompletableFuture.supplyAsync(() -> {
                var c = store.getComponent(ref, DisplayNameComponent.getComponentType());
                if (c != null) {
                    var msg = c.getDisplayName();
                    if (msg != null) return msg.getAnsiMessage();
                }
                return null;
            }, world).join();
        }
        return (name != null && !name.trim().isEmpty()) ? name : "Unnamed Entity";
    }

    @Override
    public int GetSize() {
        return 1;
    }

    @Override
    public VMObject clone() {
        return new EntityRefObject(this);
    }

    @Override
    public int ExecutionBudgetCost() {
        return 2;
    }

    @Override
    public void Evaluate(Invocation invocation) throws VMException {
        invocation.Push(new EntityRefObject(this));
    }

    public static EntityRefObject FromRef(Ref<EntityStore> ref) {
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
