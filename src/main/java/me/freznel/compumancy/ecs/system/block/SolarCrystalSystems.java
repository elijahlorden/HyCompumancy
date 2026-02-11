package me.freznel.compumancy.ecs.system.block;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.logging.Level;

public class SolarCrystalSystems {
    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();
    private static ComponentType<ChunkStore, SolarCrystalComponent> CRYSTAL_COMPONENT_TYPE;
    private static ComponentType<ChunkStore, BlockCapacitorComponent> CAPACITOR_COMPONENT_TYPE;

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
            WorldChunk worldChunk = commandBuffer.getComponent(info.getChunkRef(), WorldChunk.getComponentType());
            assert worldChunk != null;

            int index = info.getIndex();
            int x = ChunkUtil.xFromBlockInColumn(index) + worldChunk.getX() * 32;
            int y = ChunkUtil.yFromBlockInColumn(index);
            int z = ChunkUtil.zFromBlockInColumn(index) + worldChunk.getZ() * 32;

            var sectionRef = commandBuffer.getExternalData().getChunkSectionReference(worldChunk.getX(), y / 32, worldChunk.getZ());
            assert sectionRef != null;
            BlockSection section = commandBuffer.getComponent(sectionRef, BlockSection.getComponentType());
            assert section != null;

            var crystal = commandBuffer.getComponent(ref, CRYSTAL_COMPONENT_TYPE);
            if (crystal == null) return;
            var currentTime = worldTimeResource.getGameTime();
            crystal.setTimestamp(currentTime);

            long nextTickDelta = (long) (WorldTimeResource.getSecondsPerTick(commandBuffer.getExternalData().getWorld()) * Duration.ofSeconds(10).toNanos());

            section.scheduleTick(index & (ChunkUtil.SIZE_BLOCKS - 1), currentTime.plusNanos(nextTickDelta));
            Logger.at(Level.INFO).log("RefSystem started ticking");
        }

        @Override
        public void onEntityRemove(@NonNull Ref<ChunkStore> ref, @NonNull RemoveReason removeReason, @NonNull Store<ChunkStore> store, @NonNull CommandBuffer<ChunkStore> commandBuffer) {

        }
    }

    public static class SolarCrystalTickingSubsystem implements TickingBlockSubsystem<SolarCrystalComponent> {

        public SolarCrystalTickingSubsystem() {
            CRYSTAL_COMPONENT_TYPE = Compumancy.get().getSolarCrystalComponentComponentType();
            CAPACITOR_COMPONENT_TYPE = Compumancy.get().getBlockCapacitorComponentType();
        }

        @Override
        public Query<ChunkStore> getQuery() {
            return Query.and(CRYSTAL_COMPONENT_TYPE, CAPACITOR_COMPONENT_TYPE);
        }

        @Override
        public boolean tick(float dt, CommandBuffer<ChunkStore> buffer, BlockSection section, BlockComponentChunk blockComponentChunk, Ref<ChunkStore> blockRef, BlockModule.BlockStateInfo info) {
            var crystal = buffer.getComponent(blockRef, CRYSTAL_COMPONENT_TYPE);
            var capacitor = buffer.getComponent(blockRef, CAPACITOR_COMPONENT_TYPE);
            if (crystal == null || capacitor == null) return true;
            var timeResource = buffer.getExternalData().getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType());
            var currentTime = timeResource.getGameTime();

            var timestamp = crystal.getTimestamp();
            var deltaDuration = Duration.between(timestamp, currentTime);
            if (deltaDuration.compareTo(Duration.ofSeconds(10)) < 0) return true;
            crystal.setTimestamp(currentTime);
            Logger.at(Level.INFO).log("Ticking");

            double secondsPerTick = WorldTimeResource.getSecondsPerTick(buffer.getExternalData().getWorld());

            var sunlight = timeResource.getSunlightFactor();
            int index = info.getIndex();

            if (sunlight > 0.2) {
                var chunk = buffer.getComponent(info.getChunkRef(), WorldChunk.getComponentType());
                if (chunk == null) return true;
                int x = ChunkUtil.xFromBlockInColumn(index);
                int y = ChunkUtil.yFromBlockInColumn(index);
                int z = ChunkUtil.zFromBlockInColumn(index);
                double canSeeSky = chunk.getHeight(x, z) < y ? 1 : 0;
                var delta = (deltaDuration.toNanos() / 1_000_000_000d) / secondsPerTick;
                double added = capacitor.add(crystal.getRate() * delta * sunlight * canSeeSky);
                if (added > 0) {
                    Logger.at(Level.INFO).log(String.format("Spawning particles, %.2f", capacitor.getCharge()));
                    ParticleUtil.spawnParticleEffect("SolarCollectorParticleSystem", new Vector3d(x + chunk.getX() * ChunkUtil.SIZE, y + 0.5, z + chunk.getZ() * ChunkUtil.SIZE), buffer.getExternalData().getWorld().getEntityStore().getStore());
                }
            }

            long nextTickDelta = (long) secondsPerTick * Duration.ofSeconds(10).toNanos();

            section.scheduleTick(index & (ChunkUtil.SIZE_BLOCKS - 1), currentTime.plusNanos(nextTickDelta));
            return true;
        }
    }



}





















