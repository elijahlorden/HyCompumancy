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
        var key = new OperatorSetKey(left.getClass(), right.getClass());

        if (!Sets.containsKey(key)) throw new InvalidOperationException(String.format("Attempted to %s %s and %s", operator.toString(), left.getClass().getName(), right.getClass().getName()));

        @SuppressWarnings("unchecked")
        BinaryOperatorSet<T, K> set = (BinaryOperatorSet<T, K>) Sets.get(key);

        return switch (operator)
        {
            case Invalid -> throw new InvalidOperationException("Encountered invalid operator reference");
            case Add -> set.Add(left, right);
            case Subtract -> set.Subtract(left, right);
            case Multiply -> set.Multiply(left, right);
            case Divide -> set.Divide(left, right);
            case Mod -> set.Mod(left, right);
        };
    }


    public VMObject Add(T a, K b) { throw new InvalidOperationException(String.format("Attempted to Add %s and %s", a.GetName(), b.GetName())); }
    public VMObject Subtract(T a, K b) { throw new InvalidOperationException(String.format("Attempted to Subtract %s and %s", a.GetName(), b.GetName())); }
    public VMObject Multiply(T a, K b) { throw new InvalidOperationException(String.format("Attempted to Multiply %s and %s", a.GetName(), b.GetName())); }
    public VMObject Divide(T a, K b) { throw new InvalidOperationException(String.format("Attempted to Divide %s and %s", a.GetName(), b.GetName())); }
    public VMObject Mod(T a, K b) { throw new InvalidOperationException(String.format("Attempted to Mod %s and %s", a.GetName(), b.GetName())); }






}
