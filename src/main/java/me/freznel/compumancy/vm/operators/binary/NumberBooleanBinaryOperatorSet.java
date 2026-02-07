package me.freznel.compumancy.vm.operators.binary;

import me.freznel.compumancy.vm.objects.BoolObject;
import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;
import me.freznel.compumancy.vm.operators.BinaryOperatorSet;

public class NumberBooleanBinaryOperatorSet extends BinaryOperatorSet<NumberObject, BoolObject> {

    private static double ToDouble(BoolObject o) { return o.getValue() ? 1 : 0; }

    @Override
    public VMObject Add(NumberObject a, BoolObject b) {
        return new NumberObject(a.getValue() + ToDouble(b));
    }
    @Override
    public VMObject Subtract(NumberObject a, BoolObject b) {
        return new NumberObject(a.getValue() - ToDouble(b));
    }
    @Override
    public VMObject Multiply(NumberObject a, BoolObject b) {
        return new NumberObject(a.getValue() * ToDouble(b));
    }

    @Override
    public VMObject And(NumberObject a, BoolObject b) { return (a.getValue() > 0 && b.getValue()) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Nand(NumberObject a, BoolObject b) { return !(a.getValue() > 0 && b.getValue()) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Or(NumberObject a, BoolObject b) { return (a.getValue() > 0 || b.getValue()) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject Xor(NumberObject a, BoolObject b) { return (a.getValue() > 0 ^ b.getValue()) ? BoolObject.TRUE : BoolObject.FALSE; }

    @Override
    public VMObject Equal(NumberObject a, BoolObject b) { return a.getValue() == ToDouble(b) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject NotEqual(NumberObject a, BoolObject b) { return a.getValue() != ToDouble(b) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject GreaterThan(NumberObject a, BoolObject b) { return a.getValue() > ToDouble(b) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject LessThan(NumberObject a, BoolObject b) { return a.getValue() < ToDouble(b) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject GreaterThanOrEqualTo(NumberObject a, BoolObject b) { return a.getValue() >= ToDouble(b) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject LessThanOrEqualTo(NumberObject a, BoolObject b) { return a.getValue() <= ToDouble(b) ? BoolObject.TRUE : BoolObject.FALSE; }


}
