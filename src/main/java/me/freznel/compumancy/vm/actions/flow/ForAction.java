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
    public int executionBudgetCost() {
        return 10;
    }

    @Override
    public void execute(Invocation invocation) {
        if (invocation.getOperandCount() < 3) throw new StackUnderflowException("for: expected at least 3 operands");
        var c = invocation.pop();
        var b = invocation.pop();
        var a = invocation.pop();

        //Validate operands
        if (!(a instanceof NumberObject startObj) || !(b instanceof NumberObject endObj)) throw OperandException(a, b, c);
        if (!(c instanceof IExecutable || c instanceof IEvaluatable)) throw OperandException(a, b, c);
        int start = (int)startObj.getValue();
        int end = (int)endObj.getValue();
        int inc = start > end ? -1 : 1;

        invocation.pushFrame(new NumericIteratorFrame(start, end, inc, c));
    }

    private static InvalidOperationException OperandException(VMObject a, VMObject b, VMObject c) {
        return new InvalidOperationException(String.format("for: expected Number Number Evaluatable, got %s %s %s", a.getObjectName(), b.getObjectName(), c.getObjectName()));
    }
}
