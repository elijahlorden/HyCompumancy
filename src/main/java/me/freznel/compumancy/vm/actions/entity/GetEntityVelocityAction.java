package me.freznel.compumancy.vm.actions.entity;

import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import me.freznel.compumancy.vm.actions.ActionHelpers;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.NullObject;
import me.freznel.compumancy.vm.objects.Vector3Object;

public class GetEntityVelocityAction extends VMAction {
    @Override
    public int executionBudgetCost() {
        return 2;
    }

    @Override
    public boolean isExecuteSynchronous() {
        return true;
    }

    @Override
    public void execute(Invocation invocation) {
        var ref = ActionHelpers.getSyncEntityArgument(invocation, "entity:get-velocity");
        if (ref == null || !ref.isValid()) { invocation.push(NullObject.NULL); return; }
        var store = ref.getStore();
        var vel = store.getComponent(ref, Velocity.getComponentType());
        if (vel == null) { invocation.push(NullObject.NULL); return; }
        invocation.push(new Vector3Object(vel.getVelocity()));
    }
}
