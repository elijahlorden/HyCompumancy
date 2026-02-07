package me.freznel.compumancy.vm.actions.entity;

import com.hypixel.hytale.server.core.util.TargetUtil;
import me.freznel.compumancy.vm.actions.ActionHelpers;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.NullObject;
import me.freznel.compumancy.vm.objects.Vector3Object;

public class GetEntityHeadPositionAction extends VMAction {
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
        var ref = ActionHelpers.getSyncEntityArgument(invocation, "entity:get-head-position");
        if (ref == null || !ref.isValid()) { invocation.push(NullObject.NULL); return; }
        var store = ref.getStore();
        var look = TargetUtil.getLook(ref, store);
        invocation.push(new Vector3Object(look.getPosition()));
    }
}
