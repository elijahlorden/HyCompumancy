package me.freznel.compumancy.vm.execution;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.Compumancy;
import me.freznel.compumancy.ecs.component.IDefinitionStore;
import me.freznel.compumancy.ecs.component.IInvocationStore;

public class Caster<T> {

    public enum CasterType { Entity, Block }

    public CasterType type;
    public Ref<T> ref;

    private Caster(Ref<T> ref, CasterType type) { this.ref = ref; this.type = type; }

    public static Caster<EntityStore> FromEntity(Ref<EntityStore> ref) {
        return new Caster<EntityStore>(ref, CasterType.Entity);
    }

    public static Caster<ChunkStore> FromBlock(Ref<ChunkStore> ref) {
        return new Caster<ChunkStore>(ref, CasterType.Entity);
    }

    public CasterType getType() { return type; }
    public boolean isValid() { return ref.isValid(); }
    public Ref<T> GetRef() { return ref; }

    public IInvocationStore getInvocationStoreComponent() {
        if (!ref.isValid()) return null;
        if (type == CasterType.Entity) {
            @SuppressWarnings("unchecked")
            var ref = (Ref<EntityStore>) this.ref;
            return ref.getStore().getComponent(ref, Compumancy.get().getInvocationComponentType());
        } else if (type == CasterType.Block) {
            return null; //TODO: Implement
        } else {
            return null;
        }
    }

    public IInvocationStore getOrCreateInvocationStoreComponent() {
        if (!ref.isValid()) return null;
        if (type == CasterType.Entity) {
            @SuppressWarnings("unchecked")
            var ref = (Ref<EntityStore>) this.ref;
            var store = ref.getStore();
            var comp = store.getComponent(ref, Compumancy.get().getInvocationComponentType());
            if (comp != null) return comp;
            comp = store.addComponent(ref, Compumancy.get().getInvocationComponentType());
            return comp;
        } else if (type == CasterType.Block) {
            return null; //TODO: Implement
        } else {
            return null;
        }
    }

    public IDefinitionStore getDefinitionStoreComponent() {
        if (!ref.isValid()) return null;
        if (type == CasterType.Entity) {
            @SuppressWarnings("unchecked")
            var ref = (Ref<EntityStore>) this.ref;
            return ref.getStore().getComponent(ref, Compumancy.get().getDefinitionStoreComponentType());
        } else if (type == CasterType.Block) {
            return null; //TODO: Implement
        } else {
            return null;
        }
    }

    public Vector3i getBlockPosition() {
        if (type == CasterType.Entity) {
            @SuppressWarnings("unchecked")
            Ref<EntityStore> ref = (Ref<EntityStore>) this.ref;
            var store = ref.getStore();
            var transform = store.getComponent(ref, TransformComponent.getComponentType());
            if (transform == null) return null;
            var pos = transform.getPosition();
            return new Vector3i((int)Math.floor(pos.x), (int)Math.floor(pos.y), (int)Math.floor(pos.z));
        } else if (type == CasterType.Block) {
            @SuppressWarnings("unchecked")
            Ref<ChunkStore> ref = (Ref<ChunkStore>) this.ref;
            var store = ref.getStore();
            var info = store.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
            if (info == null) return null;
            var chunk = store.getComponent(info.getChunkRef(), WorldChunk.getComponentType());
            if (chunk == null) return null;
            var index = info.getIndex();
            return new Vector3i(
                    chunk.getX() << 5 | ChunkUtil.xFromBlockInColumn(index),
                    ChunkUtil.yFromBlockInColumn(index),
                    chunk.getZ() << 5 | ChunkUtil.zFromBlockInColumn(index)
            );
        } else {
            return null;
        }
    }








}
