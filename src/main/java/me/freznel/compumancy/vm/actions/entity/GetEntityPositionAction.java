package me.freznel.compumancy.vm.actions.entity;

import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import me.freznel.compumancy.vm.actions.ActionHelpers;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.NullObject;
import me.freznel.compumancy.vm.objects.Vector3Object;

public class GetEntityPositionAction extends VMAction {
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
        var ref = ActionHelpers.getSyncEntityArgument(invocation, "entity:get-position");
        if (ref == null || !ref.isValid()) { invocation.push(NullObject.NULL); return; }
        var store = ref.getStore();
        var transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) { invocation.push(NullObject.NULL); return; }
        invocation.push(new Vector3Object(transform.getPosition()));
    }
}
