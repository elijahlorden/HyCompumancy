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

public class SendMessageAction extends VMAction {
    @Override
    public int executionBudgetCost() {
        return 50;
    }

    @Override
    public boolean isExecuteSynchronous() { return true; }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.getOperandCount() < 2) throw new StackUnderflowException("player:send-message: expected at least 2 operands");
        var b = invocation.pop(); //Any
        var a = invocation.pop(); //EntityRefObject
        if (!(a instanceof EntityRefObject refObj)) throw new InvalidOperationException("send-message: expected Entity, got " + a.getObjectName());
        final String message = b.toString();
        var world = refObj.getWorld();
        if (world == null) return;

        Runnable action = () -> {
            final Ref<EntityStore> ref = refObj.getEntity();
            if (ref == null || !ref.isValid()) return;
            final var store = ref.getStore();
            var player = store.getComponent(ref, Player.getComponentType());
            if (player != null) player.sendMessage(Message.raw(message));
        };

        if (world == invocation.getWorld()) action.run();
        else world.execute(action);
    }
}
