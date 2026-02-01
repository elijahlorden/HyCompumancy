package me.freznel.compumancy.vm.operators.binary;

import me.freznel.compumancy.vm.objects.BoolObject;
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

    @Override
    public VMObject And(NumberObject a, NumberObject b) { return new NumberObject((((long)a.GetValue()) & ((long)b.GetValue())) >> 12); }
    @Override
    public VMObject Nand(NumberObject a, NumberObject b) { return new NumberObject((~((long)a.GetValue()) & ((long)b.GetValue())) >> 12);  }
    @Override
    public VMObject Or(NumberObject a, NumberObject b) { return new NumberObject((((long)a.GetValue()) | ((long)b.GetValue())) >> 12);  }
    @Override
    public VMObject Xor(NumberObject a, NumberObject b) { return new NumberObject((((long)a.GetValue()) ^ ((long)b.GetValue())) >> 12);  }

    @Override
    public VMObject Equal(NumberObject a, NumberObject b) { return a.GetValue() == b.GetValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject NotEqual(NumberObject a, NumberObject b) { return a.GetValue() != b.GetValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject GreaterThan(NumberObject a, NumberObject b) { return a.GetValue() > b.GetValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject LessThan(NumberObject a, NumberObject b) { return a.GetValue() < b.GetValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject GreaterThanOrEqualTo(NumberObject a, NumberObject b) { return a.GetValue() >= b.GetValue() ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject LessThanOrEqualTo(NumberObject a, NumberObject b) { return a.GetValue() <= b.GetValue() ? BoolObject.TRUE : BoolObject.FALSE; }

}
