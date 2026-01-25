package me.freznel.compumancy.vm.actions.stack;

import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;

public class DuplicateAction extends VMAction {

    @Override
    public int ExecutionBudgetCost() {
        return 2;
    }

    @Override
    public void Execute(Invocation invocation) {
        if (invocation.OperandCount() == 0) throw new StackUnderflowException("dup expected at least 1 operand");
        invocation.Push(invocation.Peek().clone());
    }
}
