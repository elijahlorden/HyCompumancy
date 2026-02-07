package me.freznel.compumancy.vm.actions.stack;

import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.BoolObject;

/*Select/Ternary action
* (a b c - (a ? b : c))
* */
public class SelectAction extends VMAction {
    @Override
    public int executionBudgetCost() {
        return 1;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.getOperandCount() < 3) throw new StackUnderflowException("? expected at least 3 operands");
        var c = invocation.pop();
        var b = invocation.pop();
        var a = invocation.pop();
        if (!(a instanceof BoolObject boolObject)) throw new InvalidOperationException(String.format("? expected Boolean * *, got %s %s %s", a.getObjectName(), b.getObjectName(), c.getObjectName()));
        invocation.push(boolObject.getValue() ? b : c); //Destructive read, no need to clone
    }
}
