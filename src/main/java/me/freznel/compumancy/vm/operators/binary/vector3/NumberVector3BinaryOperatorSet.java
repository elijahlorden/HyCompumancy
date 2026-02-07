package me.freznel.compumancy.vm.operators.binary.vector3;

import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;
import me.freznel.compumancy.vm.objects.Vector3Object;
import me.freznel.compumancy.vm.operators.BinaryOperatorSet;

public class NumberVector3BinaryOperatorSet extends BinaryOperatorSet<NumberObject, Vector3Object> {

    @Override
    public VMObject Multiply(NumberObject a, Vector3Object b) {
        return b.multiply(a.getValue());
    }

}
