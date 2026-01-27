package me.freznel.compumancy.vm.operators.binary;

import me.freznel.compumancy.vm.objects.BoolObject;
import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;
import me.freznel.compumancy.vm.operators.BinaryOperatorSet;

public class NumberBooleanBinaryOperatorSet extends BinaryOperatorSet<NumberObject, BoolObject> {

    private static double ToDouble(BoolObject o) { return o.GetValue() ? 1 : 0; }

    @Override
    public VMObject Add(NumberObject a, BoolObject b) {
        return new NumberObject(a.GetValue() + ToDouble(b));
    }
    @Override
    public VMObject Subtract(NumberObject a, BoolObject b) {
        return new NumberObject(a.GetValue() - ToDouble(b));
    }
    @Override
    public VMObject Multiply(NumberObject a, BoolObject b) {
        return new NumberObject(a.GetValue() * ToDouble(b));
    }

    @Override
    public VMObject And(NumberObject a, BoolObject b) { return (a.GetValue() > 0 && b.GetValue()) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Nand(NumberObject a, BoolObject b) { return !(a.GetValue() > 0 && b.GetValue()) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Or(NumberObject a, BoolObject b) { return (a.GetValue() > 0 || b.GetValue()) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Xor(NumberObject a, BoolObject b) { return (a.GetValue() > 0 ^ b.GetValue()) ? BoolObject.TRUE : BoolObject.FALSE; }

}
