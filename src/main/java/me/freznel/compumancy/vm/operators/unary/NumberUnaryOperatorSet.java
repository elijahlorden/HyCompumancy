package me.freznel.compumancy.vm.operators.unary;

import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;
import me.freznel.compumancy.vm.operators.UnaryOperatorSet;

public class NumberUnaryOperatorSet extends UnaryOperatorSet<NumberObject> {
    @Override
    public VMObject Length(NumberObject arg) {
        return new NumberObject(Math.abs(arg.getValue()));
    }

    @Override
    public VMObject SignedNegate(NumberObject arg) {
        return new NumberObject(-arg.getValue());
    }

    @Override
    public VMObject UnsignedNegate(NumberObject arg) {
        return new NumberObject((~((long)arg.getValue())) >> 12);
    }
}
