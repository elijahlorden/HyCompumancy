package me.freznel.compumancy.vm.actions.flow;

import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.execution.frame.NumericIteratorFrame;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.interfaces.IExecutable;
import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;

/*
    Numeric iteration action.  Accepts a start/end range, and an evaluatable/executable object.
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
        if (invocation.OperandCount() < 3) throw new StackUnderflowException("for: expected at least 3 operands");
        var c = invocation.Pop();
        var b = invocation.Pop();
        var a = invocation.Pop();

        //Validate operands
        if (!(a instanceof NumberObject startObj) || !(b instanceof NumberObject endObj)) throw OperandException(a, b, c);
        if (!(c instanceof IExecutable || c instanceof IEvaluatable)) throw OperandException(a, b, c);
        int start = (int)startObj.GetValue();
        int end = (int)endObj.GetValue();
        int inc = start > end ? -1 : 1;

        invocation.PushFrame(new NumericIteratorFrame(start, end, inc, c));
    }

    private static InvalidOperationException OperandException(VMObject a, VMObject b, VMObject c) {
        return new InvalidOperationException(String.format("for: expected Number Number Evaluatable, got %s %s %s", a.GetObjectName(), b.GetObjectName(), c.GetObjectName()));
    }
}
