package me.freznel.compumancy.vm.actions.entity;

import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import me.freznel.compumancy.vm.actions.ActionHelpers;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.NullObject;
import me.freznel.compumancy.vm.objects.Vector3Object;

public class GetEntityRotationAction extends VMAction {
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
        var ref = ActionHelpers.GetSyncEntityArgument(invocation, "entity:get-rotation");
        if (ref == null || !ref.isValid()) { invocation.Push(NullObject.NULL); return; }
        var store = ref.getStore();
        var transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) { invocation.Push(NullObject.NULL); return; }
        var rot = transform.getRotation();
        invocation.Push(new Vector3Object(rot.x, rot.y, rot.z));
    }
}
