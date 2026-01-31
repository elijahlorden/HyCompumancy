package me.freznel.compumancy.commands.invocation;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import org.jspecify.annotations.NonNull;

public class InvocationsCommandCollection extends AbstractCommandCollection {
    public InvocationsCommandCollection() {
        super("invocation", "Commands related to invocations");

        this.addSubCommand(new KillAllInvocationsCommand());
    }
}
