package me.freznel.compumancy.vm.objects;

import me.freznel.compumancy.codec.CustomMapCodec;
import me.freznel.compumancy.vm.exceptions.CompileException;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.execution.frame.DefBuilderFrame;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.interfaces.IExecutable;

public final class MetaObject extends VMObject implements IEvaluatable {

    public static final CustomMapCodec<MetaObject> CODEC = new CustomMapCodec<>(
            MetaObject.class,
            "Op",
            (MetaObject obj) -> obj.getOperation().toString(),
            (String opStr) -> {
                if (opStr == null) return MetaOperation.Invalid.Instance;
                try {
                    return Enum.valueOf(MetaOperation.class, opStr).Instance;
                } catch (Exception _) {
                    return MetaOperation.Invalid.Instance;
                }
            }
        );

    public enum MetaOperation {
        Invalid,
        StartDef,
        EndDef,
        StartList,
        EndList,
        Eval,
        EvalCC;

        public final MetaObject Instance;

        MetaOperation() {
            Instance = new MetaObject(this);
        }

    }

    private MetaOperation operation;

    public MetaObject() { }
    public MetaObject(MetaOperation operation) { this.operation = operation; }

    public MetaOperation getOperation() { return operation; }

    @Override
    public String getObjectName() { return "MetaOperation"; }

    @Override
    public int getObjectSize() { return 1; }

    @Override
    public String toString() {
        return operation == null ? "Invalid Meta-Operation" : "Meta-Operation: " + operation.toString();
    }

    @Override
    public VMObject clone() {
        return this; //Immutable object
    }

    @Override
    public int executionBudgetCost() {
        return 1;
    }

    @Override
    public void evaluate(Invocation invocation) {
        switch (operation) {
            case StartDef -> {
                invocation.pushFrame(new DefBuilderFrame());
            }

            case EndDef -> throw new CompileException("Invalid end of definition marker encountered");

            case EndList -> throw new CompileException("Invalid end of list marker encountered");

            case Eval -> {
                if (invocation.getOperandCount() == 0) throw new StackUnderflowException("eval expected at least 1 operand");
                var a = invocation.pop();
                if (a instanceof IExecutable executable) { //Look for IExecutable first
                    executable.execute(invocation);
                } else if (a instanceof IEvaluatable evaluatable) { //Fall back to IEvaluatable
                    evaluatable.evaluate(invocation);
                } else {
                    throw new InvalidOperationException(String.format("eval: Unable to evaluate %s", a.getObjectName()));
                }
            }

            default -> throw new InvalidOperationException("Encountered invalid meta-operation");
        }
    }

    @Override
    public boolean isEvalSynchronous() {
        return false;
    }
}
