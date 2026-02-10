package me.freznel.compumancy.ecs.system.block;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import me.freznel.compumancy.Compumancy;
import me.freznel.compumancy.ecs.component.BlockCapacitorComponent;
import me.freznel.compumancy.ecs.component.SolarCrystalComponent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SolarCrystalSystems {

    public static class SolarCrystalRefSystem extends RefSystem<ChunkStore> {
        private static ComponentType<ChunkStore, SolarCrystalComponent> COMPONENT_TYPE;

        public SolarCrystalRefSystem() {
            COMPONENT_TYPE = Compumancy.get().getSolarCrystalComponentComponentType();
        }

        @Override
        public @Nullable Query<ChunkStore> getQuery() {
            return COMPONENT_TYPE;
        }

        @Override
        public void onEntityAdded(@NonNull Ref<ChunkStore> ref, @NonNull AddReason addReason, @NonNull Store<ChunkStore> store, @NonNull CommandBuffer<ChunkStore> commandBuffer) {
            WorldTimeResource worldTimeResource = commandBuffer.getExternalData()
                    .getWorld()
                    .getEntityStore()
                    .getStore()
                    .getResource(WorldTimeResource.getResourceType());

            BlockModule.BlockStateInfo info = commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
            assert info != null;

            int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
            int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
            int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
            BlockChunk blockChunk = commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());
            assert blockChunk != null;

            BlockSection blockSection = blockChunk.getSectionAtBlockY(y);
            blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), worldTimeResource.getGameTime().plusSeconds(5));
        }

        @Override
        public void onEntityRemove(@NonNull Ref<ChunkStore> ref, @NonNull RemoveReason removeReason, @NonNull Store<ChunkStore> store, @NonNull CommandBuffer<ChunkStore> commandBuffer) {

        }
    }

    public static class SolarCrystalTickingSubsystem implements TickingBlockSubsystem<SolarCrystalComponent> {
        private static ComponentType<ChunkStore, SolarCrystalComponent> CRYSTAL_COMPONENT_TYPE;
        private static ComponentType<ChunkStore, BlockCapacitorComponent> CAPACITOR_COMPONENT_TYPE;

        public SolarCrystalTickingSubsystem() {
            CRYSTAL_COMPONENT_TYPE = Compumancy.get().getSolarCrystalComponentComponentType();
            CAPACITOR_COMPONENT_TYPE = Compumancy.get().getBlockCapacitorComponentType();
        }

        @Override
        public ComponentType<ChunkStore, SolarCrystalComponent> getComponentType() {
            return CRYSTAL_COMPONENT_TYPE;
        }

        @Override
        public boolean tick(float dt, CommandBuffer<ChunkStore> buffer, BlockSection section, BlockComponentChunk blockComponentChunk, Ref<ChunkStore> blockRef, BlockModule.BlockStateInfo info) {
            var crystal = buffer.getComponent(blockRef, CRYSTAL_COMPONENT_TYPE);
            var capacitor = buffer.getComponent(blockRef, CAPACITOR_COMPONENT_TYPE);
            if (crystal == null || capacitor == null) return true;




            return true;
        }
    }



}





















