package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import me.freznel.compumancy.codec.CustomMapCodec;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.operators.UnaryOperator;
import me.freznel.compumancy.vm.operators.UnaryOperatorSet;


public final class UnaryOperatorObject extends VMObject implements IEvaluatable {

    public static final CustomMapCodec<UnaryOperatorObject> CODEC = new CustomMapCodec<>(
            UnaryOperatorObject.class,
            "Op",
            (UnaryOperatorObject obj) -> obj.GetOperator().toString(),
            (String opStr) -> {
                if (opStr == null) return UnaryOperator.Invalid.Instance;
                try {
                    return Enum.valueOf(UnaryOperator.class, opStr).Instance;
                } catch (Exception _) {
                    return UnaryOperator.Invalid.Instance;
                }
            }
    );

    /*public static final BuilderCodec<UnaryOperatorObject> CODEC = BuilderCodec.builder(UnaryOperatorObject.class, UnaryOperatorObject::new)
            .append(new KeyedCodec<>("Ref", new EnumCodec<>(UnaryOperator.class)), UnaryOperatorObject::SetOperator, UnaryOperatorObject::GetOperator)
            .add()
            .build();*/

    private final UnaryOperator operator;

    public UnaryOperatorObject(UnaryOperator operator) { this.operator = operator; }

    public UnaryOperator GetOperator() { return operator; }

    @Override
    public String GetObjectName() { return "UnaryOperator"; }

    @Override
    public int GetObjectSize() { return 1; }

    @Override
    public String toString() {
        return operator == null ? "UnaryOperator" : "Operator: " + operator.toString();
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
        if (invocation.OperandCount() < 2) throw new StackUnderflowException(operator.toString() + " operator expected at least 1 operand");
        VMObject a = invocation.Pop();
        invocation.Push(UnaryOperatorSet.Operate(operator, a));
    }

    @Override
    public boolean IsEvalSynchronous() {
        return false;
    }
}
