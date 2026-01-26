package me.freznel.compumancy.vm.objects;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import me.freznel.compumancy.vm.execution.Invocation;
import me.freznel.compumancy.vm.actions.VMAction;
import me.freznel.compumancy.vm.exceptions.InvalidActionException;
import me.freznel.compumancy.vm.exceptions.VMException;
import me.freznel.compumancy.vm.interfaces.IExecutable;

import java.util.logging.Level;

public class ActionObject extends VMObject implements IExecutable {
    private static final HytaleLogger Logger = HytaleLogger.forEnclosingClass();
    public static final BuilderCodec<ActionObject> CODEC = BuilderCodec.builder(ActionObject.class, ActionObject::new)
            .append(new KeyedCodec<>("Ref", Codec.STRING), ActionObject::SetActionName, ActionObject::GetActionName)
            .add()
            .build();

    static {
        VMObject.CODEC.register("ACT", ActionObject.class, CODEC);
    }

    private String actionName;
    private VMAction Ref;

    public ActionObject() { }
    public ActionObject(VMAction action) { SetActionRef(action); }
    public ActionObject(String actionName) { SetActionName(actionName); }
    public ActionObject(Class<? extends VMAction> cls) {
        actionName = VMAction.GetName(cls);
        if (actionName == null) {
            actionName = cls.getName();
            Logger.at(Level.SEVERE).log(String.format("Created ActionObject from unregistered VMAction %s", actionName));
            return;
        }
        Ref = VMAction.GetAction(actionName);
    }
    protected ActionObject(VMAction ref, String actionName) { Ref = ref; this.actionName = actionName; }

    public VMAction GetActionRef() { return Ref; }
    public void SetActionRef(VMAction action) { Ref = action; actionName = VMAction.GetName(action); }

    public String GetActionName() { return actionName; }
    public void SetActionName(String actionName) { Ref = VMAction.GetAction(actionName); this.actionName = actionName; }

    @Override
    public String GetName() { return "Action"; }

    @Override
    public String toString() { return (actionName != null && !actionName.isEmpty()) ? "Action: " + actionName : "Action"; }

    @Override
    public int GetSize() { return 1; }

    @Override
    public VMObject clone() {
        return new ActionObject(Ref, actionName);
    }

    @Override
    public int ExecutionBudgetCost() {
        return Ref == null ? 1 : Ref.ExecutionBudgetCost();
    }

    @Override
    public void Execute(Invocation invocation) throws VMException {
        if (Ref == null) throw new InvalidActionException(String.format("The action '%s' was not found", actionName));
        Ref.Execute(invocation);
    }
}
