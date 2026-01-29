package me.freznel.compumancy;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.casting.InvocationComponent;
import me.freznel.compumancy.vm.RegisterVMObjects;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.actions.stack.DuplicateAction;

import java.util.logging.Level;

public class Compumancy extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static Compumancy instance;
    public static Compumancy Get() { return instance; }

    private ComponentType<EntityStore, InvocationComponent> invocationComponentType;
    public ComponentType<EntityStore, InvocationComponent> GetInvocationComponentType() { return invocationComponentType; }


    public Compumancy(JavaPluginInit init) {
        super(init);
        instance = this;
        LOGGER.atInfo().log("Hello from %s version %s", this.getName(), this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("Running setup");
        RegisterVMObjects.Register();
        ComponentRegistryProxy<EntityStore> entityStoreComponentRegistry = this.getEntityStoreRegistry();

        invocationComponentType = entityStoreComponentRegistry.registerComponent(InvocationComponent.class, "InvocationComponent", InvocationComponent.CODEC);

        this.getCommandRegistry().registerCommand(new TestCommand(this.getName()));
    }





}
