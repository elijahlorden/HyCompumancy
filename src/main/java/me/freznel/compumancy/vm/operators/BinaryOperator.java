package me.freznel.compumancy.vm.operators;

import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
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
    Xor;

    public static final BinaryOperator[] Values = BinaryOperator.values();

    public static BinaryOperator FromInt(int i)
    {
        if (i < 0 || i > Values.length - 1) return Invalid;
        return Values[i];
    }

    public <T extends VMObject, K extends VMObject> void  StandardException(String s, T left, K right) { throw new InvalidOperationException(String.format("Attempted to %s %s and %s", s, left.GetObjectName(), right.GetObjectName())); }

    public <T extends VMObject, K extends VMObject> void ThrowInvalidOperation(T left, K right)
    {
        switch (this) {
            case Invalid -> throw new InvalidOperationException("Encountered an invalid operator");
            default -> StandardException(this.toString(), left, right);
        }
    }


}
