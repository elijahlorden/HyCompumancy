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
    public static final MetaObject INVALID = new MetaObject(MetaOperation.Invalid);
    public static final MetaObject START_DEF = new MetaObject(MetaOperation.StartDef);
    public static final MetaObject END_DEF = new MetaObject(MetaOperation.EndDef);

    public static final MetaObject START_LIST = new MetaObject(MetaOperation.StartList);
    public static final MetaObject END_LIST = new MetaObject(MetaOperation.EndList);

    public static final MetaObject EVAL = new MetaObject(MetaOperation.Eval);
    public static final MetaObject EVAL_CC = new MetaObject(MetaOperation.EvalCC);

    public static final CustomMapCodec<MetaObject> CODEC = new CustomMapCodec<>(
            MetaObject.class,
            "Op",
            (MetaObject obj) -> obj.GetOperation().toString(),
            (String opStr) -> {
                if (opStr == null) return MetaObject.INVALID;
                MetaObject.MetaOperation op;
                try {
                    op = Enum.valueOf(MetaObject.MetaOperation.class, opStr);
                } catch (Exception _) {
                    op = MetaObject.MetaOperation.Invalid;
                }
                return switch (op) {
                    case StartDef -> MetaObject.START_DEF;
                    case EndDef -> MetaObject.END_DEF;
                    case StartList -> MetaObject.START_LIST;
                    case EndList -> MetaObject.END_LIST;
                    case Eval -> MetaObject.EVAL;
                    case EvalCC -> MetaObject.EVAL_CC;
                    default -> MetaObject.INVALID;
                };
            }
        );



    /*public static final ObjectCodecMapCodec<MetaOperation, MetaObject> CODEC = new ObjectCodecMapCodec<>("Op", new EnumCodec<>(MetaOperation.class));

    static {
        CODEC.register(MetaOperation.StartDef, MetaObject.class, BuilderCodec.builder(MetaObject.class, () -> START_DEF).build());
        CODEC.register(MetaOperation.EndDef, MetaObject.class, BuilderCodec.builder(MetaObject.class, () -> END_DEF).build());

        CODEC.register(MetaOperation.StartList, MetaObject.class, BuilderCodec.builder(MetaObject.class, () -> START_LIST).build());
        CODEC.register(MetaOperation.EndList, MetaObject.class, BuilderCodec.builder(MetaObject.class, () -> END_LIST).build());

        CODEC.register(MetaOperation.Eval, MetaObject.class, BuilderCodec.builder(MetaObject.class, () -> EVAL).build());
        CODEC.register(MetaOperation.EvalCC, MetaObject.class, BuilderCodec.builder(MetaObject.class, () -> EVAL_CC).build());
    }*/

    /*public static final BuilderCodec<MetaObject> CODEC = BuilderCodec.builder(MetaObject.class, MetaObject::new)
            .append(new KeyedCodec<>("Op", new EnumCodec<>(UnaryOperator.class)), MetaObject::SetOperator, MetaObject::GetOperator)
            .add()
            .build();*/

    public enum MetaOperation {
        Invalid,
        StartDef,
        EndDef,
        StartList,
        EndList,
        Eval,
        EvalCC
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
