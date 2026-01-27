package me.freznel.compumancy.vm.operators.binary;

import me.freznel.compumancy.vm.objects.BoolObject;
import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;
import me.freznel.compumancy.vm.operators.BinaryOperatorSet;

public class BooleanNumberBinaryOperatorSet extends BinaryOperatorSet<BoolObject, NumberObject> {

    private static double ToDouble(BoolObject o) { return o.GetValue() ? 1 : 0; }

    @Override
    public VMObject Add(BoolObject a, NumberObject b) {
        return new NumberObject(ToDouble(a) + b.GetValue());
    }
    @Override
    public VMObject Subtract(BoolObject a, NumberObject b) {
        return new NumberObject(ToDouble(a) - b.GetValue());
    }
    @Override
    public VMObject Multiply(BoolObject a, NumberObject b) {
        return new NumberObject(ToDouble(a) * b.GetValue());
    }

    @Override
    public VMObject And(BoolObject a, NumberObject b) { return (a.GetValue() && b.GetValue() > 0) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Nand(BoolObject a, NumberObject b) { return !(a.GetValue() && b.GetValue() > 0) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Or(BoolObject a, NumberObject b) { return (a.GetValue() || b.GetValue() > 0) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Xor(BoolObject a, NumberObject b) { return (a.GetValue() ^ b.GetValue() > 0) ? BoolObject.TRUE : BoolObject.FALSE; }

}