package me.freznel.compumancy.vm.operators.binary;

import me.freznel.compumancy.vm.objects.BoolObject;
import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;
import me.freznel.compumancy.vm.operators.BinaryOperatorSet;

public class BooleanBooleanBinaryOperatorSet extends BinaryOperatorSet<BoolObject, BoolObject> {

    private static double ToDouble(BoolObject o) { return o.getValue() ? 1 : 0; }

    @Override
    public VMObject Add(BoolObject a, BoolObject b) {
        return new NumberObject(ToDouble(a) + ToDouble(b));
    }
    @Override
    public VMObject Subtract(BoolObject a, BoolObject b) {
        return new NumberObject(ToDouble(a) - ToDouble(b));
    }
    @Override
    public VMObject Multiply(BoolObject a, BoolObject b) {
        return new NumberObject(ToDouble(a) * ToDouble(b));
    }

    @Override
    public VMObject And(BoolObject a, BoolObject b) { return (a.getValue() && b.getValue()) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Nand(BoolObject a, BoolObject b) { return !(a.getValue() && b.getValue()) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Or(BoolObject a, BoolObject b) { return (a.getValue() || b.getValue()) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Xor(BoolObject a, BoolObject b) { return (a.getValue() ^ b.getValue()) ? BoolObject.TRUE : BoolObject.FALSE; }

    @Override
    public VMObject Equal(BoolObject a, BoolObject b) { return a.getValue() == b.getValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject NotEqual(BoolObject a, BoolObject b) { return a.getValue() != b.getValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject GreaterThan(BoolObject a, BoolObject b) { return a.getValue() && !b.getValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject LessThan(BoolObject a, BoolObject b) { return !a.getValue() && b.getValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject GreaterThanOrEqualTo(BoolObject a, BoolObject b) { return a.getValue() || b.getValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject LessThanOrEqualTo(BoolObject a, BoolObject b) { return a.getValue() || b.getValue() ? BoolObject.TRUE : BoolObject.FALSE; }

}
