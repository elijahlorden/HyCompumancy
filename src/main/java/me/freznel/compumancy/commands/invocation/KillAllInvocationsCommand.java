package me.freznel.compumancy.commands.invocation;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.Compumancy;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.execution.InvocationState;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public class KillAllInvocationsCommand extends AbstractPlayerCommand {
    public KillAllInvocationsCommand() {
        super("killall", "Kill all invocations");
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void execute(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
        var invocationComponentType = Compumancy.Get().GetInvocationComponentType();
        var invocationComponent = store.getComponent(ref, invocationComponentType);
        if (invocationComponent == null) {
            invocationComponent = store.addComponent(ref, invocationComponentType);
        }

        var iterator = invocationComponent.GetIterator();
        int killed = 0;
        while (iterator.hasNext()) {
            var next = iterator.next();
            UUID invocationId = UUID.fromString(next.getKey());
            var invocation = Invocation.GetRunningInvocation(invocationId);
            if (invocation != null) invocation.Cancel();
            iterator.remove();
            killed++;
        }

        playerRef.sendMessage(Message.raw(String.format("Killed %d invocations", killed)));
    }
}
