package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3d;
import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;

public final class Vector3Object extends VMObject implements IEvaluatable {
    public static final Vector3Object ZERO = new Vector3Object(0,0,0);
    public static final Vector3Object AXIS_X = new Vector3Object(1,0,0);
    public static final Vector3Object AXIS_Y = new Vector3Object(0,1,0);
    public static final Vector3Object AXIS_Z = new Vector3Object(0,0,1);

    public static final BuilderCodec<Vector3Object> CODEC = BuilderCodec.builder(Vector3Object.class, Vector3Object::new)
            .append(new KeyedCodec<>("X", Codec.DOUBLE), (o, v) -> o.x = v == null ? 0 : v, o -> o.x)
            .add()
            .append(new KeyedCodec<>("Y", Codec.DOUBLE), (o, v) -> o.y = v == null ? 0 : v, o -> o.y)
            .add()
            .append(new KeyedCodec<>("Z", Codec.DOUBLE), (o, v) -> o.z = v == null ? 0 : v, o -> o.z)
            .add()
            .build();

    private double x, y, z;

    public Vector3Object() { x = 0; y = 0; z = 0; }
    public Vector3Object(Vector3d vector) { x = vector.x; y = vector.y; z = vector.z; }
    public Vector3Object(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    public Vector3Object(Vector3Object other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public Vector3d GetVector3d() { return new Vector3d(x, y, z); }

    public double GetX() {
        return x;
    }

    public double GetY() {
        return y;
    }

    public double GetZ() {
        return z;
    }

    @Override
    public String GetObjectName() {
        return "Vector3";
    }

    @Override
    public int GetObjectSize() {
        return 3;
    }

    @Override
    public String toString() {
        return String.format("<%.2f, %.2f, %.2f>", x, y, z);
    }

    @Override
    public VMObject clone() {
        return this; //Immutable object
    }

    @Override
    public int ExecutionBudgetCost() {
        return 1;
    }

    @Override
    public void Evaluate(Invocation invocation) throws VMException {
        invocation.Push(this); //Immutable object
    }

    @Override
    public boolean IsEvalSynchronous() {
        return false;
    }
}
