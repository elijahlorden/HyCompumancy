package me.freznel.compumancy;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.casting.DefinitionStoreComponent;
import me.freznel.compumancy.casting.InvocationComponent;
import me.freznel.compumancy.commands.CompumancyCommandCollection;
import me.freznel.compumancy.commands.TestCommand;
import me.freznel.compumancy.vm.RegisterVMObjects;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Compumancy extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static Compumancy instance;
    public static Compumancy Get() { return instance; }

    private ScheduledExecutorService executor;
    public void Schedule(Runnable runnable, long delay) {
        executor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    private ComponentType<EntityStore, InvocationComponent> invocationComponentType;
    public ComponentType<EntityStore, InvocationComponent> GetInvocationComponentType() { return invocationComponentType; }

    private ComponentType<EntityStore, DefinitionStoreComponent> definitionStoreComponentType;
    public ComponentType<EntityStore, DefinitionStoreComponent> GetDefinitionStoreComponentType() { return definitionStoreComponentType; }


    public Compumancy(JavaPluginInit init) {
        super(init);
        instance = this;
        LOGGER.atInfo().log("Hello from %s version %s", this.getName(), this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("Running setup");
        executor = Executors.newScheduledThreadPool(4);


        RegisterVMObjects.Register();
        ComponentRegistryProxy<EntityStore> entityStoreComponentRegistry = this.getEntityStoreRegistry();

        invocationComponentType = entityStoreComponentRegistry.registerComponent(InvocationComponent.class, "InvocationComponent", InvocationComponent.CODEC);
        definitionStoreComponentType = entityStoreComponentRegistry.registerComponent(DefinitionStoreComponent.class, "DefinitionStoreComponent", DefinitionStoreComponent.CODEC);

        this.getCommandRegistry().registerCommand(new CompumancyCommandCollection());
    }



    @Override
    protected void shutdown() {
        executor.shutdown();
    }





}
