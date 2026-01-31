package me.freznel.compumancy.commands;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import me.freznel.compumancy.commands.invocation.InvocationsCommandCollection;
import org.jspecify.annotations.NonNull;

public class CompumancyCommandCollection extends AbstractCommandCollection {
    public CompumancyCommandCollection() {
        super("compumancy", "Commands for the Compumancy plugin");
        this.addAliases("compu");

        this.addSubCommand(new TestCommand());

        this.addSubCommand(new InvocationsCommandCollection());

        this.setPermissionGroup(GameMode.Adventure);
    }
}
