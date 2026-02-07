package me.freznel.compumancy.vm.operators;

import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.objects.UnaryOperatorObject;
import me.freznel.compumancy.vm.objects.VMObject;

public enum UnaryOperator {
    Invalid,
    Length,
    SignedNegate,
    UnsignedNegate;

    public static final UnaryOperator[] Values = UnaryOperator.values();

    public static UnaryOperator fromInt(int i)
    {
        if (i < 0 || i > Values.length - 1) return Invalid;
        return Values[i];
    }

    public final UnaryOperatorObject Instance;

    UnaryOperator() {
        Instance = new UnaryOperatorObject(this);
    }

    public <T extends VMObject> void throwInvalidOperation(T arg)
    {
        String name = arg.getObjectName();
        switch (this) {
            case Invalid -> throw new InvalidOperationException("Encountered an invalid operator");
            case Length -> throw new InvalidOperationException(String.format("Attempted to get the length of a %s", name));
            case SignedNegate -> throw new InvalidOperationException(String.format("Attempted to negate a %s", name));
            case UnsignedNegate -> throw new InvalidOperationException(String.format("Attempted to invert a %s", name));
        }
    }

}
