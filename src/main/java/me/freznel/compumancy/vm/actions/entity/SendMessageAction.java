package me.freznel.compumancy.vm.actions.entity;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.EntityRefObject;

import java.util.concurrent.CompletableFuture;

public class SendMessageAction extends VMAction {
    @Override
    public int ExecutionBudgetCost() {
        return 50;
    }

    @Override
    public void Execute(Invocation invocation) {
        if (invocation.OperandCount() < 2) throw new StackUnderflowException("player:send-message: expected at least 2 operands");
        var b = invocation.Pop(); //Any
        var a = invocation.Pop(); //EntityRefObject
        if (!(a instanceof EntityRefObject refObj)) throw new InvalidOperationException("player:send-message: expected Entity, got " + a.GetName());
        final Ref<EntityStore> ref = refObj.GetEntity();
        if (ref == null || !ref.isValid()) return;
        final var store = ref.getStore();
        final String message = b.toString();

        CompletableFuture.runAsync(() -> {
            var player = store.getComponent(ref, Player.getComponentType());
            if (player != null) player.sendMessage(Message.raw(message));
        }, store.getExternalData().getWorld()).join();
    }
}
