package me.freznel.compumancy.vm.operators.binary;

import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;
import me.freznel.compumancy.vm.operators.BinaryOperatorSet;

public class NumberNumberBinaryOperatorSet extends BinaryOperatorSet<NumberObject, NumberObject> {

    @Override
    public VMObject Add(NumberObject a, NumberObject b) {
        return new NumberObject(a.GetValue() + b.GetValue());
    }
    @Override
    public VMObject Subtract(NumberObject a, NumberObject b) {
        return new NumberObject(a.GetValue() - b.GetValue());
    }
    @Override
    public VMObject Multiply(NumberObject a, NumberObject b) {
        return new NumberObject(a.GetValue() * b.GetValue());
    }
    @Override
    public VMObject Divide(NumberObject a, NumberObject b) {
        return new NumberObject(a.GetValue() / b.GetValue());
    }
    @Override
    public VMObject Mod(NumberObject a, NumberObject b) { return new NumberObject(a.GetValue() % b.GetValue()); }

}
