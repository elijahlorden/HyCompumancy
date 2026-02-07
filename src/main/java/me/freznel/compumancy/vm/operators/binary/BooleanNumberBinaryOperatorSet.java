package me.freznel.compumancy.vm.operators.binary;

import me.freznel.compumancy.vm.objects.BoolObject;
import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;
import me.freznel.compumancy.vm.operators.BinaryOperatorSet;

public class BooleanNumberBinaryOperatorSet extends BinaryOperatorSet<BoolObject, NumberObject> {

    private static double ToDouble(BoolObject o) { return o.getValue() ? 1 : 0; }

    @Override
    public VMObject Add(BoolObject a, NumberObject b) {
        return new NumberObject(ToDouble(a) + b.getValue());
    }
    @Override
    public VMObject Subtract(BoolObject a, NumberObject b) {
        return new NumberObject(ToDouble(a) - b.getValue());
    }
    @Override
    public VMObject Multiply(BoolObject a, NumberObject b) {
        return new NumberObject(ToDouble(a) * b.getValue());
    }

    @Override
    public VMObject And(BoolObject a, NumberObject b) { return (a.getValue() && b.getValue() > 0) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Nand(BoolObject a, NumberObject b) { return !(a.getValue() && b.getValue() > 0) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Or(BoolObject a, NumberObject b) { return (a.getValue() || b.getValue() > 0) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Xor(BoolObject a, NumberObject b) { return (a.getValue() ^ b.getValue() > 0) ? BoolObject.TRUE : BoolObject.FALSE; }

    @Override
    public VMObject Equal(BoolObject a, NumberObject b) { return ToDouble(a) == b.getValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject NotEqual(BoolObject a, NumberObject b) { return ToDouble(a) != b.getValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject GreaterThan(BoolObject a, NumberObject b) { return ToDouble(a) > b.getValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject LessThan(BoolObject a, NumberObject b) { return ToDouble(a) < b.getValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject GreaterThanOrEqualTo(BoolObject a, NumberObject b) { return ToDouble(a) >= b.getValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject LessThanOrEqualTo(BoolObject a, NumberObject b) { return ToDouble(a) <= b.getValue() ? BoolObject.TRUE : BoolObject.FALSE; }


}