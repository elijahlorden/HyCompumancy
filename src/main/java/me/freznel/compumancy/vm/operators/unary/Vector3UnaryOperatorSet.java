package me.freznel.compumancy.vm.operators.unary;

import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;
import me.freznel.compumancy.vm.objects.Vector3Object;
import me.freznel.compumancy.vm.operators.UnaryOperatorSet;

public class Vector3UnaryOperatorSet extends UnaryOperatorSet<Vector3Object> {
    @Override
    public VMObject Length(Vector3Object arg) {
        return new NumberObject(arg.Length());
    }

    @Override
    public VMObject SignedNegate(Vector3Object arg) {
        return arg.Negate();
    }
}
