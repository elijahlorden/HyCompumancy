package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;
import me.freznel.compumancy.vm.operators.BinaryOperator;
import me.freznel.compumancy.vm.operators.BinaryOperatorSet;

public final class BinaryOperatorObject extends VMObject implements IEvaluatable {
    public static final BuilderCodec<BinaryOperatorObject> CODEC = BuilderCodec.builder(BinaryOperatorObject.class, BinaryOperatorObject::new)
            .append(new KeyedCodec<>("Ref", new EnumCodec<>(BinaryOperator.class)), BinaryOperatorObject::SetOperator, BinaryOperatorObject::GetOperator)
            .add()
            .build();

    private BinaryOperator operator;

    public BinaryOperatorObject() { }
    public BinaryOperatorObject(BinaryOperator operator) { this.operator = operator; }

    public BinaryOperator GetOperator() { return operator; }
    private void SetOperator(BinaryOperator operator) { this.operator = operator; }

    @Override
    public String GetObjectName() { return "BinaryOperator"; }

    @Override
    public int GetObjectSize() { return 1; }

    @Override
    public String toString() {
        return operator == null ? "BinaryOperator" : "Operator: " + operator.toString();
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
        if (invocation.OperandCount() < 2) throw new StackUnderflowException(operator.toString() + " operator expected at least 2 operands");
        VMObject b = invocation.Pop();
        VMObject a = invocation.Pop();
        invocation.Push(BinaryOperatorSet.Operate(operator, a, b));
    }

    @Override
    public boolean IsEvalSynchronous() {
        return false;
    }
}
