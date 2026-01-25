package me.freznel.compumancy;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import me.freznel.compumancy.vm.RegisterVMObjects;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.actions.stack.DuplicateAction;

import java.util.logging.Level;

public class Compumancy extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public Compumancy(JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from %s version %s", this.getName(), this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        LOGGER.at(Level.INFO).log("Running setup");
        RegisterVMObjects.Register();

        this.getCommandRegistry().registerCommand(new TestCommand(this.getName()));
    }





}
