package me.freznel.compumancy.vm.actions.stack;

import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.interfaces.IExecutable;

public class EvalAction extends VMAction {
    @Override
    public int ExecutionBudgetCost() {
        return 5;
    }

    @Override
    public void Execute(Invocation invocation) {
        if (invocation.OperandCount() == 0) throw new StackUnderflowException("eval expected at least 1 operand");
        var a = invocation.Pop();
        if (a instanceof IExecutable executable) { //Look for IExecutable first
            executable.Execute(invocation);
        } else if (a instanceof IEvaluatable evaluatable) { //Fall back to IEvaluatable
            evaluatable.Evaluate(invocation);
        } else {
            throw new InvalidOperationException(String.format("eval: Unable to evaluate %s", a.GetObjectName()));
        }
    }
}
