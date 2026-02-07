package me.freznel.compumancy.vm.operators.binary.vector3;

import me.freznel.compumancy.vm.objects.BoolObject;
import me.freznel.compumancy.vm.objects.VMObject;
import me.freznel.compumancy.vm.objects.Vector3Object;
import me.freznel.compumancy.vm.operators.BinaryOperatorSet;

public class Vector3Vector3BinaryOperatorSet extends BinaryOperatorSet<Vector3Object, Vector3Object> {

    @Override
    public VMObject Add(Vector3Object a, Vector3Object b) { return a.Add(b); }
    @Override
    public VMObject Subtract(Vector3Object a, Vector3Object b) { return a.Subtract(b); }

    @Override
    public VMObject Equal(Vector3Object a, Vector3Object b) { return a.Equal(b) ? BoolObject.TRUE : BoolObject.FALSE; }
    @Override
    public VMObject NotEqual(Vector3Object a, Vector3Object b) { return a.Equal(b) ? BoolObject.FALSE : BoolObject.TRUE; }

}
