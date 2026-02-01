package me.freznel.compumancy.vm.operators;

import com.hypixel.hytale.logger.HytaleLogger;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.objects.VMObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class BinaryOperatorSet<T extends VMObject, K extends VMObject> {
    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();

    private static final Map<OperatorSetKey, BinaryOperatorSet<? extends VMObject, ? extends VMObject>> Sets = new HashMap<>();

    public record OperatorSetKey(Class<? extends VMObject> left, Class<? extends VMObject> right) { }

    public static <T extends VMObject, K extends VMObject> void Register(BinaryOperatorSet<T,K> set, Class<? extends T> left, Class<? extends K> right)
    {
        var key = new OperatorSetKey(left, right);
        if (Sets.containsKey(key))
        {
            Logger.at(Level.SEVERE).log(String.format("Attempted to register duplicate BinaryOperatorSet<%s,%s>", left.getName(), right.getName()));
            return;
        }
        Sets.put(key, set);
    }

    public static <T extends VMObject, K extends VMObject> VMObject Operate(BinaryOperator operator, T left, K right) throws InvalidOperationException
    {
        @SuppressWarnings("unchecked")
        BinaryOperatorSet<T, K> set = (BinaryOperatorSet<T, K>) Sets.get(new OperatorSetKey(left.getClass(), right.getClass()));
        if (set == null) throw new InvalidOperationException(String.format("Attempted to %s %s and %s", operator.toString(), left.getClass().getName(), right.getClass().getName()));

        return switch (operator)
        {
            case Invalid -> throw new InvalidOperationException("Encountered invalid operator reference");
            case Add -> set.Add(left, right);
            case Subtract -> set.Subtract(left, right);
            case Multiply -> set.Multiply(left, right);
            case Divide -> set.Divide(left, right);
            case Mod -> set.Mod(left, right);
            case And -> set.And(left, right);
            case Nand -> set.Nand(left, right);
            case Or -> set.Or(left, right);
            case Xor -> set.Xor(left, right);
            case Equal -> set.Equal(left, right);
            case NotEqual  -> set.NotEqual(left, right);
            case GreaterThan  -> set.GreaterThan(left, right);
            case LessThan  -> set.LessThan(left, right);
            case GreaterThanOrEqualTo  -> set.GreaterThanOrEqualTo(left, right);
            case LessThanOrEqualTo  -> set.LessThanOrEqualTo(left, right);
        };
    }


    public VMObject Add(T a, K b) { BinaryOperator.Add.ThrowInvalidOperation(a, b); return null; }
    public VMObject Subtract(T a, K b) { BinaryOperator.Subtract.ThrowInvalidOperation(a, b); return null; }
    public VMObject Multiply(T a, K b) { BinaryOperator.Multiply.ThrowInvalidOperation(a, b); return null; }
    public VMObject Divide(T a, K b) { BinaryOperator.Divide.ThrowInvalidOperation(a, b); return null; }
    public VMObject Mod(T a, K b) { BinaryOperator.Mod.ThrowInvalidOperation(a, b); return null; }

    public VMObject And(T a, K b) { BinaryOperator.And.ThrowInvalidOperation(a, b); return null; }
    public VMObject Nand(T a, K b) { BinaryOperator.Nand.ThrowInvalidOperation(a, b); return null; }
    public VMObject Or(T a, K b) { BinaryOperator.Or.ThrowInvalidOperation(a, b); return null; }
    public VMObject Xor(T a, K b) { BinaryOperator.Xor.ThrowInvalidOperation(a, b); return null; }

    public VMObject Equal(T a, K b) { BinaryOperator.Equal.ThrowInvalidOperation(a, b); return null; }
    public VMObject NotEqual(T a, K b) { BinaryOperator.NotEqual.ThrowInvalidOperation(a, b); return null; }
    public VMObject GreaterThan(T a, K b) { BinaryOperator.GreaterThan.ThrowInvalidOperation(a, b); return null; }
    public VMObject LessThan(T a, K b) { BinaryOperator.LessThan.ThrowInvalidOperation(a, b); return null; }
    public VMObject GreaterThanOrEqualTo(T a, K b) { BinaryOperator.GreaterThanOrEqualTo.ThrowInvalidOperation(a, b); return null; }
    public VMObject LessThanOrEqualTo(T a, K b) { BinaryOperator.LessThanOrEqualTo.ThrowInvalidOperation(a, b); return null; }

}
