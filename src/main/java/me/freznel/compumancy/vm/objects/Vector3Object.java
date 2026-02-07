package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.math.vector.Vector3d;
import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.interfaces.IEvaluatable;

import javax.annotation.Nonnull;

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

    //Vector math

    public boolean Equal(Vector3Object other) { return x == other.x && y == other.y && z == other.z; }

    @Nonnull
    public Vector3Object Normalize() {
        double length = this.Length();
        return new Vector3Object(x / length, y / length, z / length);
    }

    public double Length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double SquaredLength() {
        return x * x + y * y + z * z;
    }

    @Nonnull
    public Vector3Object SetLength(double newLen) {
        double factor = newLen / this.Length();
        return new Vector3Object(x * factor, y * factor, z * factor);
    }

    public Vector3Object Negate() { return new Vector3Object(-x, -y, -z); }

    public double Dot(Vector3Object other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector3Object Cross(Vector3Object other) {
        return new Vector3Object(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    public Vector3Object Add(Vector3Object other) { return new Vector3Object(x + other.x, y + other.y, z + other.z); }
    public Vector3Object Subtract(Vector3Object other) { return new Vector3Object(x - other.x, y - other.y, z - other.z); }
    public Vector3Object Multiply(double d) { return new Vector3Object(x * d, y * d, z * d); }
    public Vector3Object Divide(double d) { return new Vector3Object(x / d, y / d, z / d); }

    public Vector3Object RotateByAxisAngle(double u, double v, double w, double theta) {
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        return new Vector3Object(
                u*(u*x + v*y + w*z)*(1d - cosTheta) + x*cosTheta + (-w*y + v*z)*sinTheta,
                v*(u*x + v*y + w*z)*(1d - cosTheta) + y*cosTheta + (w*x - u*z)*sinTheta,
                w*(u*x + v*y + w*z)*(1d - cosTheta) + z*cosTheta + (-v*x + u*y)*sinTheta
        );
    }

    public Vector3Object ProjectToPlane(Vector3Object normal) {
        var proj = normal.Multiply(this.Dot(normal) / normal.Dot(normal));
        return Subtract(proj);
    }




}
