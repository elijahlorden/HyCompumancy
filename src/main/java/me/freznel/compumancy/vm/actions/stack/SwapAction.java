package me.freznel.compumancy.vm.actions.stack;

import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.execution.Invocation;

public class SwapAction extends VMAction {

    @Override
    public int executionBudgetCost() {
        return 1;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.getOperandCount() < 2) throw new StackUnderflowException("swap expected at least 2 operand");
        var b = invocation.pop();
        var a = invocation.pop();
        invocation.push(b);
        invocation.push(a);
    }
}
