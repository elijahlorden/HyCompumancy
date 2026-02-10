package me.freznel.compumancy.ecs.system.block;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public interface TickingBlockSubsystem<T extends Component<ChunkStore>> {

    public Query<ChunkStore> getQuery();

    public boolean tick(float dt, CommandBuffer<ChunkStore> buffer, BlockSection section, BlockComponentChunk blockComponentChunk, Ref<ChunkStore> blockRef, BlockModule.BlockStateInfo info);

}
