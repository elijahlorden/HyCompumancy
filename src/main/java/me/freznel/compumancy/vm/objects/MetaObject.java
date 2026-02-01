package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.lookup.ObjectCodecMapCodec;
import me.freznel.compumancy.codec.CustomMapCodec;
import me.freznel.compumancy.vm.exceptions.CompileException;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.execution.frame.DefBuilderFrame;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.interfaces.IExecutable;
import me.freznel.compumancy.vm.operators.UnaryOperator;
import me.freznel.compumancy.vm.operators.UnaryOperatorSet;

public final class MetaObject extends VMObject implements IEvaluatable {

    public static final CustomMapCodec<MetaObject> CODEC = new CustomMapCodec<>(
            MetaObject.class,
            "Op",
            (MetaObject obj) -> obj.GetOperation().toString(),
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

    public MetaOperation GetOperation() { return operation; }

    @Override
    public String GetObjectName() { return "MetaOperation"; }

    @Override
    public int GetObjectSize() { return 1; }

    @Override
    public String toString() {
        return operation == null ? "Invalid Meta-Operation" : "Meta-Operation: " + operation.toString();
    }

    @Override
    public VMObject clone() {
        return this; //Immutable object
    }

    @Override
    public int ExecutionBudgetCost() {
        return 1;
    }

    @Override
    public void Evaluate(Invocation invocation) {
        switch (operation) {
            case StartDef -> {
                invocation.PushFrame(new DefBuilderFrame());
            }

            case EndDef -> throw new CompileException("Invalid end of definition marker encountered");

            case EndList -> throw new CompileException("Invalid end of list marker encountered");

            case Eval -> {
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

            default -> throw new InvalidOperationException("Encountered invalid meta-operation");
        }
    }

    @Override
    public boolean IsEvalSynchronous() {
        return false;
    }
}
