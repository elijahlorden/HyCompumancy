package me.freznel.compumancy.vm.actions.stack;

import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.execution.Invocation;

public class DropAction extends VMAction {

    @Override
    public int ExecutionBudgetCost() {
        return 2;
    }

    @Override
    public void Execute(Invocation invocation) {
        if (invocation.OperandCount() == 0) throw new StackUnderflowException("drop expected at least 1 operand");
        invocation.Pop();
    }
}
