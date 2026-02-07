package me.freznel.compumancy.vm.actions.stack;

import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.execution.Invocation;

public class OverAction extends VMAction {

    @Override
    public int executionBudgetCost() {
        return 1;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.getOperandCount() < 2) throw new StackUnderflowException("over expected at least 2 operand");
        var b = invocation.pop();
        var a = invocation.peek();
        invocation.push(b);
        invocation.push(a.clone());
    }
}
