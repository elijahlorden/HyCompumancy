package me.freznel.compumancy.vm.operators.unary;

import me.freznel.compumancy.vm.objects.BoolObject;
import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;
import me.freznel.compumancy.vm.operators.UnaryOperatorSet;

public class BooleanUnaryOperatorSet extends UnaryOperatorSet<BoolObject> {
    @Override
    public VMObject UnsignedNegate(BoolObject arg) {
        return arg.GetValue() ? BoolObject.TRUE : BoolObject.FALSE;
    }
}
