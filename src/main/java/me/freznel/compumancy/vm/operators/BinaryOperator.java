package me.freznel.compumancy.vm.operators;

import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.objects.BinaryOperatorObject;
import me.freznel.compumancy.vm.objects.VMObject;

public enum BinaryOperator {
    Invalid,
    Add,
    Subtract,
    Multiply,
    Divide,
    Mod,
    And,
    Nand,
    Or,
    Xor,
    Equal,
    NotEqual,
    GreaterThan,
    LessThan,
    GreaterThanOrEqualTo,
    LessThanOrEqualTo;

    public static final BinaryOperator[] Values = BinaryOperator.values();

    public static BinaryOperator fromInt(int i)
    {
        if (i < 0 || i > Values.length - 1) return Invalid;
        return Values[i];
    }

    public final BinaryOperatorObject Instance;

    BinaryOperator() {
        Instance = new BinaryOperatorObject(this);
    }

    public <T extends VMObject, K extends VMObject> VMObject standardException(String s, T left, K right) { throw new InvalidOperationException(String.format("Attempted to %s %s and %s", s, left.getObjectName(), right.getObjectName())); }
    public <T extends VMObject, K extends VMObject> VMObject compareException(T left, K right) { throw new InvalidOperationException(String.format("Attempted to compare %s and %s", left.getObjectName(), right.getObjectName())); }

    @SuppressWarnings("UnusedReturnValue")
    public <T extends VMObject, K extends VMObject> VMObject throwInvalidOperation(T left, K right)
    {
        return switch (this) {
            case Invalid -> throw new InvalidOperationException("Encountered an invalid operator");
            case Equal, NotEqual, GreaterThan, LessThan, GreaterThanOrEqualTo, LessThanOrEqualTo -> compareException(left, right);
            default -> standardException(this.toString(), left, right);
        };
    }


}
