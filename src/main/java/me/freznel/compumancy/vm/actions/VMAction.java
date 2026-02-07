package me.freznel.compumancy.vm.actions;

import com.hypixel.hytale.logger.HytaleLogger;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.objects.ActionObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public abstract class VMAction {
    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();

    private static final Map<String, VMAction> Actions = new ConcurrentHashMap<>();
    private static final Map<Class<? extends VMAction>, String> Names = new ConcurrentHashMap<>();
    private static final Map<String, ActionObject> objects = new ConcurrentHashMap<>();

    public static void register(String name, VMAction action) {
        if (Actions.containsKey(name)) {
            Logger.at(Level.SEVERE).log("Attempted to register duplicate action: " + name);
            return;
        }
        Actions.put(name, action);
        Names.put(action.getClass(), name);
    }

    public static VMAction getAction(String name) {
        return Actions.get(name);
    }

    public static String getName(VMAction action) {
        var cls = action.getClass();
        return getName(cls);
    }

    public static String getName(Class<? extends VMAction> cls) {
        return Names.get(cls);
    }

    public static ActionObject getObject(String name) {
        if (name == null) return null;
        ActionObject obj = objects.get(name);
        if (obj != null) return obj;
        VMAction action = Actions.get(name);
        if (action != null) {
            var ins = new ActionObject(action, name);
            objects.put(name, ins);
            return ins;
        } else {
            Logger.at(Level.SEVERE).log(String.format("Created ActionObject from unregistered VMAction %s", name));
            return new ActionObject(null, name);
        }
    }

    public static ActionObject getObject(Class<? extends VMAction> cls) {
        String name = Names.get(cls);
        if (name == null) throw new IllegalArgumentException(String.format("Attempted to call VMAction.GetObject() with unregistered action class '%s'", cls.getName()));
        return getObject(name);
    }

    public abstract int executionBudgetCost();

    public abstract void execute(Invocation invocation);

    public boolean isExecuteSynchronous() { return false; }

}
