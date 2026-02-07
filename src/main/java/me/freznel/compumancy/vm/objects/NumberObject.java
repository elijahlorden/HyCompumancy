package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;

public final class NumberObject extends VMObject implements IEvaluatable {
    public static final NumberObject ZERO = new NumberObject(0);

    public static final BuilderCodec<NumberObject> CODEC = BuilderCodec.builder(NumberObject.class, NumberObject::new)
            .append(new KeyedCodec<>("Value", Codec.DOUBLE), (o, v) -> o.value = v == null ? 0 : v, NumberObject::getValue)
            .add()
            .build();

    private double value;

    public NumberObject() { this.value = 0; }
    public NumberObject(double value) {
        this.value = value;
    }

    public double getValue()
    {
        return this.value;
    }

    @Override
    public String getObjectName() {
        return "Number";
    }

    @Override
    public int getObjectSize() { return 1; }

    @Override
    public String toString()
    {
        return String.format("%.2f", value);
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
        invocation.push(this);
    }

    @Override
    public boolean isEvalSynchronous() {
        return false;
    }
}
