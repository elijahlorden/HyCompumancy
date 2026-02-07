package me.freznel.compumancy.vm.objects;

import me.freznel.compumancy.codec.CustomMapCodec;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.operators.BinaryOperator;
import me.freznel.compumancy.vm.operators.BinaryOperatorSet;

public final class BinaryOperatorObject extends VMObject implements IEvaluatable {

    public static final CustomMapCodec<BinaryOperatorObject> CODEC = new CustomMapCodec<>(
            BinaryOperatorObject.class,
            "Op",
            (BinaryOperatorObject obj) -> obj.getOperator().toString(),
            (String opStr) -> {
                if (opStr == null) return BinaryOperator.Invalid.Instance;
                try {
                    return Enum.valueOf(BinaryOperator.class, opStr).Instance;
                } catch (Exception _) {
                    return BinaryOperator.Invalid.Instance;
                }
            }
    );

    /*public static final BuilderCodec<BinaryOperatorObject> CODEC = BuilderCodec.builder(BinaryOperatorObject.class, BinaryOperatorObject::new)
            .append(new KeyedCodec<>("Ref", new EnumCodec<>(BinaryOperator.class)), BinaryOperatorObject::SetOperator, BinaryOperatorObject::GetOperator)
            .add()
            .build();*/

    private final BinaryOperator operator;

    public BinaryOperatorObject(BinaryOperator operator) { this.operator = operator; }

    public BinaryOperator getOperator() { return operator; }

    @Override
    public String getObjectName() { return "BinaryOperator"; }

    @Override
    public int getObjectSize() { return 1; }

    @Override
    public String toString() {
        return operator == null ? "BinaryOperator" : "Operator: " + operator.toString();
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
        if (invocation.getOperandCount() < 2) throw new StackUnderflowException(operator.toString() + " operator expected at least 2 operands");
        VMObject b = invocation.pop();
        VMObject a = invocation.pop();
        invocation.push(BinaryOperatorSet.Operate(operator, a, b));
    }

    @Override
    public boolean isEvalSynchronous() {
        return false;
    }
}
