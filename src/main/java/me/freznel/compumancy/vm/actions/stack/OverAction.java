package me.freznel.compumancy.vm.actions.stack;

import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.execution.Invocation;

public class OverAction extends VMAction {

    @Override
    public int ExecutionBudgetCost() {
        return 1;
    }

    @Override
    public void Execute(Invocation invocation) {
        if (invocation.OperandCount() < 2) throw new StackUnderflowException("over expected at least 2 operand");
        var b = invocation.Pop();
        var a = invocation.Peek();
        invocation.Push(b);
        invocation.Push(a.clone());
    }
}
