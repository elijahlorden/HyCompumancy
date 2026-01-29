package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;

public class BoolObject extends VMObject implements IEvaluatable {
    public static final BuilderCodec<BoolObject> CODEC = BuilderCodec.builder(BoolObject.class, BoolObject::new)
            .append(new KeyedCodec<>("Value", Codec.BOOLEAN), BoolObject::SetValue, BoolObject::GetValue)
            .add()
            .build();

    public static final BoolObject TRUE = new BoolObject(true);
    public static final BoolObject FALSE = new BoolObject(false);

    private boolean value;

    public BoolObject() { }
    public BoolObject(boolean value) {
        this.value = value;
    }

    public boolean GetValue()
    {
        return value;
    }

    public void SetValue(boolean value)
    {
        this.value = value;
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
        return new BoolObject(value);
    }

    @Override
    public int ExecutionBudgetCost() {
        return 1;
    }

    @Override
    public void Evaluate(Invocation invocation) throws VMException {
        invocation.Push(new BoolObject(value));
    }

    @Override
    public boolean IsEvalSynchronous() {
        return false;
    }
}
