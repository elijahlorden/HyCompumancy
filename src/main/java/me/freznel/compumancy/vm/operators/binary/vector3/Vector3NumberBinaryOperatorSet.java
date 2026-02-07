package me.freznel.compumancy.vm.operators.binary.vector3;

import me.freznel.compumancy.vm.objects.NumberObject;
import me.freznel.compumancy.vm.objects.VMObject;
import me.freznel.compumancy.vm.objects.Vector3Object;
import me.freznel.compumancy.vm.operators.BinaryOperatorSet;

public class Vector3NumberBinaryOperatorSet extends BinaryOperatorSet<Vector3Object, NumberObject> {

    @Override
    public VMObject Multiply(Vector3Object a, NumberObject b) {
        return a.multiply(b.getValue());
    }

    @Override
    public VMObject Divide(Vector3Object a, NumberObject b) {
        return a.divide(b.getValue());
    }

}
