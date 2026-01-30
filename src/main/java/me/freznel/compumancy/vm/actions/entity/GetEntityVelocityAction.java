package me.freznel.compumancy.vm.actions.entity;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.freznel.compumancy.vm.actions.ActionHelpers;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.OutOfAmbitException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.EntityRefObject;
import me.freznel.compumancy.vm.objects.NullObject;
import me.freznel.compumancy.vm.objects.Vector3Object;

public class GetEntityVelocityAction extends VMAction {
    @Override
    public int ExecutionBudgetCost() {
        return 2;
    }

    @Override
    public boolean ExecuteSynchronous() {
        return true;
    }

    @Override
    public void Execute(Invocation invocation) {
        var ref = ActionHelpers.GetSyncEntityArgument(invocation, "entity:get-velocity");
        if (ref == null || !ref.isValid()) { invocation.Push(NullObject.NULL); return; }
        var store = ref.getStore();
        var vel = store.getComponent(ref, Velocity.getComponentType());
        if (vel == null) { invocation.Push(NullObject.NULL); return; }
        invocation.Push(new Vector3Object(vel.getVelocity()));
    }
}
