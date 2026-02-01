package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
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
            BoolObject::GetValue,
            v -> v ? TRUE : FALSE
    );

    private final boolean value;

    public BoolObject(boolean value) {
        this.value = value;
    }

    public boolean GetValue()
    {
        return value;
    }

    @Override
    public String GetObjectName() {
        return "Boolean";
    }

    @Override
    public int GetObjectSize() { return 1; }

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
    public int ExecutionBudgetCost() {
        return 1;
    }

    @Override
    public void Evaluate(Invocation invocation) throws VMException {
        invocation.Push(this); //Immutable object
    }

    @Override
    public boolean IsEvalSynchronous() {
        return false;
    }
}
