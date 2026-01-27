package me.freznel.compumancy.vm.actions.stack;

import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.interfaces.IExecutable;
import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;

/*
    Numeric iteration action.  Accepts a start/end index, an increment, and an evaluatable/executable object.
    Each iteration will push the iterator variable onto the stack and then execute/evaluate the object.

    Stack effect: (start end increment evaluatable - many)
    Per-iteration stack effect: ( number )
 */
public class ForAction extends VMAction {
    @Override
    public int ExecutionBudgetCost() {
        return 10;
    }

    @Override
    public void Execute(Invocation invocation) {
        if (invocation.OperandCount() < 3) throw new StackUnderflowException("for: expected at least 4 operands");
        var d = invocation.Pop();
        var c = invocation.Pop();
        var b = invocation.Pop();
        var a = invocation.Pop();

        //Validate operands
        if (!(a instanceof NumberObject startObj) || !(b instanceof NumberObject endObj) || !(c instanceof NumberObject incObj)) throw OperandException(a, b, c, d);
        if (!(d instanceof IExecutable || d instanceof IEvaluatable)) throw OperandException(a, b, c, d);
        int start = (int)startObj.GetValue();
        int end = (int)endObj.GetValue();
        int inc = (int)incObj.GetValue();

        //Validate bounds.  Maybe unnecessary?  Violating this would result in an infinite loop.
        if ((end > start && inc <= 0) || (start > end && inc >= 0)) throw new InvalidOperationException(String.format("For: increment %d is invalid with bounds %d to %d", inc, start, end));

        //TODO: Implement NumericIteratorFrame class for this action


    }

    private static InvalidOperationException OperandException(VMObject a, VMObject b, VMObject c, VMObject d) {
        return new InvalidOperationException(String.format("for: expected Number Number Number Evaluatable, got %s %s %s %s", a.GetName(), b.GetName(), c.GetName(), d.GetName()));
    }
}
