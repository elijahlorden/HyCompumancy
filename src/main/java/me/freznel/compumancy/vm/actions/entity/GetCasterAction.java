package me.freznel.compumancy.vm.actions.entity;

import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.EntityRefObject;
import me.freznel.compumancy.vm.objects.NullObject;

public class GetCasterAction extends VMAction {

    @Override
    public int executionBudgetCost() {
        return 1;
    }

    @Override
    public void execute(Invocation invocation) {
        var caster = invocation.getCaster();
        invocation.push(caster == null ? NullObject.NULL : EntityRefObject.fromRef(caster));
    }
}
