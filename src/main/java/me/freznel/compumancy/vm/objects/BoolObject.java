package me.freznel.compumancy.vm.objects;

import me.freznel.compumancy.codec.BoolMapCodec;
import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;

public final class BoolObject extends VMObject implements IEvaluatable {
    public static final BoolObject TRUE = new BoolObject(true);
    public static final BoolObject FALSE = new BoolObject(false);

    public static final BoolMapCodec<BoolObject> CODEC = new BoolMapCodec<>(
            BoolObject.class,
            "Value",
            BoolObject::getValue,
            v -> v ? TRUE : FALSE
    );

    private final boolean value;

    public BoolObject(boolean value) {
        this.value = value;
    }

    public boolean getValue()
    {
        return value;
    }

    @Override
    public String getObjectName() {
        return "Boolean";
    }

    @Override
    public int getObjectSize() { return 1; }

    @Override
    public String toString()
    {
        return value ? "true" : "false";
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
    public void evaluate(Invocation invocation) throws VMException {
        invocation.push(this); //Immutable object
    }

    @Override
    public boolean isEvalSynchronous() {
        return false;
    }
}
