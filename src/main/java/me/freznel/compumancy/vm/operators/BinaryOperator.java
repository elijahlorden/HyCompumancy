package me.freznel.compumancy.vm.operators;

public enum BinaryOperator {
    Invalid,
    Add,
    Subtract,
    Multiply,
    Divide,
    Mod;

    public static final BinaryOperator[] Values = BinaryOperator.values();

    public static BinaryOperator FromInt(int i)
    {
        if (i < 0 || i > Values.length - 1) return Invalid;
        return Values[i];
    }


}
