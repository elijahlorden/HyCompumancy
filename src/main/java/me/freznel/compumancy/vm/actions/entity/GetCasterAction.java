package me.freznel.compumancy.vm.actions.entity;

import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.EntityRefObject;
import me.freznel.compumancy.vm.objects.NullObject;

public class GetCasterAction extends VMAction {

    @Override
    public int ExecutionBudgetCost() {
        return 1;
    }

    @Override
    public void Execute(Invocation invocation) {
        var caster = invocation.GetCaster();
        invocation.Push(caster == null ? NullObject.NULL : EntityRefObject.FromRef(caster));
    }
}
