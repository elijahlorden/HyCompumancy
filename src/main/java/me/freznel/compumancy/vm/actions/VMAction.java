package me.freznel.compumancy.vm.actions;

import com.hypixel.hytale.logger.HytaleLogger;
import me.freznel.compumancy.vm.execution.Invocation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public abstract class VMAction {
    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();

    public static final Map<String, VMAction> Actions = new ConcurrentHashMap<>();
    public static final Map<Class<? extends VMAction>, String> Names = new ConcurrentHashMap<>();

    public static void Register(String name, VMAction action) {
        if (Actions.containsKey(name)) {
            Logger.at(Level.SEVERE).log("Attempted to register duplicate action: " + name);
            return;
        }
        Actions.put(name, action);
        Names.put(action.getClass(), name);
    }

    public static VMAction GetAction(String name) {
        if (!Actions.containsKey(name)) return null;
        return Actions.get(name);
    }

    public static String GetName(VMAction action) {
        var cls = action.getClass();
        return GetName(cls);
    }

    public static String GetName(Class<? extends VMAction> cls) {
        if (!Names.containsKey(cls)) return null;
        return Names.get(cls);
    }

    public abstract int ExecutionBudgetCost();

    public abstract void Execute(Invocation invocation);

    public boolean ExecuteSynchronous() { return false; }

}
