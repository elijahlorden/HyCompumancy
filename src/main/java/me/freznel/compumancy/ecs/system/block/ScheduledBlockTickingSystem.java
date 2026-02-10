package me.freznel.compumancy.ecs.system.block;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScheduledBlockTickingSystem extends EntityTickingSystem<ChunkStore> {
    private static Query<ChunkStore> QUERY;

    private final Map<ComponentType<ChunkStore, ?>, TickingBlockSubsystem<?>> subsystems;
    private final Map<Archetype<ChunkStore>, TickingBlockSubsystem<?>[]> archetypeMap;

    public ScheduledBlockTickingSystem() {
        QUERY = Query.and(BlockSection.getComponentType(), ChunkSection.getComponentType());
        subsystems = new HashMap<>();
        archetypeMap = new HashMap<>();
    }

    public void Register(TickingBlockSubsystem<?> subsystem) {
        var type = subsystem.getComponentType();
        subsystems.put(type, subsystem);
    }

    @Override
    public @Nullable Query<ChunkStore> getQuery() {
        return QUERY;
    }

    @Override
    public void tick(float dt, int index, @NonNull ArchetypeChunk<ChunkStore> archetypeChunk, @NonNull Store<ChunkStore> store, @NonNull CommandBuffer<ChunkStore> commandBuffer) {
        var blocks = archetypeChunk.getComponent(index, BlockSection.getComponentType());
        assert blocks != null;
        if (blocks.getTickingBlocksCountCopy() == 0) return;
        var section = archetypeChunk.getComponent(index, ChunkSection.getComponentType());
        assert section != null;
        var blockComponentChunk = commandBuffer.getComponent(section.getChunkColumnReference(), BlockComponentChunk.getComponentType());
        assert blockComponentChunk != null;
        blocks.forEachTicking(blockComponentChunk, commandBuffer, section.getY(), (blockComponentChunk1, commandBuffer1, localX, localY, localZ, blockId) -> {
            Ref<ChunkStore> blockRef = blockComponentChunk1.getEntityReference(ChunkUtil.indexBlockInColumn(localX, localY, localZ));
            if (blockRef == null) return BlockTickStrategy.IGNORED;
            BlockModule.BlockStateInfo info = commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());

            //Cache a list of subsystems by archetype
            var archetype = commandBuffer1.getArchetype(blockRef);
            var subsystemArr = archetypeMap.get(archetype);

            if (subsystemArr == null) {
                ArrayList<TickingBlockSubsystem<?>> newArr = new ArrayList<>();
                for (int i = 0; i < archetype.count(); i++) {
                    var ct = archetype.get(i);
                    var sub = subsystems.get(ct);
                    if (sub != null) newArr.add(sub);
                }
                subsystemArr = newArr.toArray(new TickingBlockSubsystem<?>[0]);
                archetypeMap.put(archetype, subsystemArr);
            }
            if (subsystemArr.length == 0) return BlockTickStrategy.IGNORED;

            boolean sleep = false;
            for (var subsystem : subsystemArr) {
                sleep |= subsystem.tick(dt, commandBuffer1, blocks, blockComponentChunk1, blockRef, info);
            }

            return sleep ? BlockTickStrategy.SLEEP : BlockTickStrategy.CONTINUE;
        });

    }

}