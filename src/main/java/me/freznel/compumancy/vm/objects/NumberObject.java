package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.interfaces.IExecutable;

public class NumberObject extends VMObject implements IExecutable {
    public static final BuilderCodec<NumberObject> CODEC = BuilderCodec.builder(NumberObject.class, NumberObject::new)
            .append(new KeyedCodec<>("Value", Codec.DOUBLE), NumberObject::SetValue, NumberObject::GetValue)
            .add()
            .build();

    static {
        VMObject.CODEC.register("Number", NumberObject.class, CODEC);
    }

    private double value;

    public NumberObject() { super(); }
    public NumberObject(double value) {
        this.value = value;
    }

    public double GetValue()
    {
        return value;
    }

    public void SetValue(double value)
    {
        this.value = value;
    }

    @Override
    public String GetName() {
        return "Number";
    }

    @Override
    public int GetSize() { return 1; }

    @Override
    public String toString()
    {
        return String.format("%.2f", value);
    }

    @Override
    public VMObject clone() {
        return new NumberObject(value);
    }

    @Override
    public int ExecutionBudgetCost() {
        return 1;
    }

    @Override
    public void Execute(Invocation invocation) throws VMException {
        invocation.Push(new NumberObject(value));
    }
}
