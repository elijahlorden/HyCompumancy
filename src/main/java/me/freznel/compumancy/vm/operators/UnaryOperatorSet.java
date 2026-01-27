package me.freznel.compumancy.vm.operators;

import com.hypixel.hytale.logger.HytaleLogger;
import me.freznel.compumancy.vm.exceptions.InvalidOperationException;
import me.freznel.compumancy.vm.objects.VMObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class UnaryOperatorSet<T extends VMObject> {
    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();

    private static final Map<Class<? extends VMObject>, UnaryOperatorSet<? extends VMObject>> Sets = new HashMap<>();

    public static <T extends VMObject> void Register(UnaryOperatorSet<T> set, Class<? extends T> cls)
    {
        if (Sets.containsKey(cls))
        {
            Logger.at(Level.SEVERE).log(String.format("Attempted to register duplicate UnaryOperatorSet<%s>", cls.getName()));
            return;
        }
        Sets.put(cls, set);
    }

    public static <T extends VMObject> VMObject Operate(UnaryOperator operator, T arg) throws InvalidOperationException
    {
        var cls = arg.getClass();
        if (!Sets.containsKey(cls)) operator.ThrowInvalidOperation(arg);

        @SuppressWarnings("unchecked")
        UnaryOperatorSet<T> set = (UnaryOperatorSet<T>) Sets.get(cls);

        return switch (operator)
        {
            case Invalid -> throw new InvalidOperationException("Encountered invalid operator reference");
            case Length -> set.Length(arg);
            case SignedNegate -> set.SignedNegate(arg);
            case UnsignedNegate -> set.UnsignedNegate(arg);
        };
    }

    public VMObject Length(T arg) { UnaryOperator.Length.ThrowInvalidOperation(arg); return null; }
    public VMObject SignedNegate(T arg) { UnaryOperator.SignedNegate.ThrowInvalidOperation(arg); return null; }
    public VMObject UnsignedNegate(T arg) { UnaryOperator.UnsignedNegate.ThrowInvalidOperation(arg); return null; }
}
