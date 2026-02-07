package me.freznel.compumancy.vm.operators;

import com.hypixel.hytale.logger.HytaleLogger;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.objects.BoolObject;
import me.freznel.compumancy.vm.objects.NullObject;
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

        if (set == null) {
            if (operator == BinaryOperator.Equal) {
                return BoolObject.FALSE;
            } else if (operator == BinaryOperator.NotEqual) {
                return BoolObject.TRUE;
            } else {
                return operator.ThrowInvalidOperation(left, right);
            }
        }

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

    public VMObject Add(T a, K b) { return BinaryOperator.Add.ThrowInvalidOperation(a, b); }
    public VMObject Subtract(T a, K b) { return BinaryOperator.Subtract.ThrowInvalidOperation(a, b); }
    public VMObject Multiply(T a, K b) { return BinaryOperator.Multiply.ThrowInvalidOperation(a, b); }
    public VMObject Divide(T a, K b) { return BinaryOperator.Divide.ThrowInvalidOperation(a, b); }
    public VMObject Mod(T a, K b) { return BinaryOperator.Mod.ThrowInvalidOperation(a, b); }

    public VMObject And(T a, K b) { return BinaryOperator.And.ThrowInvalidOperation(a, b); }
    public VMObject Nand(T a, K b) { return BinaryOperator.Nand.ThrowInvalidOperation(a, b); }
    public VMObject Or(T a, K b) { return BinaryOperator.Or.ThrowInvalidOperation(a, b); }
    public VMObject Xor(T a, K b) { return BinaryOperator.Xor.ThrowInvalidOperation(a, b); }

    public VMObject Equal(T a, K b) { return BinaryOperator.Equal.ThrowInvalidOperation(a, b); }
    public VMObject NotEqual(T a, K b) { return BinaryOperator.NotEqual.ThrowInvalidOperation(a, b); }
    public VMObject GreaterThan(T a, K b) { return BinaryOperator.GreaterThan.ThrowInvalidOperation(a, b); }
    public VMObject LessThan(T a, K b) { return BinaryOperator.LessThan.ThrowInvalidOperation(a, b); }
    public VMObject GreaterThanOrEqualTo(T a, K b) { return BinaryOperator.GreaterThanOrEqualTo.ThrowInvalidOperation(a, b); }
    public VMObject LessThanOrEqualTo(T a, K b) { return BinaryOperator.LessThanOrEqualTo.ThrowInvalidOperation(a, b); }

}
