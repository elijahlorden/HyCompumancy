package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.exceptions.StackUnderflowException;
import me.freznel.compumancy.vm.interfaces.IExecutable;
import me.freznel.compumancy.vm.operators.UnaryOperator;
import me.freznel.compumancy.vm.operators.UnaryOperatorSet;

public class UnaryOperatorObject extends VMObject implements IExecutable {
    public static final BuilderCodec<UnaryOperatorObject> CODEC = BuilderCodec.builder(UnaryOperatorObject.class, UnaryOperatorObject::new)
            .append(new KeyedCodec<>("Ref", new EnumCodec<>(UnaryOperator.class)), UnaryOperatorObject::SetOperator, UnaryOperatorObject::GetOperator)
            .add()
            .build();

    static {
        VMObject.CODEC.register("OP1", UnaryOperatorObject.class, CODEC);
    }

    private UnaryOperator operator;

    public UnaryOperatorObject() { }
    public UnaryOperatorObject(UnaryOperator operator) { this.operator = operator; }

    public UnaryOperator GetOperator() { return operator; }
    public void SetOperator(UnaryOperator operator) { this.operator = operator; }

    @Override
    public String GetName() { return "UnaryOperator"; }

    @Override
    public int GetSize() { return 1; }

    @Override
    public String toString() {
        return operator == null ? "UnaryOperator" : "Operator: " + operator.toString();
    }

    @Override
    public VMObject clone() {
        return new UnaryOperatorObject(operator);
    }

    @Override
    public int ExecutionBudgetCost() {
        return 1;
    }

    @Override
    public void Execute(Invocation invocation) {
        if (invocation.OperandCount() < 2) throw new StackUnderflowException(operator.toString() + " operator expected at least 1 operand");
        VMObject a = invocation.Pop();
        invocation.Push(UnaryOperatorSet.Operate(operator, a));
    }
}
