package me.freznel.compumancy;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.util.concurrent.ThreadUtil;
import me.freznel.compumancy.ecs.component.*;
import me.freznel.compumancy.commands.CompumancyCommandCollection;
import me.freznel.compumancy.config.CompumancyConfig;
import me.freznel.compumancy.ecs.system.block.ScheduledBlockTickingSystem;
import me.freznel.compumancy.ecs.system.block.SolarCrystalSystems;
import me.freznel.compumancy.vm.RegisterVMObjects;
import me.freznel.compumancy.vm.store.InvocationStore;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Compumancy extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static Compumancy instance;
    public static Compumancy get() { return instance; }

    //Executors

    private ScheduledExecutorService executor;
    public Executor getDaemonExecutor() { return executor; }
    public void scheduleDaemon(Runnable runnable, long delay) {
        executor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    private Executor userThreadPool;
    public Executor getUserExecutor() { return userThreadPool; }

    //Components

    private ComponentType<EntityStore, InvocationComponent> invocationComponentType;
    public ComponentType<EntityStore, InvocationComponent> getInvocationComponentType() { return invocationComponentType; }

    private ComponentType<EntityStore, DefinitionStoreComponent> definitionStoreComponentType;
    public ComponentType<EntityStore, DefinitionStoreComponent> getDefinitionStoreComponentType() { return definitionStoreComponentType; }

    private ComponentType<ChunkStore, ScheduledBlockComponent> scheduledBlockComponentType;
    public ComponentType<ChunkStore, ScheduledBlockComponent> getScheduledBlockComponentType() { return scheduledBlockComponentType; }

    private ComponentType<ChunkStore, BlockCapacitorComponent> blockCapacitorComponentComponentType;
    public ComponentType<ChunkStore, BlockCapacitorComponent> getBlockCapacitorComponentType() { return blockCapacitorComponentComponentType; }

    private ComponentType<ChunkStore, SolarCrystalComponent> solarCrystalComponentComponentType;
    public ComponentType<ChunkStore, SolarCrystalComponent> getSolarCrystalComponentComponentType() { return solarCrystalComponentComponentType; }

    //Config

    private final Config<CompumancyConfig> config;
    public CompumancyConfig getConfig() { return config.get(); }

    public Compumancy(JavaPluginInit init) {
        super(init);
        instance = this;
        this.config = this.withConfig("CompumancyPlugin", CompumancyConfig.CODEC);
    }

    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("Running setup");

        executor = Executors.newScheduledThreadPool(config.get().AsyncThreadCount);
        executor.scheduleAtFixedRate(InvocationStore::resetExecuteCount, 1000, 1000, TimeUnit.MILLISECONDS);

        LOGGER.at(Level.INFO).log(String.format("Created ScheduledThreadPool with %d threads", config.get().AsyncThreadCount));

        userThreadPool = ThreadUtil.newCachedThreadPool(32, Thread::new);

        RegisterVMObjects.register();

        ComponentRegistryProxy<EntityStore> entityStoreComponentRegistry = this.getEntityStoreRegistry();
        ComponentRegistryProxy<ChunkStore> chunkStoreComponentRegistryProxy = this.getChunkStoreRegistry();

        //Register entity components
        invocationComponentType = entityStoreComponentRegistry.registerComponent(InvocationComponent.class, "FZCOMPU:InvocStore", InvocationComponent.CODEC);
        definitionStoreComponentType = entityStoreComponentRegistry.registerComponent(DefinitionStoreComponent.class, "FZCOMPU:DefStore", DefinitionStoreComponent.CODEC);


        //Register entity systems
        entityStoreComponentRegistry.registerSystem(new InvocationComponent.InvocationComponentRefSystem());

        //Register block components
        scheduledBlockComponentType = chunkStoreComponentRegistryProxy.registerComponent(ScheduledBlockComponent.class, "FZCOMPU:BlkTck", ScheduledBlockComponent.CODEC);
        blockCapacitorComponentComponentType = chunkStoreComponentRegistryProxy.registerComponent(BlockCapacitorComponent.class, "FZCOMPU:Capacitor", BlockCapacitorComponent.CODEC);
        solarCrystalComponentComponentType = chunkStoreComponentRegistryProxy.registerComponent(SolarCrystalComponent.class, "FZCOMPU:SolarCrystal", SolarCrystalComponent.CODEC);

        //Register block systems
        chunkStoreComponentRegistryProxy.registerSystem(new ScheduledBlockTickingSystem(
                new SolarCrystalSystems.SolarCrystalTickingSubsystem()
        ));


        this.getCommandRegistry().registerCommand(new CompumancyCommandCollection());
    }



    @Override
    protected void shutdown() {
        InvocationStore.saveAll(true);
        executor.shutdown();
    }





}
