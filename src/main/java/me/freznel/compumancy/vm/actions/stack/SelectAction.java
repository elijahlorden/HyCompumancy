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
    public int ExecutionBudgetCost() {
        return 1;
    }

    @Override
    public void Execute(Invocation invocation) {
        if (invocation.OperandCount() < 3) throw new StackUnderflowException("? expected at least 3 operands");
        var c = invocation.Pop();
        var b = invocation.Pop();
        var a = invocation.Pop();
        if (!(a instanceof BoolObject boolObject)) throw new InvalidOperationException(String.format("? expected Boolean * *, got %s %s %s", a.GetObjectName(), b.GetObjectName(), c.GetObjectName()));
        invocation.Push(boolObject.GetValue() ? b : c); //Destructive read, no need to clone
    }
}
