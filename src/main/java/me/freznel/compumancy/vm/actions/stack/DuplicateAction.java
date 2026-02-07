package me.freznel.compumancy.vm.actions.stack;

import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;

public class DuplicateAction extends VMAction {

    @Override
    public int executionBudgetCost() {
        return 1;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.getOperandCount() == 0) throw new StackUnderflowException("dup expected at least 1 operand");
        invocation.push(invocation.peek().clone()); //Non-destructive read, cloning required
    }
}
