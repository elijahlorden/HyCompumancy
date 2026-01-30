package me.freznel.compumancy.vm.actions.entity;

import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.util.TargetUtil;
import me.freznel.compumancy.vm.actions.ActionHelpers;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.NullObject;
import me.freznel.compumancy.vm.objects.Vector3Object;

public class GetEntityHeadPositionAction extends VMAction {
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
        var ref = ActionHelpers.GetSyncEntityArgument(invocation, "entity:get-head-position");
        if (ref == null || !ref.isValid()) { invocation.Push(NullObject.NULL); return; }
        var store = ref.getStore();
        var look = TargetUtil.getLook(ref, store);
        invocation.Push(new Vector3Object(look.getPosition()));
    }
}
